@role:hive /tmp/post-install.sh
#!/bin/sh

#
# Copyright (c) 2011 the original author or authors.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

#
# hive_post-install.sh
# 
# @author Timo Saarinen
#

# Install java connector (in addition to installing the rpm)
echo "Link mysql-connector-java"
if [ ! -f /usr/lib/hive/lib/mysql-connector-java.jar ] ; then
    ln -s /usr/share/java/mysql-connector-java.jar /usr/lib/hive/lib/mysql-connector-java.jar || exit 1
fi

# Set MariaDB password
echo "Set MariaDB root password"
service mysql start || exit 1
mysqladmin -u root password [[HIVE_DATABASE_ROOT_PASSWORD]] || exit 1

# Make MariaDB start at boot
echo "Make MariaDB start at boot"
/sbin/chkconfig mysql on || exit 1

# Create Hive Metastore
echo "Create Hive Metastore"
mysql -u root --password=[[HIVE_DATABASE_ROOT_PASSWORD]] << EOF
CREATE DATABASE metastore;
USE metastore;
SOURCE /usr/lib/hive/scripts/metastore/upgrade/mysql/hive-schema-0.10.0.mysql.sql;

CREATE USER '[[HIVE_DATABASE_USER]]'@'localhost' IDENTIFIED BY '[[HIVE_DATABASE_USER_PASSWORD]]';
REVOKE ALL PRIVILEGES, GRANT OPTION FROM '[[HIVE_DATABASE_USER]]'@'localhost';
GRANT SELECT,INSERT,UPDATE,DELETE,LOCK TABLES,EXECUTE ON metastore.* TO '[[HIVE_DATABASE_USER]]'@'[[METASTORE_HOSTNAME]]';
GRANT SELECT,INSERT,UPDATE,DELETE,LOCK TABLES,EXECUTE ON metastore.* TO '[[HIVE_DATABASE_USER]]'@'localhost';
FLUSH PRIVILEGES;
EOF
if [ $? -ne 0 ] ; then
    exit 1
fi

# Stop MariaDB (because this script is run before service start phase)
service mysql stop

# Make needed HDFS directories
echo "Creating Hive directories"
su - hdfs -s /bin/bash -c "hdfs dfs -mkdir [[HIVE_METASTORE_WAREHOUSE_DIR]]"
su - hdfs -s /bin/bash -c "hdfs dfs -chmod 1777 [[HIVE_METASTORE_WAREHOUSE_DIR]]" || exit 1
su - hdfs -s /bin/bash -c "hdfs dfs -chown hive [[HIVE_METASTORE_WAREHOUSE_DIR]]" || exit 1
su - hdfs -s /bin/bash -c "hdfs dfs -chgrp hadoop [[HIVE_METASTORE_WAREHOUSE_DIR]]"

# General tmp directory (needed by Hive Metastore Server)
mkdir -p [[TMP_DIR]]
chmod 1777 [[TMP_DIR]]


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

# Hive-metaserver have to be started as hdfs user instead of hive (workaround)
# https://groups.google.com/a/cloudera.org/forum/#!msg/cdh-user/Zs4X2AcMqRQ/QCpn5VsCskAJ
sed -i 's/SVC_USER="hive"/SVC_USER="hdfs"/g' /etc/init.d/hive-server2 
sed -i 's/-m 0755/-m 0775"/g' /etc/init.d/hive-server2
usermod -G hive hdfs
chgrp -R hdfs /var/run/hive && chmod -R g+w /var/run/hive
chgrp -R hdfs /var/log/hive/* && chmod -R g+w /var/log/hive/*
chgrp -R hdfs /var/log/hive/ && chmod -R g+w /var/log/hive/

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
su - hdfs -s /bin/bash -c "hadoop fs -mkdir [[HIVE_METASTORE_WAREHOUSE_DIR]]"
su - hdfs -s /bin/bash -c "hadoop fs -chmod 1777 [[HIVE_METASTORE_WAREHOUSE_DIR]]" || exit 1
su - hdfs -s /bin/bash -c "hadoop fs -chown hive [[HIVE_METASTORE_WAREHOUSE_DIR]]" || exit 1
su - hdfs -s /bin/bash -c "hadoop fs -chgrp hadoop [[HIVE_METASTORE_WAREHOUSE_DIR]]" || exit 1
su - hdfs -s /bin/bash -c "hadoop fs -chmod -R 1777 /tmp" || exit 1
su - hdfs -s /bin/bash -c "hadoop fs -chmod -R 1777 [[TMP_DIR]]" || exit 1

# This may look ugly, but is probably the only way to fix the following 
# map/reduce problem
# java.io.FileNotFoundException: File does not exist: hdfs://hbase7:8020/usr/lib/hive/lib/hive-builtins-0.10.0-cdh4.4.0.jar
su - hdfs -c "hadoop fs -mkdir /usr/lib/hive/lib" || exit 1
su - hdfs -c "hadoop fs -put `find /usr/lib/hive/lib/ -name "hive-builtin*jar"` /usr/lib/hive/lib/"


# General tmp directory (needed by Hive Metastore Server)
mkdir -p [[TMP_DIR]]
chmod 1777 [[TMP_DIR]]


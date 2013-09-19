@role:hive /tmp/cleanup.sh
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
# hive_cleanup.sh
# 
# shortcut: yum -y erase mysql-connector-java MariaDB-shared MariaDB-server && rm -fR /var/lib/mysql /etc/hive /usr/lib/hive/lib/mysql-connector-java.jar
#
# @author Timo Saarinen
#

# Delete rpm etc backups
find /etc -name "*.rpmsave" | xargs rm -fR

# Purge MariaDB data dir
rm -fR /var/lib/mysql

# Delete all Hive configuration files
rm -fR /etc/hive

# Remove the connector symbolic link
rm -f /usr/lib/hive/lib/mysql-connector-java.jar

# The common package have to be removed too, to ensure full cleanup 
# (and recovery in case of re-install)
yum -y erase MariaDB-common hive-jdbc


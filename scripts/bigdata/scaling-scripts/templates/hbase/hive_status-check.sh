@role:hive /tmp/status-check.sh
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
# hive_status-check.sh
# 
# @author Timo Saarinen
#

# Hive MetaStore connection test
echo "Test connectivity to the metastore"
hive -e "show tables;" &>/dev/stdout | grep OK &>/dev/null || exit 1

# HiveServer2 command line connectiont test
echo "Testing HiveServer2"
beeline -u jdbc:hive2://localhost:10000 -n [[HIVE_DATABASE_USER]] -p [[HIVE_DATABASE_USER_PASSWORD]] -d org.apache.hive.jdbc.HiveDriver -e "show tables" &>/dev/stdout | grep "Connected to" >/dev/null
if [ $? -ne 0 ] ; then
    echo "HiveServer2 test failed" >/dev/stderr
    exit 1
else
    echo "HiveServer2 test succeeded" >/dev/stderr
fi

# To allow Hive work with HBase, the following lines should be added in the 
# beginning of each script:
#  ADD JAR /usr/lib/hive/lib/zookeeper.jar;
#  ADD JAR /usr/lib/hive/lib/hbase.jar;
#  ADD JAR /usr/lib/hive/lib/hive-hbase-handler-0.10.0-cdh4.2.0.jar
#  ADD JAR /usr/lib/hive/lib/guava-11.0.2.jar;

# To use Pig with HBase, the following statement has to be on top of each script
#  register /usr/lib/zookeeper/zookeeper-3.4.5-cdh4.2.0.jar
#  register /usr/lib/hbase/hbase-0.94.2-cdh4.2.0-security.jar


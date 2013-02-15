@role:slave /tmp/post-install.sh
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
# slave_post-install.sh
# 
# @author Timo Saarinen
# @author Tommi Siitonen
# @version 1.0.0
# @since 1.0.0
#

echo "Creating directories"

mkdir -p /data/1/dfs/dn /data/2/dfs/dn /data/3/dfs/dn ; mkdir -p /data/1/dfs/dn /data/2/dfs/dn /data/3/dfs/dn 
chown -R hdfs:hadoop /data/1/dfs/dn /data/2/dfs/dn /data/3/dfs/dn

mkdir -p /data/1/mapred/local /data/2/mapred/local /data/3/mapred/local /data/4/mapred/local; 
chown -R mapred:hadoop /data/1/mapred/local /data/2/mapred/local /data/3/mapred/local /data/4/mapred/local

#echo "Formatting HDFS"
#su - hdfs -c 'yes Y | /usr/bin/hadoop --config /etc/hadoop/conf namenode -format' 2>/dev/stdout

echo "Creating SSH keys for hbase and hdfs users"
mkdir /var/run/hbase/.ssh
chown hbase /var/run/hbase/.ssh 
chmod 0700 /var/run/hbase/.ssh
rm -f /var/run/hbase/.ssh/id_rsa*
su - hbase -s /bin/bash -c "ssh-keygen -q -t rsa -f /var/run/hbase/.ssh/id_rsa -N \"\""
mkdir /usr/lib/hadoop-0.20/.ssh
chown hdfs /usr/lib/hadoop-0.20/.ssh
chmod 0700 /usr/lib/hadoop-0.20/.ssh
rm -f /usr/lib/hadoop-0.20/.ssh/id_rsa*
su - hdfs -s /bin/bash -c "ssh-keygen -q -t rsa -f /usr/lib/hadoop-0.20/.ssh/id_rsa -N \"\""


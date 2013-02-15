@role:hmaster /tmp/post-install.sh
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
# hmaster_post-install.py
# 
# @author Timo Saarinen
# @author Tommi Siitonen
# @version 1.0.0
# @since 1.0.0
#

echo "Creating directories"

mkdir -p /data/1/dfs/nn /data/2/dfs/nn; mkdir -p /data/1/dfs/dn /data/2/dfs/dn /data/3/dfs/dn /data/4/dfs/dn; 
chown -R hdfs:hadoop /data/1/dfs/nn /data/2/dfs/nn /data/1/dfs/dn /data/2/dfs/dn /data/3/dfs/dn /data/4/dfs/dn; 

mkdir -p /data/1/mapred/local /data/2/mapred/local /data/3/mapred/local /data/4/mapred/local
chown -R mapred:hadoop /data/1/mapred /data/2/mapred/local /data/3/mapred /data/4/mapred

# mkdir -p /app/hadoop; mkdir /app/hadoop/tmp; chmod 775 -R /app; chown -R hdfs.hadoop /app

echo "Formatting HDFS"
su - hdfs -c 'yes Y | /usr/bin/hadoop --config /etc/hadoop/conf namenode -format' 2>/dev/stdout || exit 1

echo "Creating HDFS directories"
/etc/init.d/hadoop-0.20-namenode restart || exit 1
/etc/init.d/hadoop-0.20-namenode status | grep running
if [ $? -eq 1 ] ; then
    echo "Hadoop name node didn't start: `/etc/init.d/hadoop-0.20-namenode status`" &>/dev/stderr
    exit 1
fi
su - hdfs -s /bin/bash -c 'hadoop fs -mkdir /hbase' 
su - hdfs -s /bin/bash -c 'hadoop fs -chmod -R 775 /hbase' || exit 1
su - hdfs -s /bin/bash -c 'hadoop fs -chown -R hbase /hbase' || exit 1
su - hdfs -s /bin/bash -c 'hadoop fs -mkdir -p /app/hadoop/tmp'
su - hdfs -s /bin/bash -c 'hadoop fs -chmod -R 775 /app' || exit 1
su - hdfs -s /bin/bash -c 'hadoop fs -chown -R hdfs /app' || exit 1
su - hdfs -s /bin/bash -c 'hadoop fs -mkdir -p /mapred/system'
su - hdfs -s /bin/bash -c 'hadoop fs -chmod -R 775 /mapred/system' || exit 1
su - hdfs -s /bin/bash -c 'hadoop fs -chown -R mapred /mapred/system' || exit 1
su - hdfs -s /bin/bash -c 'hadoop fs -mkdir -p /user/hbase'
su - hdfs -s /bin/bash -c 'hadoop fs -chmod -R 775 /user/hbase' || exit 1
su - hdfs -s /bin/bash -c 'hadoop fs -chown -R hbase /user/hbase' || exit 1
/etc/init.d/hadoop-0.20-namenode stop || exit 1
/etc/init.d/hadoop-0.20-namenode status | grep stopped
if [ $? -eq 1 ] ; then
    echo "Hadoop name node didn't stop: `/etc/init.d/hadoop-0.20-namenode status`" &>/dev/stderr
    exit 1
fi

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


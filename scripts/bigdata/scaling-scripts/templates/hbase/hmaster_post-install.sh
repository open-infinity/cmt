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

export DATADIR=[[DATABASE_DIR]]

echo "Creating log directories"
groupmems -g hadoop -a hbase
mkdir -p [[LOG_DIR]]
chown hdfs [[LOG_DIR]]
chown hdfs [[LOG_DIR]]
chmod g+rwx [[LOG_DIR]]

mkdir [[LOG_DIR]]/hbase
chown hdfs [[LOG_DIR]]/hbase
chgrp hadoop [[LOG_DIR]]/hbase
chmod 0775 [[LOG_DIR]]/hbase
rm -fR /var/log/hbase
ln -s [[LOG_DIR]]/hbase /var/log/hbase

mkdir [[LOG_DIR]]/hadoop-0.20-mapreduce
chown mapred [[LOG_DIR]]/hadoop-0.20-mapreduce
chgrp hadoop [[LOG_DIR]]/hadoop-0.20-mapreduce
chmod 0775 [[LOG_DIR]]/hadoop-0.20-mapreduce
rm -fR /var/log/hadoop-0.20-mapreduce
ln -s [[LOG_DIR]]/hadoop-0.20-mapreduce /var/log/hadoop-0.20-mapreduce

mkdir [[LOG_DIR]]/hadoop-hdfs
chown hdfs [[LOG_DIR]]/hadoop-hdfs
chgrp hadoop [[LOG_DIR]]/hadoop-hdfs
chmod 0775 [[LOG_DIR]]/hadoop-hdfs
rm -fR /var/log/hadoop-hdfs
ln -s [[LOG_DIR]]/hadoop-hdfs /var/log/hadoop-hdfs

echo "Creating data directories"
mkdir -p $DATADIR/1/dfs/nn $DATADIR/2/dfs/nn; mkdir -p $DATADIR/1/dfs/dn $DATADIR/2/dfs/dn $DATADIR/3/dfs/dn $DATADIR/4/dfs/dn; 
chown -R hdfs:hadoop $DATADIR/1/dfs/nn $DATADIR/2/dfs/nn $DATADIR/1/dfs/dn $DATADIR/2/dfs/dn $DATADIR/3/dfs/dn $DATADIR/4/dfs/dn; 

mkdir -p $DATADIR/1/mapred/local $DATADIR/2/mapred/local $DATADIR/3/mapred/local $DATADIR/4/mapred/local
chown -R mapred:hadoop $DATADIR/1/mapred $DATADIR/2/mapred/local $DATADIR/3/mapred $DATADIR/4/mapred

# mkdir -p /app/hadoop; mkdir /app/hadoop/tmp; chmod 775 -R /app; chown -R hdfs.hadoop /app

echo "Ensuring that dfs-hosts-exclude exists"
touch /etc/hadoop/conf/dfs-hosts-exclude || exit 1

echo "Formatting HDFS"
su - hdfs -c 'yes Y | hdfs --config /etc/hadoop/conf namenode -format' 2>/dev/stdout || exit 1

echo "Creating HDFS directories"
service hadoop-hdfs-namenode restart || exit 1
echo "Starting Namenode: `service hadoop-hdfs-namenode status`"
service hadoop-hdfs-namenode status | grep "is running"
if [ $? -eq 1 ] ; then
    echo "Hadoop name node didn't start: `service hadoop-hdfs-namenode status`" &>/dev/stderr
    exit 1
fi

if [ "[[CLUSTER_TYPE]]" == "hbase" ] ; then
    su - hdfs -s /bin/bash -c 'hadoop fs -mkdir /hbase' 
    su - hdfs -s /bin/bash -c 'hadoop fs -chmod -R 775 /hbase' || exit 1
    su - hdfs -s /bin/bash -c 'hadoop fs -chown -R hbase /hbase' || exit 1
fi

su - hdfs -s /bin/bash -c 'hadoop fs -mkdir -p /app/hadoop/tmp'
su - hdfs -s /bin/bash -c 'hadoop fs -chmod -R 775 /app' || exit 1
su - hdfs -s /bin/bash -c 'hadoop fs -chown -R hdfs /app' || exit 1

su - hdfs -s /bin/bash -c 'hadoop fs -mkdir -p /mapred/system'
su - hdfs -s /bin/bash -c 'hadoop fs -chmod -R 775 /mapred/system' || exit 1
su - hdfs -s /bin/bash -c 'hadoop fs -chown -R mapred /mapred/system' || exit 1

if [ "[[CLUSTER_TYPE]]" == "hbase" ] ; then
    su - hdfs -s /bin/bash -c 'hadoop fs -mkdir -p /user/hbase'
    su - hdfs -s /bin/bash -c 'hadoop fs -chmod -R 775 /user/hbase' || exit 1
    su - hdfs -s /bin/bash -c 'hadoop fs -chown -R hbase /user/hbase' || exit 1
fi

su - hdfs -s /bin/bash -c 'hadoop fs -mkdir -p [[TMP_DIR]]'
su - hdfs -s /bin/bash -c 'hadoop fs -chmod -R 1777 [[TMP_DIR]]' || exit 1
su - hdfs -s /bin/bash -c 'hadoop fs -chown -R hdfs:hadoop [[TMP_DIR]]' || exit 1

mkdir -p [[TMP_DIR]]
chown hdfs:hadoop [[TMP_DIR]]
chmod 775 [[TMP_DIR]]

service hadoop-hdfs-secondarynamenode stop
service hadoop-hdfs-namenode stop || exit 1
echo "Starting Namenode: `service hadoop-hdfs-namenode status`"
service hadoop-hdfs-namenode status | grep "is not running"
if [ $? -eq 1 ] ; then
    echo "Hadoop name node didn't stop: `service hadoop-hdfs-namenode status`" &>/dev/stderr
    exit 1
fi

echo "Creating SSH keys for hbase and hdfs users"

if [ "[[CLUSTER_TYPE]]" == "hbase" ] ; then
    HBASE_HOMEDIR=`egrep "^hbase:" /etc/passwd | cut -d':' -f6`
    mkdir $HBASE_HOMEDIR/.ssh
    chown hbase $HBASE_HOMEDIR/.ssh 
    chmod 0700 $HBASE_HOMEDIR/.ssh
    rm -f $HBASE_HOMEDIR/.ssh/id_rsa*
    su - hbase -s /bin/bash -c "ssh-keygen -q -t rsa -f /var/run/hbase/.ssh/id_rsa -N \"\""
fi

HDFS_HOMEDIR=`egrep "^hdfs:" /etc/passwd | cut -d':' -f6`
mkdir $HDFS_HOMEDIR/.ssh
chown hdfs $HDFS_HOMEDIR/.ssh
chmod 0700 $HDFS_HOMEDIR/.ssh
rm -f $HDFS_HOMEDIR/.ssh/id_rsa*
su - hdfs -s /bin/bash -c "ssh-keygen -q -t rsa -f $HDFS_HOMEDIR/.ssh/id_rsa -N \"\""


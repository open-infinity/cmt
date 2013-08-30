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

echo "Creating local directories"
export DATADIR=[[DATABASE_DIR]]

mkdir -p $DATADIR/1/dfs/dn $DATADIR/2/dfs/dn $DATADIR/3/dfs/dn ; mkdir -p $DATADIR/1/dfs/dn $DATADIR/2/dfs/dn $DATADIR/3/dfs/dn 
chown -R hdfs:hadoop $DATADIR/1/dfs/dn $DATADIR/2/dfs/dn $DATADIR/3/dfs/dn

mkdir -p $DATADIR/1/mapred/local $DATADIR/2/mapred/local $DATADIR/3/mapred/local $DATADIR/4/mapred/local; 
chown -R mapred:hadoop $DATADIR/1/mapred/local $DATADIR/2/mapred/local $DATADIR/3/mapred/local $DATADIR/4/mapred/local

#echo "Formatting HDFS"
#su - hdfs -c 'yes Y | /usr/bin/hadoop --config /etc/hadoop/conf namenode -format' 2>/dev/stdout

echo "Creating SSH keys for hbase and hdfs users"

HBASE_HOMEDIR=`egrep "^hbase:" /etc/passwd | cut -d':' -f6`
mkdir $HBASE_HOMEDIR/.ssh
chown hbase $HBASE_HOMEDIR/.ssh 
chmod 0700 $HBASE_HOMEDIR/.ssh
rm -f $HBASE_HOMEDIR/.ssh/id_rsa*
su - hbase -s /bin/bash -c "ssh-keygen -q -t rsa -f /var/run/hbase/.ssh/id_rsa -N \"\""

HDFS_HOMEDIR=`egrep "^hdfs:" /etc/passwd | cut -d':' -f6`
mkdir $HDFS_HOMEDIR/.ssh
chown hdfs $HDFS_HOMEDIR/.ssh
chmod 0700 $HDFS_HOMEDIR/.ssh
rm -f $HDFS_HOMEDIR/.ssh/id_rsa*
su - hdfs -s /bin/bash -c "ssh-keygen -q -t rsa -f $HDFS_HOMEDIR/.ssh/id_rsa -N \"\""



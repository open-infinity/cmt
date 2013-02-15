@role:hmaster /tmp/cleanup.sh
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
# hmaster_cleanup.sh
# 
# @author Timo Saarinen
# @author Tommi Siitonen
# @version 1.0.0
# @since 1.0.0
#

# Delete rpm etc backups
find /etc -name "*.rpmsave" | xargs rm -fR

# Delete data directories
rm -fR /data 

# Delete configuration files
rm -fR /etc/hadoop-0.20 /etc/hbase /etc/zookeeper.dist

# Cloudera RPMs don't do their cleanup work correctly and reinstalling RPMs
# can lead to problems with /etc/alternatives/hadoop-0.20-conf setups. 
# This remove should prevent them from happening.
rm -fR /etc/hadoop /etc/hadoop-0.20

# Delete some other directories
rm -fR /var/zookeeper


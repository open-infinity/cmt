@role:shard /tmp/mongodb/cleanup.sh
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
# shard_cleanup.sh
# 
# @author Timo Saarinen
# @author Tommi Siitonen
# @version 1.0.0
# @since 1.0.0
#

ps -eo user,comm,pid | egrep "mongod\s+mongo.\s+[0-9]+" >/dev/null
if [ $? -eq 0 ] ; then
    echo "Killing all mongos and mongod processes"
    killall mongod
    killall mongos
    sleep 10s
    ps -eo user,comm,pid | egrep "mongod\s+mongo.\s+[0-9]+" >/dev/null
    if [ $? -eq 0 ] ; then
        echo "Mongo processes still alove, hitting harder..."
        killall -9 mongod
        killall -9 mongos
    fi
fi

rm -fR [[MONGO_DBPATH]]/*
#rm -fR [[MONGO_LOGPATH]]/mongo*


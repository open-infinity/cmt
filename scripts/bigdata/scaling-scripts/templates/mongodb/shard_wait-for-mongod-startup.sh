@role:shard /tmp/mongodb/wait-for-mongod-startup.sh
#!/bin/bash

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
# shard_wait-for-mongod-startup.sh
# 
# @author Timo Saarinen
# @author Tommi Siitonen
# @version 1.0.0
# @since 1.0.0
#


echo "Waiting for mongod process to bind a port..."
while :
do
    # Test if mongod port is bound
    netstat --numeric-ports --all | egrep "tcp\s+[0-9+]\s+[0-9+]\s+\S+:27018.*\s+\S+\s+LISTEN.*" >/dev/null
    PORT_TEST=$?
  
    # Test if mongod process is alive
    ps -eo user,comm,pid | egrep "mongod\s+mongod\s+[0-9]+" >/dev/null
    PROCESS_TEST=$?

    if [ $PORT_TEST -eq 0 ] && [ $PROCESS_TEST -eq 0 ] ; then
        echo "mongod port bound"
        exit 0
    elif [ $PROCESS_TEST -ne 0 ] ; then
        echo "mongod process died unexpectedly!" >/dev/stderr
        exit 1
    fi

    # Delay between iterations
    sleep 1
done


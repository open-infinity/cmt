@role:hmaster /usr/lib/hadoop-0.20/bin/hadoop-config.sh
@role:zookeeper /usr/lib/hadoop-0.20/bin/hadoop-config.sh
@role:slave /usr/lib/hadoop-0.20/bin/hadoop-config.sh
@role:hive /usr/lib/hadoop-0.20/bin/hadoop-config.sh
# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

# included in all the hadoop scripts with source command
# should not be executable directly
# also should not be passed any arguments, since we need original $*

# Resolve links ($0 may be a softlink) and convert a relative path
# to an absolute path.  NB: The -P option requires bash built-ins
# or POSIX:2001 compliant cd and pwd.
this="${BASH_SOURCE-$0}"
bin=$(cd -P -- "$(dirname -- "$this")" && pwd -P)
script="$(basename -- "$this")"
this="$bin/$script"

# the root of the Hadoop installation
if [ -z "$HADOOP_HOME" ]; then
  export HADOOP_HOME=`dirname "$this"`/..
fi

# double check that our HADOOP_HOME looks reasonable.
# cding to / here verifies that we have an absolute path, which is
# necessary for the daemons to function properly
if [ -z "$(cd / && ls $HADOOP_HOME/hadoop-core-*.jar $HADOOP_HOME/build 2>/dev/null)" ]; then
  cat 1>&2 <<EOF
+================================================================+
|      Error: HADOOP_HOME is not set correctly                   |
+----------------------------------------------------------------+
| Please set your HADOOP_HOME variable to the absolute path of   |
| the directory that contains hadoop-core-VERSION.jar            |
+================================================================+
EOF
  exit 1
fi

# check to see if the conf dir is given as an optional argument
if [ $# -gt 1 ]
then
    if [ "--config" = "$1" ]
	  then
	      shift
	      confdir=$1
	      shift
	      HADOOP_CONF_DIR=$confdir
    fi
fi
 
# Allow alternate conf dir location.
HADOOP_CONF_DIR="${HADOOP_CONF_DIR:-$HADOOP_HOME/conf}"

if [ -f "${HADOOP_CONF_DIR}/hadoop-env.sh" ]; then
  . "${HADOOP_CONF_DIR}/hadoop-env.sh"
fi

# set JAVA_HOME
export JAVA_HOME=/etc/alternatives/jre_openjdk
if [ -f $JAVA_HOME/bin/java ] ; then
  true
else
  echo "Error: JAVA_HOME is not set correctly." >&2
  exit 1
fi

# Newer versions of glibc use an arena memory allocator that causes virtual
# memory usage to explode. This interacts badly with the many threads that
# we use in Hadoop. Tune the variable down to prevent vmem explosion.
export MALLOC_ARENA_MAX=${MALLOC_ARENA_MAX:-4}


if [ -d $HADOOP_HOME/pids ]; then
HADOOP_PID_DIR="${HADOOP_PID_DIR:-$HADOOP_HOME/pids}"
fi

#check to see it is specified whether to use the slaves or the
# masters file
if [ $# -gt 1 ]
then
    if [ "--hosts" = "$1" ]
    then
        shift
        slavesfile=$1
        shift
        export HADOOP_SLAVES="${HADOOP_CONF_DIR}/$slavesfile"
    fi
fi




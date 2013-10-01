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
# Copies files from the bigdata scaling directory to the ROOT dir and builds RPM package using FPM.
# The following packages are required in CentOS:
#
#   yum install ruby rubygems ruby-devel rpm-build make gcc-c++ 
# 
# Then the following common have to be run (FPM 0.4.1):
#
#   gem install fpm
#
# The script itself should be run in an operating system supporting RPM creation.
# This script can be run as normal user (as opposite to root).
#

# 
# @author Timo Saarinen
# @author Tommi Siitonen
# @version 1.0.0
# @since 1.0.0
#

#
# VERSION HISTORY
#
# 1.0   2012-04-03  Initial version
#
# 1.0.1 2012-04-04  Added RPM install retry
#
# 1.1   2013-08-28  MongoDB 2.4.6 and Cloudera 4.3 support. 
#                   Fixed /etc/hosts generation.
#
# 1.2   2013-09-19  Hadoop without HBase support
#                   Apache Hive Support
#
# 1.3   2013-09-24  Apache Pig support implemented
#
# 1.3.1 2013-10-01  Hive support related fixes
#

# Update the application files
export TMPDIR=/tmp/bigdata-fpm-ROOT
export SCRIPTDIR=`pwd`
export TARGETDIR=~/

mkdir $TMPDIR || exit 1
mkdir -p $TMPDIR/ROOT/ || exit 1
cp -pR ../templates $TMPDIR/ROOT/
cp -pR ../bin $TMPDIR/ROOT/

# Build RPM package
fpm -s dir -t rpm -C $TMPDIR -p $TARGETDIR -a noarch \
	-n oi3-bigdata-mgmt --version 1.3.1 --iteration 2 \
	--description "Hadoop, HBase and MongoDB cluster up and down scaling scipts" \
	-d "python python-paramiko" \
	--prefix /opt/bigdata \
	-C $TMPDIR/ROOT --after-install $SCRIPTDIR/fpm-post-install.sh \
	bin templates

# Cleanup
rm -fR $TMPDIR


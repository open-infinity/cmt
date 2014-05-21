
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
# config.py
# 
# @author Timo Saarinen
# @author Tommi Siitonen
# @version 1.0.0
# @since 1.0.0
#

import sys
from os.path import join

# Directories
storage_dir = "/opt/openinfinity/3.1.0/bigdata/metadata"
bundle_root_dir = "/var/tmp/openinfinitybundle"
template_dir = join(sys.path[0], "../templates")
log_dir = "/opt/openinfinity/3.1.0/bigdata/log"
ssh_log_logpath = "/opt/openinfinity/3.1.0/bigdata/log"
database_dir = "/opt/openinfinity/3.1.0/bigdata/data"
tmp_dir = "/opt/openinfinity/3.1.0/bigdata/tmp"

hive_metastore_warehouse_dir = "/opt/openinfinity/3.1.0/bigdata/hive-warehouse"

# Database users 
# TODO: these should be generated per instance instead of using hard-coded ones here
hive_database_root_password = 'bzzzzzzZ'
hive_database_user          = 'hive'
hive_database_user_password = 'bzzzzzzz'

# Constants
FILE_LOCK_TIMEOUT = (24 * 3600)


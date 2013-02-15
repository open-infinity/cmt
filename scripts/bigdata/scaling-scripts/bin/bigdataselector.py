
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
# bigdataselector.py
# 
# @author Timo Saarinen
# @author Tommi Siitonen
# @version 1.0.0
# @since 1.0.0
#

from os.path import join, exists
from config import *
from common import MgmtException
import hbase
import mongodb 

# Create either HBase or MongoDB context based on how the database is initialized.
def create_config_context(options):
    if exists(join(storage_dir, "mongodb")):
        return create_mongo_config_context(options)
    if exists(join(storage_dir, "hbase")):
        return create_hbase_config_context(options)
    else:
        raise MgmtException("No initialized hbase or mongodb configuration found.")

# Create HBase config context
def create_hbase_config_context(options):
    cc = hbase.HBaseConfigContext()
    cc.hmasters   = hbase.get_hbase_masters()
    cc.zookeepers = hbase.get_zookeepers()
    cc.slaves     = hbase.get_hbase_slaves()
    cc.hives      = hbase.get_hbase_hives()
    cc.options    = options
    return cc
    
# Create MongoDB config context
def create_mongo_config_context(options):
    cc = mongodb.MongoConfigContext()
    cc.configs    = mongodb.get_mongo_config_servers()
    cc.shards     = mongodb.get_mongo_shards()
    cc.options    = options
    return cc


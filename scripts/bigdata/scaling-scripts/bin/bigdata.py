
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
# bigdata.py
# 
# @author Timo Saarinen
# @author Tommi Siitonen
# @version 1.0.0
# @since 1.0.0
#

from __future__ import division       # Python 3 forward compatibility
from __future__ import print_function # Python 3 forward compatibility
from os.path import join, exists
import os
import sys
import errno
from dircache import listdir
from config import *
from mgmtexception import MgmtException
from datetime import datetime
import time
import hbase
import mongodb 

# Create either HBase or MongoDB context based on how the database is initialized.
def create_config_context(options):
    if not is_initialized():
        raise MgmtException("Big data not initialized.")
    if exists(join(storage_dir, "mongodb")):
        return create_mongo_config_context(options)
    if exists(join(storage_dir, "hbase")):
        return create_hbase_config_context(options)
    if exists(join(storage_dir, "hadoop")):
        return create_hadoop_config_context(options)
    raise MgmtException("Can't read node information because of unrecognized bigdata configuration.")

# Create HBase config context
def create_hbase_config_context(options):
    cc = hbase.HBaseConfigContext()
    cc.hmasters   = hbase.get_hbase_masters()
    cc.zookeepers = hbase.get_zookeepers()
    cc.slaves     = hbase.get_hbase_slaves()
    cc.hives      = hbase.get_hbase_hives()
    cc.options    = options
    return cc
    
# Create Hadoop config context
def create_hadoop_config_context(options):
    cc = hbase.HBaseConfigContext(hbase = False)
    cc.hmasters   = hbase.get_hbase_masters()
    cc.zookeepers = []
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

# Test if big data is initialized already
def is_initialized():
    bigdata_id_filename = join(storage_dir, "bigdata-id")
    return exists(bigdata_id_filename)

# ------------------------------------------------------------------------------

locks = {}
lock_recursion = {}
lock_files = {}
locked = False # TODO: remove lock/unlock when acquire/release work

# Mark bigdata directory tree as locked to prevent other instances from messing 
# up the configuration. Returns True if succeeds, False if not.
def lock():
    global locked
    # Check that the initialize is run
    if not exists(join(storage_dir, "hbase")):
        if not exists(join(storage_dir, "mongodb")):
            raise MgmtException("Bigdata not initialized in the management server")
    
    # Create the new directory
    filename = join(storage_dir, "lock")
    if exists(filename):
        return False
    else:
        # FIXME: the test/lock is not atomic
        f = open(filename, "w")
        f.write(str(datetime.now()))
        f.close()
        locked = True
        return True

# Lock HBase tree
def unlock():
    global locked
    if locked:
        filename = join(storage_dir, "lock")
        if exists(filename):
            os.remove(filename)
        locked = False


# Acquire a lock for certain big data resource. If the wait_until_free is True,
# waits until the lock is freed, creates a new lock and returns True.
# If no waiting is wanted, returns either False or True depending on if the
# lock is active or not.
def acquire(resource, wait_until_free = True):
    global locks
    global lock_recursion
    global lock_files
    
    # Check that the initialize is run
    if not exists(join(storage_dir, "hbase")):
        if not exists(join(storage_dir, "mongodb")):
            if not exists(join(storage_dir, "lock")):
                raise MgmtException("Bigdata not initialized in the management server")

    # Already acquired
    if resource in locks:
        lock_recursion[resource] = lock_recursion[resource] + 1
        return True
    
    # Filename
    filename = join(storage_dir, "lock", resource)

    # Loop as long as lock is acquired or timeout detected    
    while True:
        start_time = time.time()        
        try:
            lock_recursion[resource] = 1
            locks[resource] = os.open(filename, os.O_CREAT|os.O_EXCL|os.O_RDWR)
            os.write(locks[resource], "%d" % os.getpid())
            break;
        except OSError as e:
            if e.errno != errno.EEXIST:
                raise 
            if not wait_until_free:
                return False
            if (time.time() - start_time) >= FILE_LOCK_TIMEOUT:
                raise MgmtException("File lock '%s' timeout occured." % resource)
            time.sleep(1.0)
    
    return True

# Release a lock of given bigdata resource.
def release(resource):
    global locks
    global lock_recursion
    global lock_files
    
    if resource in locks:
        # If the lock is acquired recursively, decrease counter
        if lock_recursion[resource] > 1:
            lock_recursion[resource] = lock_recursion[resource] - 1
            return

        filename = join(storage_dir, "lock", resource)
        os.close(locks[resource])
        os.unlink(filename)
        del locks[resource]
        del lock_recursion[resource]
    else:
        print("Warning: resource %s released without acquiring" % resource, file=sys.stderr)

# Release all resources allocated by this process. It's good to call this in the 
# finally block of the mainloop.
def release_all():
    global locks
    for resource in list(locks.keys()):
        #print("Releasing lock for resource '%s'" % resource, file=sys.stderr)
        release(resource)



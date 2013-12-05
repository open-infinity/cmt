
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
# mongodb.py
# 
# @author Timo Saarinen
# @author Tommi Siitonen
# @version 1.0.0
# @since 1.0.0
#

from os.path import join, exists
from os import mkdir, makedirs, remove, system
from dircache import listdir
from datetime import datetime
#from sshconn import SSHConnection
from paramiko import SSHException
from time import sleep
import shutil
import hashlib
import cStringIO as string_io
import ConfigParser
import socket

from templates import *
from config import *
from common import *
from mgmtexception import MgmtException
import bigdata

# Just a simple object holding lists of different nodes
class MongoConfigContext(object):
    type = "mongodb"
    
    replica_set_size = 3         # Number of members in a replica set in a shard
    number_of_config_servers = 3 # for production

    configs = [] # List of config server nodes
    shards = []  # List of shard nodes

    # Constructor
    def __init__(self):
        bconf = read_hashmap(join(storage_dir, 'mongodb', 'cluster-config'))
        if 'replication-size' in bconf:
            self.replica_set_size = int(bconf['replication-size'])
            if self.replica_set_size < 1:
                raise MgmtException("Replication set size can't be less than one! (%s)" % self.replica_set_size)

    
    # Get all nodes
    def get_everything(self):
        return self.configs + self.shards
    everything = property(get_everything)

    # Params for /etc/bigdata file
    def get_bigdata_params(self):
        params = {}
        if len(self.configs) >= 1:
            cfg_server_names = []
            for cfg_node in self.configs:
                cfg_server_names.append(cfg_node.hostname)
            params['mongo-config-servers'] = ','.join(cfg_server_names)
        return params

    # Returns the other members of this replica set or [] if none was found
    def find_replicaset_members(self, replsetnum):
        rset = []
        for node in self.shards:
            if node.replsetnum == replsetnum:
                rset.append(node)
        return rset

    # Get the first node with mongos        
    def get_first_mongos(self):
        if len(self.configs) > 0:
            return self.configs[0]
        else:
            return None
    first_mongos = property(get_first_mongos)
    
# Generates all node roles from 1 to num
def generate_role_list(cc, num):
    r = []
    ccount = 0
    scount = 0
    for i in range(1, num + 1):
        if i <= cc.number_of_config_servers:
            r.append('config')
            ccount += 1
        else:
            r.append('shard')
            scount += 1
    return (r, {'configs' : ccount, 'shards' : scount})

# Make directory and don't throw an exception, if it exists already
def _mkdirs(path, mode = 0777):
    if not exists(path):
        makedirs(path, mode)

# Node-related values read from file
class MongoNode(Node):
    def __init__(self, role, num = None):
        self.type = "mongodb"
        self.role = role
        if not role in ['config', 'shard']:
            raise MgmtException("Unexpected MongoDB node role: %s" % role)
        if num != None:
            self.num  = num
        else:
            # Find the next number
            max_num = 0
            for item in listdir(join(storage_dir, self.type, role)):
                try:
                    max_num = max(int(item), max_num)
                except ValueError:
                    pass
            self.num = max_num + 1

            self.put_config_state("blank")
            self.put_node_status("created")

    def __repr__(self):
        return "%s/%s (%s) rs%s" % (self.role, self.num, self.hostname, self.replsetnum)
        
    # Replica set number
    def get_replsetnum(self): return self.read(self.role, self.num, "replsetnum")
    def set_replsetnum(self, value): self.write(self.role, self.num, "replsetnum", value)
    replsetnum = property(get_replsetnum, set_replsetnum)

    ## Slave (or master) in replica set
    #def get_slave(self): return bool(int(self.read(self.role, self.slave, "slave")))
    #def set_slave(self, value): self.write(self.role, self.slave, "slave", str(int(value)))
    #slave = property(get_slave, set_slave)

    # Configure a new node
    def attach(self, cc, out):
        out.info("Started installing %s in %s (%s) ---------------------------------------" % (self.role, self.hostname, self.ip_address))

        # Regenerate host files of all the managed nodes (before acquiring the node lock)
        try:
            self.put_config_state("starting")
            self.config_description = "regerating /etc/host files"
            out.info("Regerating /etc/host files")
            bigdata.acquire('cluster')
            cc = bigdata.create_config_context(cc.options)
            self.regenerate_etc_hosts_files(cc, out)
        finally:
            bigdata.release('cluster')
        
        # Connect by SSH
        bigdata.acquire("node-%s-files" % self.hostname)
        ssh = SSHConnection(self.hostname, out)
        try:
            out.info("Connecting to %s" % self.hostname)
            ssh.connect()

            # Check operating system type and version and remote hostname
            self.check_operating_system_version(cc, out, ssh)
            self.check_remote_hostname(cc, out, ssh)

            # Set state        
            self.put_config_state("attaching")
        
            # Decide replica set name
            try:
                bigdata.acquire('cluster')
                cc = bigdata.create_config_context(cc.options)
            
                current_replica_set_size = 0
                primary_replicaset_member = None
                current_replica_set = []
                current_is_the_last_in_rs = False
                if self.role == 'shard':
                    max_rn = 0
                    replsets = {}
                    for snode in cc.shards:
                        if snode.replsetnum != None:
                            rn = int(snode.replsetnum)
                            max_rn = max(rn, max_rn)
                            if rn in replsets:
                                replsets[rn].append(snode)
                            else:
                                replsets[rn] = [snode]
                    self.replsetnum = None
                    for rn in replsets:
                        # Try to find a non-complete replica set and assign this node to it
                        if len(replsets[rn]) < cc.replica_set_size:
                            if len(replsets[rn]) + 1 == cc.replica_set_size:
                                current_is_the_last_in_rs = True
                            self.replsetnum = rn
                            primary_replicaset_member = replsets[rn][0]
                            current_replica_set_size = len(replsets[rn])
                            current_replica_set = replsets[rn]
                    if self.replsetnum == None:                        
                        self.replsetnum = max_rn + 1
                else:
                    self.replsetnum = None
            finally:
                bigdata.release('cluster')
        
            # Make template params
            template_params = make_template_params(cc, self)
        
            # Check if there is an installation in the node already
            self.check_possible_big_data_installation(cc, out, ssh)

            # Install RPM files from the custom repository
            self.config_description = "installing RPMs"
            out.info("Installing RPM files from repository")
            ssh.install(['mongo-10gen-server', 'mongo-10gen'])
            self.config_description = None

            # Populate templates and send them to the remote server
            out.info("Populating and applying configuration templates")
            for content, remote_filename, mode in populate_templates('mongodb', self.role, template_params):
                ssh.send_file_to(content, remote_filename, mode=mode)

            # Run post install script
            out.info("Executing post-install script")
            ssh.execute("cd /tmp/mongodb && ./post-install.sh", raise_on_non_zero=True)

            # Process startups
            attached_when_finished = True
            if self.role == "config":
                # Start cfg server
                out.info("Starting config server")
                ssh.execute("/etc/rc.d/init.d/mongo-cfgsrv restart")
                
                # Wait for cfg server
                out.info("Waiting for mongo config server to get ready and its port bound (can take minutes)")
                self.config_description = "initializing"
                ssh.execute("cd /tmp/mongodb && ./wait-for-mongo-cfgsrv-startup.sh")
                self.config_description = None
                
                # Reconfigure config nodes                    
                if len(cc.configs) >= cc.number_of_config_servers and cc.configs[-1].hostname == self.hostname:
                    # Since parallel configuration is possible, let's ensure,
                    # that the other config servers are ready
                    out.info("Waiting for the other config nodes to become ready")
                    self.put_config_state("waiting", "waiting for config nodes")
                    all_config_nodes_ok = False
                    while not all_config_nodes_ok:
                        try:
                            bigdata.acquire('cluster')
                            cc = bigdata.create_config_context(cc.options)
                            all_config_nodes_ok = True
                            for cnode in cc.configs:
                                if cnode.hostname != self.hostname:
                                    if cnode.config_state != 'attached':
                                        all_config_nodes_ok = False
                                        break
                                    if cnode.config_state == 'error':
                                        # We can't continue if any of the config nodes failed
                                        raise MgmtException("Config node %s has an error. Aborting configuration." % cnode.hostname)
                        finally:
                            bigdata.release('cluster')
                        sleep(2.0)
                    self.put_config_state("attaching")
                
                    out.info("Reconfigurating and restarting mongos processes in config nodes")
                    try:
                        bigdata.acquire('cluster')
                        for cnode in cc.configs:
                            bigdata.acquire("node-%s-files" % cnode.hostname)
                            rssh = SSHConnection(cnode.hostname, out)
                            try:
                                rssh.connect()
                                # Update config node templates
                                for content, remote_filename, mode in populate_templates('mongodb', 'config', template_params):
                                    rssh.send_file_to(content, remote_filename, mode=mode)
                                
                                # Mongos (re)start
                                rssh.execute("/etc/init.d/mongos restart")
                            finally:
                                rssh.disconnect()
                                bigdata.release("node-%s-files" % cnode.hostname)
                    finally:
                        bigdata.release('cluster')

            elif self.role == "shard":
                # Start the services
                out.info("Starting services of shard node")
                ssh.execute("/etc/rc.d/init.d/mongod restart")

                out.info("Waiting for mongod to get ready and its port bound (can take minutes)")
                self.config_description = "mongod initializing"
                ssh.execute("cd /tmp/mongodb && ./wait-for-mongod-startup.sh")
                self.config_description = None

                # Since parallel configuration is possible, let's ensure, all
                # of the config servers are up and running
                out.info("Waiting for the config nodes to become ready")
                self.put_config_state("waiting", "waiting for config nodes")
                all_config_nodes_ok = False
                while not all_config_nodes_ok:
                    try:
                        bigdata.acquire('cluster')
                        cc = bigdata.create_config_context(cc.options)
                        if len(cc.configs) >= cc.number_of_config_servers:
                            all_config_nodes_ok = True
                            for cnode in cc.configs:
                                if cnode.config_state != 'attached':
                                    all_config_nodes_ok = False
                                    break
                                if cnode.config_state == 'error':
                                    # We can't continue if any of the config nodes failed
                                    raise MgmtException("Config node %s has an error. Aborting configuration." % cnode.hostname)
                    finally:
                        bigdata.release('cluster')
                    sleep(2.0)
                self.put_config_state("attaching")
                    

                # Set state of the non-last rs members                
                self.put_config_state("pending", "configuration completed but waiting to be added to the replica set")
                attached_when_finished = False
                    
                # Operations for the last replica set member
                if current_is_the_last_in_rs:
                    self.put_config_state("attaching")
                    
                    # Wait until other replica set members are ready
                    out.info("Waiting for the other members of the replicaset rs%s to start" % self.replsetnum)
                    self.put_config_state("waiting", "waiting for the replica set members")
                    all_replica_set_memebers_ready = False
                    while not all_replica_set_memebers_ready:
                        all_replica_set_memebers_ready = True
                        for rnode in current_replica_set:
                            if rnode.hostname != self.hostname:
                                if rnode.config_state != 'pending':
                                    all_replica_set_memebers_ready = False
                                if rnode.config_state in ['error', None]:
                                    raise MgmtException("Aborting replica set configuration because node %s failed" % rnode.hostname)
                        sleep(2.0)
                    self.put_config_state("attaching")

                    # Initiate replica set
                    try:
                        bigdata.acquire('cluster')
                        self.put_config_state("attaching", "initiating replica set")
                        out.info("Initiating the replica set rs%s " % self.replsetnum)
                        ssh.execute("mongo localhost:27018 /tmp/mongodb/mongod-replicaset-initiate.js")
                        self.put_config_state("attaching")
                    finally:
                        bigdata.release('cluster')

                    try:                        
                        bigdata.acquire('cluster')

                        # Repopulate templates and send them to the remote server (to update mongos-shard-add.js)
                        out.info("Populating and applying configuration templates")
                        cc = bigdata.create_config_context(cc.options)
                        template_params = make_template_params(cc, self)
                        for content, remote_filename, mode in populate_templates('mongodb', self.role, template_params):
                            ssh.send_file_to(content, remote_filename, mode=mode)
                            
                        # Create a shard from the replica set
                        bigdata.acquire('cluster')
                        self.put_config_state("attaching", "creating shard")
                        out.info("Creating a shard for replica set rs%s " % self.replsetnum)
                        ssh.execute("mongo %s:27017 /tmp/mongodb/mongos-shard-add.js" % cc.first_mongos.hostname)
                        rset = cc.find_replicaset_members(self.replsetnum)
                        self.put_config_state("attaching")
                        for node in rset:
                            node.put_config_state("attached", "")
                    finally:
                        bigdata.release('cluster')
                    attached_when_finished = True
            else:
                raise "Unknown role: %s" % (self.role)

            # Run status check script
            out.info("Run status check script")
            ssh.execute("cd /tmp/mongodb && ./status-check.sh")

            # Remove post install and status check scripts
            #out.info("Remove installation related scripts from /tmp")
            #ssh.execute("rm -fR /tmp/mongodb")

            if attached_when_finished:
                self.put_config_state("attached")
            out.info("Node configuration finished SUCCESSFULLY.")
        except socket.error as e:
            raise MgmtException("SSH connection failed: %s" % str(e), e)
        except SSHException as e:
            raise MgmtException("SSH error: %s" % str(e), e)
        except Exception as e:
            self.put_config_state("error", str(e))
            raise
        finally:
            if ssh != None:
                ssh.disconnect()
            out.info("Closed SSH connection to %s" % self.hostname)
            bigdata.release("node-%s-files" % self.hostname)
        return True

    # Prepares data-safe way to detach nodes from the cluster
    def exclude(self, cc, out):
        out.info("Exclude not implemented for MongoDB")

    # Reads decommission status from master. If master doesn't exist
    # or if this node is not a slave, False will be returned. If decommission 
    # is still under process, True will be returned. Detach should not be done
    # before the value is True.
    def is_decommission_in_progress(self, cc, out):
        return False

    # Remove configuration from the remote server and uninstall binaries
    def detach(self, cc, out):
        # Check exclude/decommission status
        if not cc.options.force:
            out.info("Cheking decommission status of %s" % self.hostname)
            if self.is_decommission_in_progress(cc, out):
                out.status = "pending"
                raise MgmtException("Excluding (decommission) is in progress. The node can't be detached safely, aborting. Use --force paramter to bypass this check.")
    
        # Connect by SSH
        hostname = self.hostname
        bigdata.acquire("node-%s-files" % hostname)
        ssh = SSHConnection(self.hostname, out)
        try:
            # Set state during the uninstall
            out.info("Detaching node %s from the bigdata cluster" % (self))
            self.put_config_state("detaching")

            # SSH connect
            out.info("Connecting to %s" % self.hostname)
            ssh.connect()

            # Populate /tmp/mongodb templates and send them to the node
            template_params = make_template_params(cc, self)
            out.info("Populating and applying configuration templates")
            for content, remote_filename, mode in populate_templates('mongodb', self.role, template_params):
                if remote_filename[0:12] == "/tmp/mongodb":
                    ssh.send_file_to(content, remote_filename, mode=mode)
                    
            # Remove shard from config server
            if self.role == "shard":
                if cc.options.force:
                    out.info("Shard draining skipped because of the force switch")
                else:
                    try:
                        bigdata.acquire('cluster')
                        rset = cc.find_replicaset_members(self.replsetnum)
                        if len(rset) >= cc.replica_set_size:
                            out.info("Removing %s (very slow)" % template_params["SHARD_NAME"])
                            ssh.execute("mongo %s:27017 /tmp/mongodb/mongos-shard-remove.js" % (cc.first_mongos.hostname))
                            for node in rset:
                                node.put_config_state("drained", "")
                    finally:
                        bigdata.release('cluster')

            # Stop services
            out.info("Stopping services")
            if self.role == "shard":
                out.info("  mongod...")
                ssh.execute("/etc/init.d/mongod stop", raise_on_non_zero=False)
            elif self.role == "config":
                out.info("  mongos...")
                ssh.execute("/etc/init.d/mongos stop", raise_on_non_zero=False)
                out.info("  config server...")
                ssh.execute("/etc/init.d/mongo-cfgsrv stop", raise_on_non_zero=False);      
            
            # Uninstall RPMS
            out.info("Uninstalling RPM files")
            ssh.uninstall(['mongo-10gen-server', 'mongo-10gen'])

            # Run the final cleanup script
            out.info("Run cleanup script")
            ssh.execute("cd /tmp/mongodb && ./cleanup.sh", raise_on_non_zero=False)

            # Delete /etc/bigdata file from the server
            ssh.remove_file("/etc/bigdata")

            # Drop the node from the config context
            if self.role == "shard":
                del cc.shards[cc.shards.index(self)]
            elif self.role == "config":
                del cc.configs[cc.configs.index(self)]

            # Make template params
            template_params = make_template_params(cc, self)

            # Remove post install and status check scripts
            out.info("Remove installation related scripts from /tmp")
            ssh.execute("rm -fR /tmp/mongodb", raise_on_non_zero=False, read_out_and_err=False)

            # Drop from cluster
            try:
                bigdata.acquire('cluster')
                self.drop()
            finally:
                bigdata.release('cluster')
            
            out.info("The node was dropped SUCCESSFULLY.")
        except Exception as e:
            self.put_config_state("error", str(e))
            if cc.options.force:
                out.warn("An error detected but forcing detach still")
                self.drop()
            raise
        finally:
            ssh.disconnect()
            out.info("Closed SSH the connection")
            bigdata.release("node-%s-files" % hostname)

# Make parameters for the templates
def make_template_params(cc, node):
    # Template params for HMaster
    template_params = {}

    replsetnum_as_string = '0'
    if node.replsetnum != None:
        replsetnum_as_string = int(node.replsetnum)
    
    template_params["REPLICATION_SIZE"] = cc.replica_set_size
        
    template_params['NODE_HOSTNAME'] = node.hostname
    template_params['REPLICASET_NAME'] = "rs%s" % replsetnum_as_string
    template_params['MONGO_LOGPATH'] = log_dir
    template_params['MONGO_DBPATH'] = database_dir
    template_params['SHARD_NAME'] = "shard%s" % replsetnum_as_string
    
    # Comma-separated representation of the replica set members
    replica_set_members = []
    for n in cc.everything:
        if n.replsetnum == node.replsetnum and n.replsetnum != None and n.role == "shard":
            replica_set_members.append("%s:%s" % (n.hostname, 27018))
    template_params['REPLICASET_HOSTNAMES_AND_PORTS'] = ','.join(replica_set_members)

    # Create JSON representation of the replica set members    
    rsm = []
    i = 0
    for n in cc.everything:
        if n.replsetnum == node.replsetnum and n.replsetnum != None and n.role == "shard":
            rsm.append("{_id: %d, host: '%s:%d'}" % (i, n.hostname, 27018))
            i += 1
    template_params['REPLICASET_MEMBERS_AS_JSON'] = ','.join(rsm)
    
    # List of config servers
    css = []
    for cs in cc.configs:
        css.append("%s:%s" % (cs.hostname, 27019))
    template_params["CONFIG_SERVERS_SEPARATED_BY_COMMA"] = ','.join(css)
    
    return template_params

# ------------------------------------------------------------------------------

# Create the directories. Returns False, if the directories exist already,
# and True on success.
def initialize_directories(out, options):
    bigdata_id_filename = join(storage_dir, "bigdata-id")
    if exists(bigdata_id_filename):
        out.error("Bigdata is already initialized.")
        return False

    _mkdirs(join(storage_dir, "mongodb", "shard"))
    _mkdirs(join(storage_dir, "mongodb", "config"))
    _mkdirs(join(storage_dir, "lock"))
    _mkdirs(ssh_log_logpath)

    # Save big data cluster options
    f = open(join(storage_dir, "mongodb", 'cluster-config'), 'w')
    f.write("replica-set-size: %s\n" % options.replsize)
    f.close()

    # Create a unique bigdata id for the cluster
    f = open(bigdata_id_filename, "w")
    m = hashlib.md5()
    m.update(str(datetime.now()).encode("utf-8"))
    f.write("%s\n" % m.hexdigest())
    f.close()
    return True

# Test if big data is initialized already
def is_initialized():
    bigdata_id_filename = join(storage_dir, "bigdata-id")
    return exists(bigdata_id_filename)

# Read unique id for the big data
def read_bigdata_unique_id():
    f = open(join(storage_dir, "bigdata-id"), "r")
    u = f.read()
    f.close()
    return u.strip()
    
# ------------------------------------------------------------------------------

locked = False

# Mark MongoDB directory tree as locked to prevent other instances from messing 
# up the configuration. Returns True if succeeds, False if not.
def lock():
    global locked
    # Check that the initialize is run
    if not exists(join(storage_dir, "mongodb")):
        raise MgmtException("MongoDB not initialized in the management server")
    
    # Create the new directory
    filename = join(storage_dir, "mongodb", "lock")
    if exists(filename):
        return False
    else:
        # FIXME: the test/lock is not atomic
        f = open(filename, "w")
        f.write(str(datetime.now()))
        f.close()
        locked = True
        return True

# Lock  MongoDB tree
def unlock():
    global locked
    if locked:
        filename = join(storage_dir, "mongodb", "lock")
        if exists(filename):
            remove(filename)
        locked = False

# ------------------------------------------------------------------------------

# Returns list of HMaster nodes (usually only one)
def get_mongo_shards():
    nodes = []
    for item in listdir(join(storage_dir, "mongodb", "shard")):
        if item[0] != ".":
            try:
                nodes.append(MongoNode("shard", int(item)))
            except ValueError:
                pass
    nodes.sort()
    return nodes
    
# Returns list of Zookeepers
def get_mongo_config_servers():
    nodes = []
    for item in listdir(join(storage_dir, "mongodb", "config")):
        if item[0] != ".":
            try:
                nodes.append(MongoNode("config", int(item)))
            except ValueError:
                pass
    nodes.sort()
    return nodes

# ------------------------------------------------------------------------------


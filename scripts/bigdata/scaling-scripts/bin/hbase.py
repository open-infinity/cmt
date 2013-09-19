
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
# hbase.py
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
import hashlib
import cStringIO as string_io
import ConfigParser
from templates import *
from config import *
import socket
from common import *
from mgmtexception import MgmtException
import bigdata

# Just a simple object holding lists of different nodes
class HBaseConfigContext(object):
    type = "hbase"
    hmasters = []
    zookeepers = []
    slaves = []
    hives = []
    options = {}
    replication_size = 3
    hive_support = True

    # Constructor
    def __init__(self, hbase=True):
        if hbase:
            self.type = "hbase"
        else:
            self.type = "hadoop"
        bconf = read_hashmap(join(storage_dir, self.type, 'cluster-config'))
        if 'replication-size' in bconf:
            self.replication_size = int(bconf['replication-size'])
        if 'hive-support' in bconf:
            self.hive_support = (bconf['hive-support'] == 'True')

    def get_hmaster(self): 
        if len(self.hmasters) > 0:
            return self.hmasters[0]
        else:
            return None
    hmaster = property(get_hmaster)

    def is_hbase_cluster(self): return self.type == 'hbase'

    def get_everything(self):
        return self.hmasters + self.zookeepers + self.hives + self.slaves
    everything = property(get_everything)

    # Print presentation
    def __repr__(self):
        s = "Config context (%s)\n" % self.type
        for node in self.get_everything():
            s +="  %s\n" % node
        return s
        
    # String presentation
    def __str__(self):
        return self.__repr__()


# Generates all node roles from 1 to num
def generate_role_list(cc, num):
    r = []
    hmcount = 0
    zcount = 0
    scount = 0
    hive_count = 0
    for i in range(1, num + 1):
        if cc.type == 'hbase' and (i < 3 or zcount < scount // 100 or zcount % 2 == 0):
            r.append('zookeeper')
            zcount += 1
        elif hmcount == 0 and scount >= 3: # Notice: this should be in sync with dfs.replication parameter in hmaster_hdfs-site.xml
            r.append('hmaster')
            hmcount += 1
        elif cc.hive_support and hive_count == 0 and zcount >= 3:
            r.append('hive')
            hive_count += 1
        else:
            r.append('slave')
            scount += 1
    return (r, { 'hmasters' : hmcount, 'zookeepers' : zcount, 'slaves' : scount,'hives' : hive_count })

# Make directory and don't throw an exception, if it exists already
def _mkdirs(path, mode = 0777):
    if not exists(path):
        makedirs(path, mode)

# Count number of nodes with the same config state as the given argument
def count_config_states(cclist, state):
    n = 0
    for node in cclist:
        if node.config_state == state:
            n += 1
    return n

# Node-related values read from file
class HBaseNode(Node):
    def __init__(self, role, cluster_type, num = None):
        if not role in ['hmaster', 'zookeeper', 'slave', 'hive']:
            raise MgmtException("Uknown role '%s'" % role)
        if not cluster_type in ['hbase', 'hadoop']:
            raise MgmtException("Uknown cluster type %s" % cluster_type)
        if num != None and type(num) != type(1):
            raise MgmtException("Node number is not of numeric type: %s" % num)
            
        self.type = cluster_type
        self.role = role
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

    def is_hadoop_only(self): return (self.type == 'hadoop')
        
    def get_hbase_public_ssh_key(self): self.read(self.role, self.num, "hbase-public-ssh-key", 1)
    def set_hbase_public_ssh_key(self, value): self.write(self.role, self.num, "hbase-public-ssh-key", str(value), 1)
    hbase_public_ssh_key = property(get_hbase_public_ssh_key, set_hbase_public_ssh_key)

    def get_hdfs_public_ssh_key(self): self.read(self.role, self.num, "hdfs-public-ssh-key", 1)
    def set_hdfs_public_ssh_key(self, value): self.write(self.role, self.num, "hdfs-public-ssh-key", str(value), 1)
    hdfs_public_ssh_key = property(get_hdfs_public_ssh_key, set_hdfs_public_ssh_key)

    # Returns a fresh config context. Locks 'cluster' while reading.
    def recreate_config_context(self, options):
        try:
            bigdata.acquire('cluster')
            return bigdata.create_config_context(options)
        finally:
            bigdata.release('cluster')

    # Configure a new node
    def attach(self, cc, out):
        out.info("Started installing %s in %s (%s) ---------------------------------------" % (self.role, self.hostname, self.ip_address))

        # Regenerate host files of all the managed nodes (before acquiring the node lock)
        if not cc.options.dns:
            try:
                self.config_description = 'regenerating /etc/hosts files of the cluster'
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
        
            # Regenerate host files of all the managed nodes
            try:
                bigdata.acquire('cluster')
                self.regenerate_etc_hosts_files(cc, out)
            finally:
                bigdata.release('cluster')
        
            # Make template params
            template_params = make_template_params(cc)
        
            # Check if there is an installation in the node already
            self.check_possible_big_data_installation(cc, out, ssh)
            
            # List of rpm lists for uninstallation
            rpms_list = RpmList(self)
            rpms_list.push([])

            # Install RPMs
            if cc.options.rpms:
                self.config_description = 'installing rpms'
                
                ## Running yum update
                #out.info("Running YUM update") # TODO: this shouldn't be done in the final version
                #ssh.execute("yum clean all ; yum --assumeyes --quiet update")

                # Install open java
                out.info("Installing OpenJDK")
                ssh.install(['java-1.6.0-openjdk'])

            
                if self.role == "hmaster":
                    out.info("Installing HMaster RPM files")
                    if self.type == 'hbase':
                        rpms = [
                                'zookeeper',
                                'hadoop-0.20-mapreduce-jobtracker',
                                'hadoop-hdfs-secondarynamenode',
                                'hadoop-hdfs-namenode',
                                'hbase',
                                'hbase-master',
                                'hadoop-hdfs',
                        ]
                    else:
                        rpms = [
                                'hadoop-0.20-mapreduce-jobtracker',
                                'hadoop-hdfs-secondarynamenode',
                                'hadoop-hdfs-namenode',
                                'hadoop-hdfs',
                        ]
                    ssh.install(rpms)
                    rpms_list.push(rpms)
                elif self.role == "zookeeper":
                    # Install RPM packages
                    out.info("Installing ZooKeeper RPM files")
                    rpms = [
                            'zookeeper',
                            'zookeeper-server',
                    ]
                    ssh.install(rpms)
                    rpms_list.push(rpms)
                elif self.role == "slave":
                    # Install RPM packages
                    if self.type == 'hbase':
                        rpms = [
                                    'zookeeper',
                                    'hadoop-hdfs',
                                    'hadoop-hdfs-datanode',
                                    'hadoop-0.20-mapreduce-tasktracker',
                                    'hbase',
                                    'hbase-regionserver',
                        ]
                    else:
                        rpms = [
                                    'hadoop-hdfs',
                                    'hadoop-hdfs-datanode',
                                    'hadoop-0.20-mapreduce-tasktracker',
                        ]
                    ssh.install(rpms)
                    rpms_list.push(rpms)
                elif self.role == "hive":
                    # Install RPM packages
                    if self.type == 'hbase':
                        rpms = [
                                    'hive',
                                    'hive-hbase',
                                    'hive-metastore',
                                    'hive-server2',
                                    'MariaDB-server',
                                    'MariaDB-client',
                                    'mysql-connector-java',
                        ]
                    else:
                        rpms = [
                                    'hive',
                                    'hive-metastore',
                                    'hive-server2',
                                    'MariaDB-server',
                                    'MariaDB-client',
                                    'mysql-connector-java',
                        ]
                    ssh.install(rpms)
                    rpms_list.push(rpms)
                else:
                    raise "Unknown role: %s" % (self.role)

            # FIXME: earlier these all refered to 'hbase' user, but changed to the correct ones. Does this work?
            hbase_homedir = ssh.get_homedir_of('hbase')
            hdfs_homedir = ssh.get_homedir_of('hdfs')
            zookeeper_homedir = ssh.get_homedir_of('zookeeper')
            hive_homedir = ssh.get_homedir_of('hive')

            # Ensure that update-alternatives configuration is correct
            if self.role in ['hmaster', 'slave']:
                out.info("Configure update alternatives")
                ssh.execute("mkdir -p /etc/hadoop/conf.cluster")
                ssh.execute("cp -pfR /etc/hadoop/conf.empty/* /etc/hadoop/conf.cluster/")
                ssh.execute("update-alternatives --install /etc/hadoop/conf hadoop-conf /etc/hadoop/conf.cluster 20")
                ssh.execute("update-alternatives --set hadoop-conf /etc/hadoop/conf.cluster")

            # Wait until expected number of dependencies are ready        
            try:
                self.config_description = 'waiting for other nodes'
                bigdata.release('node-%s' % self.hostname)
                if self.role == 'hmaster':
                    if len(cc.zookeepers) > 0:
                        self.config_description = 'waiting for zookeepers'
                        while count_config_states(cc.zookeepers, 'attached') < 3:
                            cc = self.recreate_config_context(cc.options)
                            sleep(2.0)
                    self.config_description = 'waiting for initial slaves'
                    while count_config_states(cc.slaves, 'attached') < 3:
                        cc = self.recreate_config_context(cc.options)
                        sleep(2.0)
                elif self.role == 'zookeeper':
                    pass
                elif self.role == 'slave':
                    if len(cc.zookeepers) > 0:
                        self.config_description = 'waiting for zookeepers'
                        while count_config_states(cc.zookeepers, 'attached') < 3:
                            cc = self.recreate_config_context(cc.options)
                            sleep(2.0)
                elif self.role == 'hive':
                    if len(cc.zookeepers) > 0:
                        self.config_description = 'waiting for zookeepers'
                        while count_config_states(cc.zookeepers, 'attached') < 3:
                            cc = self.recreate_config_context(cc.options)
                            sleep(2.0)
                else:
                    raise MgmtException("Uknown role %s" % self.role)
            finally:
                bigdata.acquire('node-%s' % self.hostname)
            self.config_description = ''
        
            # Re-read nodes and generate template parameters
            try:
                bigdata.acquire('cluster')
                cc = bigdata.create_config_context(cc.options)
                template_params = make_template_params(cc)
            finally:
                bigdata.release('cluster')

            # Populate templates and send them to the remote server
            self.config_description = 'populating templates'
            out.info("Populating and applying configuration templates")
            for content, remote_filename, mode in populate_templates('hbase', self.role, template_params):
                ssh.send_file_to(content, remote_filename, mode=mode)

            # Repopulate and start the first slaves 
            if self.role == 'hmaster':
                self.config_description = "updating and restarting slaves"
                out.info("Updating and restarting first 3 slaves")
                for snode in cc.slaves[0:3]:
                    self.config_description = "updating and restarting slaves: %s" % snode.hostname
                    rssh = SSHConnection(snode.hostname, out)
                    try:
                        bigdata.acquire('node-%s' % snode.hostname)
                        rssh.connect()
                        
                        # Repopulate and send the templates
                        for content, remote_filename, mode in populate_templates('hbase', 'slave', template_params):
                            rssh.send_file_to(content, remote_filename, mode=mode)

                        # (Re)start the services                            
                        rssh.execute("service hadoop-hdfs-datanode restart");                    
                        rssh.execute("service hadoop-0.20-mapreduce-tasktracker restart");
                        if self.type == 'hbase':
                            rssh.execute("service hbase-regionserver restart");
                    finally:
                        bigdata.release('node-%s' % snode.hostname)
                        rssh.disconnect()

            # Run post install script
            self.config_description = 'executing post install script'
            out.info("Executing post-install script")
            ssh.execute("cd /tmp && ./post-install.sh", raise_on_non_zero=True, 
                raise_on_keywords=["java.net.ConnectException"])

            # Process startups
            if self.role == "hmaster":
                # Copy SSH keys of hdfs and hbase to the bigdata storage
                self.config_description = 'sending ssh keys'
                if self.type == 'hbase':
                    out.info("Save public SSH keys of hbase and hdfs")
                    self.hdfs_public_ssh_key  = ssh.receive_file_from("%s/.ssh/id_rsa.pub" % hdfs_homedir)
                    self.hbase_public_ssh_key = ssh.receive_file_from("%s/.ssh/id_rsa.pub" % hbase_homedir)
                else:
                    out.info("Save public SSH keys of hdfs")
                    self.hdfs_public_ssh_key  = ssh.receive_file_from("%s/.ssh/id_rsa.pub" % hdfs_homedir)

                # Start the services
                self.config_description = 'starting services'
                if self.type == 'hbase':
                    out.info("Starting services in HMaster")
                    ssh.execute("service hbase-master stop", raise_on_non_zero=False)
                    ssh.execute("/etc/init.d/hadoop-0.20-mapreduce-jobtracker stop", raise_on_non_zero=False)
                    ssh.execute("service hadoop-hdfs-secondarynamenode stop", raise_on_non_zero=False)
                    ssh.execute("service hadoop-hdfs-namenode stop", raise_on_non_zero=False)
                    
                    ssh.execute("service hadoop-hdfs-namenode start")
                    ssh.execute("service hadoop-hdfs-secondarynamenode start")
                    ssh.execute("/etc/init.d/hadoop-0.20-mapreduce-jobtracker restart")
                    ssh.execute("service hbase-master start")
                else:
                    out.info("Starting services in Hadoop Master")
                    ssh.execute("/etc/init.d/hadoop-0.20-mapreduce-jobtracker stop", raise_on_non_zero=False)
                    ssh.execute("service hadoop-hdfs-secondarynamenode stop", raise_on_non_zero=False)
                    ssh.execute("service hadoop-hdfs-namenode stop", raise_on_non_zero=False)
                    
                    ssh.execute("service hadoop-hdfs-namenode start")
                    ssh.execute("service hadoop-hdfs-secondarynamenode start")
                    ssh.execute("/etc/init.d/hadoop-0.20-mapreduce-jobtracker restart")
            elif self.role == "zookeeper":
                # Initialize the service
                self.config_description = 'starting services'
                out.info("Initiating ZooKeeper")
                ssh.execute("service zookeeper-server init");      

                # update zookeeper id before start
                ssh.execute("echo %s > /var/lib/zookeeper/myid" % (cc.zookeepers.index(self)));      
                
                # Start the service
                out.info("Starting services in ZooKeeper")
                ssh.execute("service zookeeper-server restart");
            elif self.role == "slave":
                # Copy SSH public keys
                if self.type == 'hbase':
                    out.info("Copying the public SSH keys of hbase and hdfs from master to the node")
                    ssh.send_file_to(self.hbase_public_ssh_key, "/tmp/id_rsa.pub")
                    ssh.execute("cat /tmp/id_rsa.pub >> %s/.ssh/authorized_keys && rm /tmp/id_rsa.pub" % hbase_homedir)
                    ssh.execute("mkdir %s/.ssh && chmod 0700 %s/.ssh" % (hdfs_homedir, hdfs_homedir), raise_on_non_zero=False)
                    ssh.send_file_to(self.hdfs_public_ssh_key, "/tmp/id_rsa.pub")
                    ssh.execute("cat /tmp/id_rsa.pub >> %s/.ssh/authorized_keys && rm /tmp/id_rsa.pub" % hdfs_homedir)
                else:
                    out.info("Copying the public SSH keys of hdfs from master to the node")
                    ssh.execute("mkdir %s/.ssh && chmod 0700 %s/.ssh" % (hdfs_homedir, hdfs_homedir), raise_on_non_zero=False)
                    ssh.send_file_to(self.hdfs_public_ssh_key, "/tmp/id_rsa.pub")
                    ssh.execute("cat /tmp/id_rsa.pub >> %s/.ssh/authorized_keys && rm /tmp/id_rsa.pub" % hdfs_homedir)
            
                # Start the services
                if len(cc.slaves) > 3:
                    self.config_description = 'starting services'
                    if self.type == 'hbase':
                        out.info("Starting services in HBase slave")
                        ssh.execute("service hadoop-hdfs-datanode restart");                    
                        ssh.execute("/etc/init.d/hadoop-0.20-mapreduce-tasktracker restart");
                        ssh.execute("service hbase-regionserver restart");
                    else:
                        out.info("Starting services in Hadoop slave")
                        ssh.execute("service hadoop-hdfs-datanode restart");                    
                        ssh.execute("/etc/init.d/hadoop-0.20-mapreduce-tasktracker restart");
                else:
                    out.info("Not starting slave services before HMaster")
            elif self.role == "hive":
                # Initialize the service
                self.config_description = 'starting services'
                
                # Start the service
                out.info("Starting services in Hive")
                ssh.execute("service mysql restart");
                ssh.execute("service hive-metastore restart");
                ssh.execute("service hive-server2 restart");
            else:
                raise "Unknown role: %s" % (self.role)

            # Run status check script
            self.config_description = 'checking status'
            out.info("Run status check script")
            ssh.execute("cd /tmp && ./status-check.sh")

            ## Remove post install and status check scripts
            #self.config_description = 'cleaning /tmp'
            #out.info("Remove installation scripts from /tmp")
            #ssh.remove_file("/tmp/post-install.sh")
            #ssh.remove_file("/tmp/status-check.sh")
            #ssh.remove_file("/tmp/hadoop-config.sh.diff")
            
            # Release host lock because we won't touch in this host anymore
            bigdata.release('node-%s' % self.hostname)

            # Configure related nodes
            self.config_description = 'configuring related nodes...'
            
            # Re-read nodes and generate template parameters
            try:
                bigdata.acquire('cluster')
                cc = bigdata.create_config_context(cc.options)
                template_params = make_template_params(cc)
            finally:
                bigdata.release('cluster')
            
            # Reconfigure zoo.cfg on all zookeeper nodes and restart services
            if self.role == "hmaster":
                # Notice: if we need to add something here, be careful with
                # node locking...
                pass
            elif self.role == "zookeeper":
                # Update ZooKeepers (if this is the last zookeeper being configured in parallel)
                if cc.zookeepers.index(self) == len(cc.zookeepers) - 1:
                    for node in cc.zookeepers:
                        if node != self:
                            rssh = SSHConnection(node.hostname, out)
                            try:
                                self.config_description = "reconfiguring zookeeper of %s" % node.hostname
                                bigdata.acquire('node-%s' % node.hostname)
                                rssh.connect()
                                # copy zoo.cfg including new zookeeper to all zookeeper nodes 
                                # Populate templates and send them to the remote server
                                out.info("Populating and applying configuration templates")
                                for content, remote_filename, mode in populate_templates('hbase', 'zookeeper', template_params):
                                    # send configurations to new node
                                    rssh.send_file_to(content, remote_filename, mode=mode)
                                # restart zookeepers
                                out.info("Restarting ZooKeeper services in node: %s" % node.hostname)
                                rssh.execute("service zookeeper-server restart");
                            finally:
                                bigdata.release('node-%s' % node.hostname)
                                rssh.disconnect()                                
                                
                    # Update and restart HMaster
                    if cc.hmaster != None and len(cc.zookeepers) > 3:
                        rssh = SSHConnection(cc.hmaster.hostname, out)
                        try:
                            self.config_description = "updating and restarting hmaster"
                            bigdata.acquire('node-%s' % cc.hmaster.hostname)
                            rssh.connect()
                            
                            # update hbase-site.xml for hmaster 
                            for content, remote_filename, mode in populate_templates('hbase', 'hmaster', template_params):
                                rssh.send_file_to(content, remote_filename, mode=mode)
                               
                            # refresh hmaster (hbase)?? - restart required?
                            rssh.execute("service hbase-master restart");
                        finally:
                            bigdata.release('node-%s' % cc.hmaster.hostname)
                            rssh.disconnect()

            elif self.role == "slave":
                if len(cc.hmasters) > 0:
                    rssh = SSHConnection(cc.hmaster.hostname, out)
                    try:
                        self.config_description = "updating hmaster files"
                        bigdata.acquire('node-%s' % cc.hmaster.hostname)
                        rssh.connect()
                        # hbase:
                        # copy regionservers file including new node to hmaster
                        for content, remote_filename, mode in populate_templates('hbase', 'hmaster', template_params):
                            # send configurations to new node
                            rssh.send_file_to(content, remote_filename, mode=mode)
                        # start hbase services in node
                        # optional: hbase splitting or other reblancing activities?
                        # hadoop/hdfs:
                        # copy slaves file including new node to hmaster
                        # start hadoop services in node
                        # optional: use balacer tool for re-distributing blocks to upscaled cluster
                        
                        ## Name node restart
                        #ssh.execute("service hadoop-hdfs-namenode restart")
                    finally:
                        bigdata.release('node-%s' % cc.hmaster.hostname)
                        rssh.disconnect()
                else:
                    out.info("No masters available, skipping sending region servers file at this time")

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
            
        return True

    # Prepares data-safe way to detach nodes from the cluster
    def exclude(self, cc, out):
        if self.role == 'slave' and len(cc.hmasters) > 0:
            # We won't configure the slave here by any means but master
            master = cc.hmasters[0]
            exclude_filename = '/etc/hadoop/conf/dfs-hosts-exclude'
            
            ssh = SSHConnection(master.hostname, out)
            try:
                # Set state during the uninstall
                out.info("Adding the node %s to be decommissioned from the cluster" % (self))

                # SSH connect
                out.info("Connecting to %s" % self.hostname)
                ssh.connect()

                # Modify exclude file
                out.info("Updating file %s" % (exclude_filename))
                content = ssh.receive_file_from(exclude_filename, '')
                lines = content.split('\n')
                new_line = self.hostname # "%s:50010" % self.ip_address # TODO: DNS support
                if new_line in lines:
                    out.warn("Host %s is already in the exclude list of the master" % self.hostname)
                    return
                lines.append(new_line)
                content = '\n'.join(lines)
                ssh.send_file_to(content, exclude_filename, mode=0o644)

                # Signal service
                out.info("Refreshing master node list")
                ssh.execute('su - hdfs -s /bin/bash -c "/usr/bin/hadoop dfsadmin -refreshNodes"')
                ssh.execute('su - hdfs -s /bin/bash -c "/usr/bin/hadoop dfsadmin -report"')

                self.put_config_state('excluding')
                
                out.info("The node exclusion was started SUCCESSFULLY.")
            except Exception as e:
                self.put_config_state("error", str(e))
                raise
            finally:
                ssh.disconnect()
                out.info("Closed SSH the connection")

    # Reads decommission status from master. If master doesn't exist
    # or if this node is not a slave, False will be returned. If decommission 
    # is still under process, True will be returned. Detach should not be done
    # before the value is True.
    def is_decommission_in_progress(self, cc, out):
        if self.role == 'slave' and cc.hmaster != None:
            out.info("Checking decommission status of the node")
            rssh = SSHConnection(cc.hmaster.hostname, out)
            try:
                rssh.connect()

                # Write the report to a file
                rssh.execute('su - hdfs -s /bin/bash -c "/usr/bin/hadoop dfsadmin -report" | grep -A 1 "Name: ' + str(self.ip_address) + ':" | grep "Decommission Status" | uniq >/tmp/dfsadmin-report') # TODO: DNS support

                # Remove this node from the exlude file            
                content = rssh.receive_file_from('/tmp/dfsadmin-report')
                if content != None:
                    status = content.strip()[22:]
                    if status == 'Decommission in progress':
                        return True
                    elif status == 'Decommissioned':
                        return False
                    elif status == "Normal":
                        return False
                    else:
                        out.error("Unexpected decommissions status '%s'. There is a risk of data loss." % status)
                else:
                    raise MgmtException("Decommission state is unknown because of an internal error")
            finally:
                rssh.disconnect()
        else:
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
        ssh = SSHConnection(self.hostname, out)
        try:
            # Set state during the uninstall
            out.info("Detaching node %s from the bigdata cluster" % (self))
            self.put_config_state("detaching")

            # SSH connect
            out.info("Connecting to %s" % self.hostname)
            ssh.connect()

            # Stop services
            out.info("Stopping services")
            ronz = not cc.options.force
            if self.role == "hmaster":
                if self.type == 'hbase':
                    out.info("  hmaster...")
                    ssh.execute("service hbase-master stop", raise_on_non_zero=ronz)
                out.info("  jobtracker...")
                ssh.execute("service hadoop-0.20-mapreduce-jobtracker stop", raise_on_non_zero=ronz)
                out.info("  namenode...")
                ssh.execute("service hadoop-hdfs-namenode stop", raise_on_non_zero=ronz)
                out.info("  secondarynamenode...")
                ssh.execute("service hadoop-hdfs-secondarynamenode stop", raise_on_non_zero=ronz)
            elif self.role == "zookeeper":
                out.info("  zookeeper...")
                ssh.execute("service zookeeper-server stop", raise_on_non_zero=ronz);      
            elif self.role == "slave":
                if self.type == 'hbase':
                    out.info("  regionserver...")
                    ssh.execute("service hbase-regionserver stop", raise_on_non_zero=ronz);
                out.info("  taskcracker...")
                ssh.execute("service hadoop-0.20-mapreduce-tasktracker stop", raise_on_non_zero=ronz);
                out.info("  datanode...")
                ssh.execute("service hadoop-hdfs-datanode stop", raise_on_non_zero=ronz);                    
            elif self.role == "hive":
                out.info("  HiveServer2...")
                ssh.execute("service hive-server2 stop");
                out.info("  Hive Metastore...")
                ssh.execute("service hive-metastore stop");
                out.info("  MariaDB...")
                ssh.execute("service mysql stop");
            
            # Read list of RPMs installed
            rpm_list = RpmList(self)

            # Uninstall RPMS
            if cc.options.rpms:
                while True:
                    rpms = rpm_list.pop()
                    if rpms != None:
                        if len(rpms) > 0:
                            out.info("Uninstalling %d RPMs" % (len(rpms)))
                            ssh.uninstall(rpms)
                    else:
                        break

            # Populate /tmp/ templates and send them to the node
            template_params = make_template_params(cc)
            out.info("Populating and applying configuration templates")
            for content, remote_filename, mode in populate_templates('hbase', self.role, template_params):
                if remote_filename[0:5] == "/tmp/":
                    ssh.send_file_to(content, remote_filename, mode=mode)

            # Run the final cleanup script
            out.info("Run cleanup script")
            ssh.execute("cd /tmp && ./cleanup.sh", raise_on_non_zero=False)

            # Remove update-alternatives config
            if self.role in ['hmaster', 'slave']:
                ssh.execute("update-alternatives --remove hadoop-conf /etc/hadoop/conf.cluster", raise_on_non_zero=False)

            # Delete /etc/bigdata file from the server
            ssh.remove_file("/etc/bigdata")

            # Drop the node from the config context
            if self.role == "hmaster":
                del cc.hmasters[cc.hmasters.index(self)]
            elif self.role == "zookeeper":
                del cc.zookeepers[cc.zookeepers.index(self)]
            elif self.role == "slave":
                del cc.slaves[cc.slaves.index(self)]
            elif self.role == "hive":
                del cc.hives[cc.hives.index(self)]

            # Make template params
            template_params = make_template_params(cc)

            # Reconfigure related nodes
            if self.role == "zookeeper":
                # Update ZooKeepers
                for node in cc.zookeepers:
                    if node != self:
                        rssh = SSHConnection(node.hostname, out)
                        try:
                            rssh.connect()
                            # copy zoo.cfg including new zookeeper to all zookeeper nodes 
                            # Populate templates and send them to the remote server
                            out.info("Populating and applying configuration templates")
                            for content, remote_filename, mode in populate_templates('hbase', 'zookeeper', template_params):
                                # send configurations to new node
                                rssh.send_file_to(content, remote_filename, mode=mode)
                            # restart zookeepers
                            out.info("Restarting ZooKeeper services in node: %s" % node.hostname)
                            rssh.execute("service zookeeper-server stop", raise_on_non_zero=ronz);
                        finally:
                            rssh.disconnect()                                
                            
                # Update and restart HMaster
                if cc.hmaster != None:
                    rssh = SSHConnection(cc.hmaster.hostname, out)
                    try:
                        rssh.connect()
                        
                        # update hbase-site.xml for hmaster 
                        for content, remote_filename, mode in populate_templates('hbase', 'hmaster', template_params):
                            rssh.send_file_to(content, remote_filename, mode=mode)
                           
                        # refresh hmaster (hbase)?? - restart required?
                        rssh.execute("service hbase-master stop", raise_on_non_zero=ronz);
                    finally:
                        rssh.disconnect()

            elif self.role == "slave":
                if cc.hmaster != None:
                    rssh = SSHConnection(cc.hmaster.hostname, out)
                    try:
                        rssh.connect()
                        # hbase:
                        # copy regionservers file including new node to hmaster
                        for content, remote_filename, mode in populate_templates('hbase', 'hmaster', template_params):
                            # send configurations to new node
                            rssh.send_file_to(content, remote_filename, mode=mode)
                        # start hbase services in node
                        # optional: hbase splitting or other reblancing activities?
                        # hadoop/hdfs:
                        # copy slaves file including new node to hmaster
                        # start hadoop services in node
                        # optional: use balacer tool for re-distributing blocks to upscaled cluster
                    finally:
                        rssh.disconnect()

            # Modify exclude file
            if self.role == 'slave' and cc.hmaster != None:
                rssh = SSHConnection(cc.hmaster.hostname, out)
                try:
                    rssh.connect()

                    # Remove this node from the exlude file            
                    exclude_filename = '/etc/hadoop/conf/dfs-hosts-exclude'
                    out.info("Updating file %s" % (exclude_filename))
                    content = rssh.receive_file_from(exclude_filename, '')
                    lines = content.split('\n')
                    remove_line = self.hostname
                    if remove_line in lines:
                        lines.remove(remove_line)
                        out.info("Dropping %s from the exclude list of master" % self.hostname)
                        content = '\n'.join(lines)
                        rssh.send_file_to(content, exclude_filename, mode=0o644)

                        # Signal service
                        out.info("Refreshing master node list")
                        rssh.execute('su - hdfs -s /bin/bash -c "/usr/bin/hadoop dfsadmin -refreshNodes"')
                        rssh.execute('su - hdfs -s /bin/bash -c "/usr/bin/hadoop dfsadmin -report"')
                finally:
                    rssh.disconnect()

            # Regenerate host files of all the managed nodes
            if not cc.options.dns:
                out.info("Regenerating /etc/hosts files of all nodes")
                etc_hosts = "127.0.0.1\tlocalhost localhost.localdomain\n\n"
                etc_hosts += "\n"
                etc_hosts += generate_etc_hosts_content(cc)
                etc_hosts += "\n"
                for node in cc.get_everything():
                    if node.hostname != None:
                        out.debug("  %s" % node.hostname)
                        rssh = SSHConnection(node.hostname, out)
                        try:
                            rssh.connect()
                            rssh.send_file_to(etc_hosts, "/etc/hosts", 0o0644)
                        finally:
                            rssh.disconnect()
                            rssh = None
                    else:
                        out.warn("No hostname for node %s" % (node.ip_address));
            
            # Drop
            self.drop()
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

# Manages a list of list of RPM files
class RpmList(object):
    def __init__(self, node):    
        self.node = node
        self.__rpms_list = [] # List of list of RPM filenames
        self.read()

    # Returns the last RPM list saved
    def get_last(self):
        if len(self.__rpms_list) > 0:
            return self.__rpms_list[-1]
        else:
            return None

    # Pushes set of RPMs to the end of the list
    def push(self, rpms, write=True):
        self.__rpms_list.append(rpms)
        if write:
            self.write()
        return rpms

    # Returns and removes the last set of RPMS
    def pop(self):
        rpms = self.get_last()
        if rpms != None:
            del self.__rpms_list[-1]
            return rpms
        else:
            return None

    # List of RPM lists to a file
    def write(self):
        filename = join(storage_dir, get_cluster_basename(), self.node.role, str(self.node.num), "rpms")
        f = open(filename, "w")
        for rl in self.__rpms_list:
            line = ' '.join(rl)
            if len(line.strip()) > 0:
                f.write("%s\n" % (line.strip()))
        f.close()

    # Read RPM lists related to this node from a file
    def read(self):
        filename = join(storage_dir, get_cluster_basename(), self.node.role, str(self.node.num), "rpms")
        self.__rpms_list = []
        try:
            f = open(filename, "r")
            while True:
                line = f.readline()
                if line.strip() != "":
                    rpms = line.strip().split(' ')
                    self.push(rpms, write=False)
                else:
                    break
            f.close()
            return True
        except IOError:
            return False
    
# Make parameters for the templates
def make_template_params(cc):
    # Template params for HMaster
    template_params = {}
    for hm in cc.hmasters:
        template_params["HMASTER_HOST"] = hm.hostname
        
    # TODO: template_paarams["DATA_DIR"] = database_dir

    template_params["CLUSTER_TYPE"] = cc.type
        
    template_params["REPLICATION_SIZE"] = cc.replication_size

    template_params["LOG_DIR"] = log_dir
    template_params["DATABASE_DIR"] = database_dir
    template_params["TMP_DIR"] = tmp_dir
        
    # Template params for ZooKeepers
    zknames = []
    zkcfg = ""
    for i, zk in enumerate(cc.zookeepers):
        zknames.append(zk.hostname)
        zkcfg += "server.%d=%s:2888:3888\n" % (i, zk.hostname)
    template_params["ZOOKEEPER_HOSTNAMES_COMMA_SEPERATED"] = ','.join(zknames)
    template_params["ZOOKEEPER_CFG"] = zkcfg

    # Template params for Slaves
    slavenames = []
    for sl in cc.slaves:
        slavenames.append(sl.hostname)
    #template_params["SLAVE_HOSTNAMES_COMMA_SEPERATED"] = ','.join(slavenames)
    template_params["SLAVE_HOSTNAMES_BY_LINE"] = '\n'.join(slavenames) + '\n'
    
    # Template params for Regionservers
    slavenames = []
    for sl in cc.slaves:
        slavenames.append(sl.hostname)
    #template_params["REGIONSERVER_HOSTNAMES_COMMA_SEPERATED"] = ','.join(slavenames)
    template_params["REGIONSERVERS_HOSTNAMES_BY_LINE"] = '\n'.join(slavenames) + '\n'

    # Templave params for Hive
    template_params["HIVE_DATABASE_ROOT_PASSWORD"] = hive_database_root_password
    template_params["HIVE_DATABASE_USER"]          = hive_database_user
    template_params["HIVE_DATABASE_USER_PASSWORD"] = hive_database_user_password
    for hive in cc.hives:
        template_params["METASTORE_HOSTNAME"]      = hive.hostname # TODO: this doesn't work, if there are many hives

    return template_params

# ------------------------------------------------------------------------------

# Create the directories. Returns False, if the directories exist already,
# and True on success. If parameter hbase = False, then only Hadoop will 
# be installed
def initialize_directories(out, options, hbase = True):
    bigdata_id_filename = join(storage_dir, "bigdata-id")
    if exists(bigdata_id_filename):
        out.error("Bigdata is already initialized.")
        return False

    if hbase:
        basename = "hbase"
    else:
        basename = "hadoop"

    _mkdirs(join(storage_dir, basename, "hmaster"))
    _mkdirs(join(storage_dir, basename, "hive"))
    if hbase:
        _mkdirs(join(storage_dir, basename, "zookeeper"))
    _mkdirs(join(storage_dir, basename, "slave"))
    _mkdirs(join(storage_dir, "lock"))
    _mkdirs(ssh_log_logpath)

    # Save big data cluster options
    f = open(join(storage_dir, basename, 'cluster-config'), 'w')
    f.write("replication-size: %s\n" % options.replsize)
    f.write("hive-support: %s\n" % options.hive_support)
    f.close()

    # Create a unique bigdata id for the cluster
    f = open(bigdata_id_filename, "w")
    m = hashlib.md5()
    m.update(str(datetime.now()).encode("utf-8"))
    f.write("%s\n" % m.hexdigest())
    f.close()
    return True


# ------------------------------------------------------------------------------

# Returns either "hadoop" or "hbase", depending on cluster type.
# If neither is found, raises an exception.
def get_cluster_basename():
    if exists(join(storage_dir, "hbase")):
        return 'hbase'
    if exists(join(storage_dir, "hadoop")):
        return 'hadoop'
    raise MgmtException("Unable to recognize cluster type, based on directory %s" % storage_dir)

# Returns list of HMaster nodes (usually only one)
def get_hbase_masters():
    nodes = []
    cluster_type = get_cluster_basename()
    for item in listdir(join(storage_dir, cluster_type, "hmaster")):
        if item[0] != ".":
            try:
                nodes.append(HBaseNode("hmaster", cluster_type, int(item)))
            except ValueError:
                pass
    nodes.sort()
    return nodes
    
# Returns list of Zookeepers
def get_zookeepers():
    nodes = []
    cluster_type = get_cluster_basename()
    path = join(storage_dir, cluster_type, "zookeeper")
    if exists(path):
        for item in listdir(path):
            if item[0] != ".":
                try:
                    nodes.append(HBaseNode("zookeeper", cluster_type, int(item)))
                except ValueError:
                    pass
        nodes.sort()
    return nodes

# Returns list of HBase slaves
def get_hbase_slaves():
    nodes = []
    cluster_type = get_cluster_basename()
    for item in listdir(join(storage_dir, cluster_type, "slave")):
        if item[0] != ".":
            try:
                nodes.append(HBaseNode("slave", cluster_type, int(item)))
            except ValueError:
                pass
    nodes.sort()
    return nodes

# Returns list of HMaster nodes (usually only one)
def get_hbase_hives():
    nodes = []
    cluster_type = get_cluster_basename()
    path = join(storage_dir, cluster_type, "hive")
    if exists(path):
        for item in listdir(path):
            if item[0] != ".":
                try:
                    nodes.append(HBaseNode("hive", cluster_type, int(item)))
                except ValueError:
                    pass
        nodes.sort()
    return nodes

# ------------------------------------------------------------------------------


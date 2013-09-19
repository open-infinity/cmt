#!/usr/bin/env python2

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
# See https://confluence.etb.tieto.com:9443/display/TAP/Dynamic+cluster+configuration
# for more information 
#

#
# attach-node.py
# 
# @author Timo Saarinen
# @author Tommi Siitonen
# @version 1.0.0
# @since 1.0.0
#

from __future__ import division       # Python 3 forward compatibility
from __future__ import print_function # Python 3 forward compatibility
from optparse import OptionParser
import sys
import logging
import bigdata
import hbase
import mongodb
from config import *
from common import *
from mgmtexception import MgmtException

# Print command line parameger usage
def print_usage():
    if options.xml:
        print("<xml>Illegal arguments</xml>")
    else:
        print("Usage: attach-node.py [options] <hostname> [ip address]", file=sys.stderr)

# Parse command line arguments
parser = OptionParser()
parser.add_option("--xml", dest="xml",
                  action="store_true", default=False,
                  help="output in XML instead of plain text", metavar="XML")
parser.add_option("--force", "-f", dest="force",
                  action="store_true", default=False,
                  help="attach node even if it's attached already", metavar="FORCE")
parser.add_option("--role-only", dest="roleonly",
                  action="store_true", default=False,
                  help="don't do anything, just tell the next role", metavar="ROLEONLY")
parser.add_option("--dns", dest="dns",
                  action="store_true", default=False,
                  help="don't modify /etc/host files but trust in DNS", metavar="DNS")
parser.add_option("--role", dest="role", 
                  help="manually assign the given role to the node", metavar="ROLE")
parser.add_option("--no-rpms", dest="rpms",
                  action="store_false", default=True,
                  help="don't install RPM packages", metavar="RPMS")
(options, args) = parser.parse_args()
hostname   = None
ip_address = None
if not options.roleonly:
    if len(args) == 0:
        print_usage()
        sys.exit(1)
    elif len(args) == 1 and options.dns:
        hostname = args[0]
    elif len(args) == 2:
        hostname   = args[0]
        ip_address = args[1]
    else:
        print_usage()
        sys.exit(1)

# ------------------------------------------------------------------------
out = OutputWriter(options)

node = None
try:
    # Chec that HBase big data is initialized
    if not bigdata.is_initialized():
        out.error("Big data storage not initialized.")
        sys.exit(1)

    # Lock the HBase directory tree
    if hostname:
        if not bigdata.acquire('node-%s' % hostname, False):
            out.error("The node %s is being configured currently by another process. Waiting until it's complete..." % hostname)
            bigdata.acquire('node-%s' % hostname)
    bigdata.acquire('cluster')
    
    # Get access objects of the nodes
    cc = bigdata.create_config_context(options)

    # Check that the node is not attached already (according to the storage dir)
    reuse_node = None
    for node in cc.everything:
        if node.hostname == hostname and (node.ip_address == ip_address or ip_address == None) and cc.options.force:
            reuse_node = node
        elif node.hostname == hostname:
            raise MgmtException("The hostname %s is in use in the bigdata cluster already" % hostname)
        elif node.ip_address == ip_address and ip_address != None:
            raise MgmtException("The IP address %s is in use in the bigdata cluster already" % ip_address)
    if reuse_node != None:
        out.info("Reusing node %s" % (reuse_node))
        reuse_node.attach(cc, out)
        sys.exit(0)
        

    # Decide the role of the new node
    role = 'unknown'
    if cc.type == "hbase" or cc.type == "hadoop":
        # Decide role
        role  = 'unknown'
        if options.role != None:
            role = options.role
        else:
            (roles, rolemap) = hbase.generate_role_list(cc, len(cc.everything) + 1)
            if len(cc.zookeepers) < rolemap['zookeepers']:
                role = 'zookeeper'
            elif len(cc.hmasters) < rolemap['hmasters']:
                role = 'hmaster'
            elif len(cc.hives) < rolemap['hives']:
                role = 'hive'
            else:
                role = 'slave'
                
        # Choose the node list of config context
        cclist = None
        if role == 'zookeeper': cclist = cc.zookeepers
        elif role == 'hmaster':   cclist = cc.hmasters
        elif role == 'hive':      cclist = cc.hives
        elif role == 'slave':     cclist = cc.slaves
        else: raise MgmtException("Invalid HBase role: %s" % role)
            
        # Configure
        out.role = role
        if not cc.options.roleonly:
            node = hbase.HBaseNode(role, cc.type)
            node.hostname = hostname
            node.ip_address = ip_address
            cclist.append(node)

            # Update local /etc/hosts
            if not cc.options.roleonly and not cc.options.dns:
                update_local_etc_hosts(cc)
            
            # Release cluster lock and start attaching
            bigdata.release('cluster')
            node.attach(cc, out)
    elif cc.type == "mongodb":
        # Decide role
        role  = 'unknown'
        if options.role != None:
            role = options.role
        else:
            (roles, rolemap) = mongodb.generate_role_list(cc, len(cc.everything) + 1)
            if len(cc.configs) < rolemap['configs']:
                role = 'config'
            else:
                role = 'shard'
        
        # Choose the node list of config context
        cclist = None
        if role == 'config':  cclist = cc.configs
        elif role == 'shard': cclist = cc.shards
        else: raise MgmtException("Invalid MongoDB role: %s" % role)

        # Generic attach
        out.role = role
        if not cc.options.roleonly:
            node = mongodb.MongoNode(role)
            node.hostname = hostname
            node.ip_address = ip_address
            cclist.append(node)
            
            # Update local /etc/hosts
            if not cc.options.roleonly and not cc.options.dns:
                update_local_etc_hosts(cc)
                
            # Release cluster lock and start attaching
            bigdata.release('cluster')
            node.attach(cc, out)

    # Update /etc/hosts files of localhost again (since the role of the last one is known)
    bigdata.acquire('cluster')
    cc = bigdata.create_config_context(options)
    if not cc.options.roleonly and not cc.options.dns:
        update_local_etc_hosts(cc)
    bigdata.release('cluster')

    # Tell user the next node role
    if cc.options.roleonly and cc.options.xml == False:
        out.info("The next node will be a %s node of %s cluster" % (role, cc.type))

except MgmtException as e:
    out.error(str(e))
    if node != None:
        try:
            node.drop()
        except:
            pass
    sys.exit(1)
finally:
    # Release all locks
    bigdata.release_all()
    if options.xml:
        print(out.to_xml())
    logging.shutdown()


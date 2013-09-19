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
# detach-node.py
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
import hbase
import logging
import bigdata
from hbase import *
from config import *
from common import *
from mgmtexception import MgmtException

# Print command line parameger usage
def print_usage():
    if options.xml:
        print("<xml>Illegal arguments</xml>")
    else:
        print("Usage: detach-node.py [options] <hostname(s)>", file=sys.stderr)
        print("Hostname ALL will remove all nodes of the bigdata cluster.", file=sys.stderr)

# Parse command line arguments
parser = OptionParser()
parser.add_option("--xml", dest="xml",
                  action="store_true", default=False,
                  help="output in XML instead of plain text", metavar="XML")
parser.add_option("--force", dest="force",
                  action="store_true", default=False,
                  help="ignore decommission state that can result lose of important data", metavar="XML")
parser.add_option("--dns", dest="dns",
                  action="store_true", default=False,
                  help="don't modify /etc/host files", metavar="DNS")
parser.add_option("--no-rpms", dest="rpms",
                  action="store_false", default=True,
                  help="don't uninstall RPM packages", metavar="RPMS")
(options, args) = parser.parse_args()
hostnames = []
if len(args) == 0:
    print_usage()
    sys.exit(1)
elif len(args) >= 1:
    hostnames = args
else:
    print_usage()
    sys.exit(1)

# HBase ------------------------------------------------------------------------
out = OutputWriter(options)

try:
    # Chec that HBase big data is initialized
    if not bigdata.is_initialized():
        out.error("Big data storage not initialized.")
        sys.exit(1)

    # Get access objects of the nodes
    cc = bigdata.create_config_context(options)

    # Handle the special case for all        
    if hostnames == ['ALL']:
        if cc.type == "hbase" or cc.type == 'hadoop':
            hostnames = []
            for host in cc.hives:
                hostnames.append(host.hostname)
            for host in cc.slaves:
                hostnames.append(host.hostname)
            for host in cc.hmasters:
                hostnames.append(host.hostname)
            for host in cc.zookeepers:
                hostnames.append(host.hostname)
        elif cc.type == "mongodb":
            hostnames = []
            for host in cc.shards:
                hostnames.append(host.hostname)
            for host in cc.configs:
                hostnames.append(host.hostname)

    # Iterate hostnames
    for hostname in hostnames:
        if hostname:
            # Lock the big data directory tree
            if bigdata.acquire("node-%s" % hostname, False):
                # Reread the config after each removal
                cc = bigdata.create_config_context(options)
            
                # Find the node
                node = None
                for n in cc.everything:
                    if n.hostname == hostname:
                        node = n
                if node == None:
                    raise MgmtException("Couldn't find node %s" % hostname)

                # Do the detach
                node.detach(cc, out)

                # Update /etc/hosts files of localhost
                if not cc.options.dns:
                    update_local_etc_hosts(cc)
            else:
                out.warn("Host %s is being configured by another process" % hostname)

except MgmtException as e:
    out.error(str(e))
    sys.exit(1)
finally:
    # Release the lock
    bigdata.release_all()
    if options.xml:
        print(out.to_xml())
    logging.shutdown()



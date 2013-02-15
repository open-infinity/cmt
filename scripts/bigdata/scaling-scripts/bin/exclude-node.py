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
# Exclude slave host from the cluster to prepare a safe detach.
#

#
# exclude-node.py
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
        print("Usage: exclude-node.py [options] <hostname(s)>", file=sys.stderr)

# Parse command line arguments
parser = OptionParser()
parser.add_option("--xml", dest="xml",
                  action="store_true", default=False,
                  help="output in XML instead of plain text", metavar="XML")
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

    # Lock the HBase directory tree
    if not bigdata.lock():
        out.error("Lock file detected. Aborting.")
        sys.exit(1)
    
    # Get access objects of the nodes
    cc = bigdata.create_config_context(options)
    
    # Handle the special case for all        
    if hostnames == ['ALL']:
        hostnames = []
        if cc.type == "hbase":
            for host in cc.slaves:
                hostnames.append(host.hostname)
        elif cc.type == "mongodb":
            for host in cc.shards:
                hostnames.append(host.hostname)

    # Iterate hostnames
    for hostname in hostnames:
        # Find the node
        node = None
        for n in cc.everything:
            if n.hostname == hostname:
                node = n
        if node == None:
            raise MgmtException("Couldn't find node %s" % hostname)

        # Check role
        if node.role == 'slave' or node.role == "shard":
            node.exclude(cc, out)
        else:
            out.info("Skipping a %s because only slave can be excluded" % (node.role))

except MgmtException as e:
    out.error(str(e))
    sys.exit(1)
finally:
    # Release the lock
    bigdata.unlock()
    if options.xml:
        print(out.to_xml())
    logging.shutdown()


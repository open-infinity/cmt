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
# ask-roles.py
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
        print("Usage: ask-roles.py [options] [number of nodes]", file=sys.stderr)

# Parse command line arguments
parser = OptionParser()
parser.add_option("--xml", dest="xml",
                  action="store_true", default=False,
                  help="output in XML instead of plain text", metavar="XML")
(options, args) = parser.parse_args()
node_count = 1
if len(args) == 0:
    print_usage()
    sys.exit(1)
elif len(args) == 1:
    node_count = int(args[0])
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

    # List roles
    cc = bigdata.create_config_context(options)
    roles = []
    if cc.type == "hbase":
        (roles, counts) = hbase.generate_role_list(cc, node_count)        
    elif cc.type == "hadoop":
        (roles, counts) = hbase.generate_role_list(cc, node_count)        
    elif cc.type == "mongodb":
        (roles, counts) = mongodb.generate_role_list(cc, node_count)

    # Generate XML
    xml =  "  <roles>\n"
    out.info('The node roles will be assigned according to the included list:')
    for role in roles:  
        xml += "    <role>%s</role>\n" % role
        if not options.xml:
            out.info("  %s" % role)
    xml += "  </roles>\n"
    out.extra_xml += xml
    out.role = ''
        

except MgmtException as e:
    out.error(str(e))
    if node != None:
        node.drop()
    sys.exit(1)
finally:
    # Release all locks
    bigdata.release_all()
    if options.xml:
        print(out.to_xml())
    logging.shutdown()


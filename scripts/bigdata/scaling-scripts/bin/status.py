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
# status.py
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
from config import *
from common import *
from mgmtexception import MgmtException
import bigdata

# Print command line parameger usage
def print_usage():
    if options.xml:
        print("<xml>Illegal arguments</xml>")
    else:
        print("Usage: status.py [options|--ignore-lock]", file=sys.stderr)

# Parse command line arguments
parser = OptionParser()
parser.add_option("--xml", dest="xml",
                  action="store_true", default=False,
                  help="output in XML instead of plain text", metavar="XML")
(options, args) = parser.parse_args()

out = OutputWriter(options)

try:
    # Get access objects of the nodes
    cc = bigdata.create_config_context(options)

    # Iterate all
    out.extra_xml += "  <NodeStatuses>\n"
    if len(cc.everything) > 0:
        last_role = None
        for node in cc.everything:
            if node.role != last_role:
                out.info("%s" % (node.role))
                last_role = node.role
            if node.type == "hbase" or node.type == 'hadoop':
                out.extra_xml += "    <NodeStatus hostname=\"%s\" ip-address=\"%s\" role=\"%s\" num=\"%s\" state=\"%s\" description=\"%s\" />\n" % \
                    (node.hostname, node.ip_address, node.role, node.num, node.config_state, node.config_description)
                out.info("  #%s %s (%s) %s" % (node.num, node.hostname, node.ip_address, node.config_state))
                if node.config_description != "":
                    out.info("    %s" % node.config_description.replace('\n', '\n    '))
            elif node.type == "mongodb":
                out.extra_xml += "    <NodeStatus hostname=\"%s\" ip-address=\"%s\" role=\"%s\" num=\"%s\" state=\"%s\" description=\"%s\" replsetnum=\"%s\" />\n" % \
                    (node.hostname, node.ip_address, node.role, node.num, node.config_state, node.config_description, nonone(node.replsetnum, node.replsetnum, ""))
                rsstr = nonone(node.replsetnum, " rs%s" % node.replsetnum, "")                    
                out.info("  #%s %s (%s)%s %s" % (node.num, node.hostname, node.ip_address, rsstr, node.config_state))
                if node.config_description != "":
                    out.info("    %s" % node.config_description.replace('\n', '\n    '))
            else:
                out.info("  #%s %s (%s)" % (node.num, node.hostname))
    else:
        out.info("No nodes attached to the cluster.")
        out.status_description = "No nodes attached to the cluster."
    out.extra_xml += "  </NodeStatuses>\n"

except MgmtException as e:
    out.error(str(e))
finally:
    if options.xml:
        print(out.to_xml())


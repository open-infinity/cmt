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
# disk-usage.py
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
import re
import logging

import hbase
import mongodb
import bigdata
from hbase import *
from config import *
from common import *
from mgmtexception import MgmtException
import worker

# Print command line parameger usage
def print_usage():
    if options.xml:
        print("<xml>Illegal arguments</xml>")
    else:
        print("Usage: estimate-scaling.py [options]", file=sys.stderr)

# Parse command line arguments
parser = OptionParser()
parser.add_option("--xml", dest="xml",
                  action="store_true", default=False,
                  help="output in XML instead of plain text", metavar="XML")
parser.add_option("--estimate", dest="estimate", 
                  help="estimate disk usage after scaling, cluster size as parameter", metavar="ESTIMATE")
(options, args) = parser.parse_args()
num = 0
if len(args) > 0:
    print_usage()
    sys.exit(1)

out = OutputWriter(options)
# ------------------------------------------------------------------------------
# Retrieves disk data usage and capasity from the remove node
class DiskSpaceAnalyzerTask(object):
    def __init__(self, node):
        self.node = node
        self.used = None
        self.capacity = None
        self.available = None
        
    def execute(self):
        global out
    
        ssh = SSHConnection(self.node.hostname, out)
        try:
            ssh.connect()
            (r, sout, serr, all) = ssh.execute("df -P /data/")
            if r == 0:
                mo = re.compile(r"^.*\s+\d+\s+(\d+)\s+(\d+)\s+(\S+)\s+\S+\s*$").match(sout)
                if mo != None:
                    self.used = int(mo.group(1))
                    self.available = int(mo.group(2))
                    self.capacity = self.used + self.available
        except MgmtException, e:
            pass #out.debug(str(e))
        finally:
            ssh.disconnect()

# Retrieves the current disk usage of the cluster and offers totals per node 
# types. The data gathering will be done in threads.
class DiskSpaceAnalyzer(object):
    def __init__(self, cc, node_roles):
        self.__mgr = worker.WorkerManager(min(50, len(cc.everything)))
        self.__node_roles = node_roles
        self.__cc = cc
        
    def run(self):
        cc = self.__cc
        try:
            tasks = []
            for node in cc.everything:
                if node.role in self.__node_roles:
                    task = DiskSpaceAnalyzerTask(node)
                    tasks.append(task)
                    self.__mgr.add_task(task)
            self.__mgr.wait_for_completion()

            # Calculate per-role usages results
            usage_per_role = {}
            total_used = 0
            total_capacity = 0
            for role in self.__node_roles:
                group_total_used = 0
                group_total_capacity = 0
                for task in tasks:
                    if task.node.role == role:
                        if task.capacity != None and task.capacity > 0 and task.used > 0:
                            usage = 100.0 * (task.used / task.capacity)
                            total_used += task.used
                            total_capacity += task.capacity
                            group_total_used += task.used
                            group_total_capacity += task.capacity
                group_total_usage = 100.0 * (group_total_used / group_total_capacity)
                if group_total_capacity > 0:
                    usage_per_role[role] = 100.0 * (group_total_used / group_total_capacity)
                else:
                    usage_per_role[role] = None
            total_usage = 100.0 * (total_used / total_capacity)
            
            # Print results to stdout
            out.info("Disk usage per role:")
            for role in usage_per_role:
                usage = usage_per_role[role]
                if usage != None:
                    out.info("  %ss: %.1f%%" % (role, usage))
                else:
                    out.info("  %ss:  ---" % (role))
            out.info("  Total: %.1f%%" % total_usage)
            
            # Print results to XML
            out.extra_xml += "  <current-disk-usage total=\"%.1f\">\n" % total_usage
            for role in usage_per_role:
                usage = usage_per_role[role]
                if usage != None:
                    out.extra_xml += "    <disk-usage role=\"%s\" total=\"%.1f\">\n" % (role, usage)
                else:
                    out.extra_xml += "    <disk-usage role=\"%s\" total=\"\">\n" % (role)
            out.extra_xml += "  </current-disk-usage\n"
            
            # Calcualte an estimate for changed cluster size
            if options.estimate:
                current_cluster_size = len(cc.everything)
                new_cluster_size = int(options.estimate)

                # Get role list
                roles = []
                if cc.type == "hbase" or cc.type == 'hadoop':
                    (roles, counts) = hbase.generate_role_list(cc, current_cluster_size)
                elif cc.type == "mongodb":
                    (roles, counts) = mongodb.generate_role_list(cc, current_cluster_size)
                else:
                    raise MgmtException("Unknown cluster type: %s" % cc.type)
                    
                if new_cluster_size <= current_cluster_size:
                    # Make role counts lists
                    role_counts = {}
                    for role in self.__node_roles:
                        role_counts[role] = 0
                    for node in cc.everything:
                        if node.role in role_counts:
                            role_counts[node.role] += 1
                    while len(roles) > new_cluster_size:
                        r = roles.pop()
                        role_counts[r] -= 1
                        
                    # Recount after down-scaling
                    scaling_ok = True
                    scaling_usage_per_role = {}
                    for role in self.__node_roles:
                        role_total_used = 0
                        role_total_capacity = 0
                        for task in tasks:
                            if task.node.role == role:
                                if task.capacity != None and task.capacity > 0 and task.used > 0:
                                    role_total_used += task.used
                                    if role_counts[role] > 0:
                                        role_total_capacity += task.capacity
                                        role_counts[role] -= 1
                        if role_total_capacity > 0:
                            scaling_usage_per_role[role] = (100.0 * role_total_used / role_total_capacity)
                            if role_total_used > 0.8 * role_total_capacity:
                                scaling_ok = False
                        else:
                            scaling_usage_per_role[role] = None
                            scaling_ok = False

                   
                    # Print stdout
                    out.info("\nDisk usage per role after scaling")
                    for role in scaling_usage_per_role:
                        scaling_usage = scaling_usage_per_role[role]
                        if scaling_usage != None:
                            out.info("  %ss: %.1f%%" % (role, scaling_usage))
                        else:
                            out.info("  %ss:  ---" % (role))
                    if not scaling_ok:
                        out.warn("WARNING: down-scaling can result loss of data!");

                    # Print XML
                    if scaling_ok:
                        out.extra_xml += "  <scaling-disk-usage result=\"ok\">\n"
                    else:
                        out.extra_xml += "  <scaling-disk-usage result=\"danger\">\n"
                    for role in scaling_usage_per_role:
                        scaling_usage = scaling_usage_per_role[role]
                        if scaling_usage != None:
                            out.extra_xml += "    <disk-usage role=\"%s\" usage=\"%.1f\"/>\n" % (role, scaling_usage)
                        else:
                            out.extra_xml += "    <disk-usage role=\"%s\" usage=\"\"/>\n" % (role)
                    out.extra_xml += "  </scaling-disk-usage>\n"

        finally:
            self.__mgr.stop_workers()


# ------------------------------------------------------------------------------

try:
    # Check that HBase big data is initialized
    if not bigdata.is_initialized():
        out.error("Big data storage not initialized.")
        sys.exit(1)

    # Get access objects of the nodes
    cc = bigdata.create_config_context(options)
    if cc.type == 'hbase':
        a = DiskSpaceAnalyzer(cc, ['slave', 'zookeeper', 'hmaster'])
        a.run()
    elif cc.type == 'hadoop':
        a = DiskSpaceAnalyzer(cc, ['slave', 'hmaster'])
        a.run()
    elif cc.type == 'mongodb':
        a = DiskSpaceAnalyzer(cc, ['shard', 'config'])
        a.run()

except MgmtException as e:
    out.error(str(e))
    sys.exit(1)
finally:
    # Release the lock
    bigdata.release_all()
    if options.xml:
        print(out.to_xml())
    logging.shutdown()


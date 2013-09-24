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
# initialize.py
# 
# @author Timo Saarinen
# @author Tommi Siitonen
# @version 1.0.0
# @since 1.0.0
#

from __future__ import print_function
from optparse import OptionParser
import sys
import hbase
import mongodb
from config import *
from hbase import *
from common import *

# Print command line parameger usage
def print_usage():
    if options.xml:
        print("<xml>Illegal arguments</xml>")
    else:
        print("Usage: initialize.py [options] <hbase|mongodb>", file=sys.stderr)

# Parse command line arguments
parser = OptionParser()
parser.add_option("--xml", dest="xml",
                  action="store_true", default=False,
                  help="output in XML instead of plain text", metavar="XML")
parser.add_option("--hive-support", dest="hive_support",
                  action="store_true", default=True,
                  help="HBase: include Hive support in cluster", metavar="HIVE")
parser.add_option("--pig-support", dest="pig_support",
                  action="store_true", default=True,
                  help="HBase: include Pig support in cluster", metavar="PIG")
parser.add_option("--replication-size", dest="replsize", default=3,
                  help="replica set size", metavar="REPL")
(options, args) = parser.parse_args()
if len(args) == 0:
    print_usage()
    sys.exit(1)
cluster_type = args[0]

# Out
out = OutputWriter(options)

# Check contradictions
if options.pig_support and not options.hive_support:
    out.warn("Pig command line tools are not available without Hive")

# Initialize HBase
if cluster_type == "hbase":
    hbase.initialize_directories(out, options, hbase=True)
elif cluster_type == "hadoop":
    hbase.initialize_directories(out, options, hbase=False)
elif cluster_type == "mongodb":
    mongodb.initialize_directories(out, options)
else:
    out.error("Invalid cluster type. Currently only hbase is supported.")

if options.xml:
    print(out.to_xml())


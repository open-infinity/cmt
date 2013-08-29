
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
# common.py
# 
# @author Timo Saarinen
# @author Tommi Siitonen
# @version 1.0.0
# @since 1.0.0
#

from __future__ import division       # Python 3 forward compatibility
from __future__ import print_function # Python 3 forward compatibility
import sys
from os.path import join, exists
from os import mkdir, makedirs, remove, system
from dircache import listdir
from datetime import datetime
from sshconn import SSHConnection
import shutil
import cStringIO as string_io
import ConfigParser
import re
import mgmtexception

from config import *

# Output writer
class OutputWriter(object):
    def __init__(self, options):
        self.options = options
        
        # For XML mode
        self.status = "success" # "success", "error", "pending"
        self.status_description = ""
        self.warnings = []
        self.errors = []
        self.extra_xml = ""
        self.role = 'unknown'

    # Print a debug message
    def debug(self, msg):
        if self.options.xml == False:
            print(msg)
            
    # Print an info message
    def info(self, msg):
        if self.options.xml == False:
            print(msg)
            
    # Print/save a warning message
    def warn(self, msg):
        if self.options.xml == False:
            print(msg, file=sys.stderr)
        else:
            self.warnings.append(msg)
        
    # Print/save an error message
    def error(self, msg):
        if self.options.xml == False:
            print(msg, file=sys.stderr)
        else:
            if self.status == "success":
                self.status = "error"   
            self.errors.append(msg)

    # Create an XML message for admin console
    def to_xml(self):
        # Decide the description
        if self.status_description:
            desc = self.status_description
        elif len(self.errors):
            desc = self.errors[0]
        else:
            desc = ""
    
        # Return XML
        xml =  '<?xml version="1.0" encoding="UTF-8" ?>\n'
        xml += '<BigDataMgmt status="' + self.status + '" description="' + desc + '" role="' + self.role +  '">\n'
        xml += '  <Messages>\n'
        for err in self.errors:
            xml += '    <ErrorMessage>' + err + '</ErrorMessage>\n'
        for warn in self.warnings:
            xml += '    <WarningMessage>' + warn + '</WarningMessage>\n'
        xml += '  </Messages>\n'
        xml += self.extra_xml
        xml += '</BigDataMgmt>\n'
        return xml

# If the first argument is None, returns the third one, but otherwise the second one.
def nonone(none_test, positive, negative):
    if none_test != None:
        return positive
    else:
        return negative

# Read unique id for the big data
def read_bigdata_unique_id():
    f = open(join(storage_dir, "bigdata-id"), "r")
    u = f.read()
    f.close()
    return u.strip()
    

# Super class for HBaseNode and MongoNode
class Node(object):
    dropped = False

    # Returns contents of the file as string or None if the file doesn't exist
    def read(self, role, num, fname, line = 1):
        path = join(storage_dir, self.type, role, str(num))
        filename = join(path, fname)
        if exists(filename):
            f = open(filename, "r")
            while line > 1:
                f.readline()
                line = line - 1
            r = f.readline().strip()
            f.close()
            return r.replace('\\n', '\n')
        else:
            return None

    # Remove all data related to this node in the bigdata storage directory in 
    # the management node
    def drop(self):
        self.dropped = True
        path = join(storage_dir, self.type, self.role, str(self.num))
        shutil.rmtree(path) # system("rm -fR \"%s\"" % path)

    # Writes the given value to file. If the value is None, the file will be deleted
    def write(self, role, num, fname, value, line_num = -1):
        # Don't write anything if this node was dropped
        if self.dropped:
            return
    
        if line_num != -1 and value != None:
            value = value.replace('\n', '\\n')

        path = join(storage_dir, self.type, role, str(num))
        if not exists(path): mkdir(path)
        filename = join(path, fname)
        if line_num == -1:
            if value != None:
                # Write
                f = open(filename, "w")
                f.write("%s\n" % value)
                f.close()
            else:
                # Remove the file if the value is None
                if exists(filename):
                    remove(filename)
        else:
            # Read existing file
            if value == None: value = ""
            lines = []
            if exists(filename):
                f = open(filename, "r")
                while True:
                    line = f.readline()
                    if line != "":
                        lines.append(line.strip())
                    else:
                        break
                f.close()
            if len(lines) >= line_num:
                lines[line_num - 1] = value
            else:
                while len(lines) < line_num:
                    lines.append("")
                lines[line_num - 1] = value
            self.write(role, num, fname, '\n'.join(lines))

    # The nodes are equal if hostnames match
    def __eq__(self, other):
        if other != None:
            return self.hostname == other.hostname
        else:
            return False

    # Print presentation
    def __repr__(self):
        return "%s/%s (%s)" % (self.role, self.num, self.hostname)
        
    # String presentation
    def __str__(self):
        return self.__repr__()

    # Comparator for sorting        
    def __lt__(self, other):
        return self.num < other.num

    def get_hostname(self): return self.read(self.role, self.num, "hostname")
    def set_hostname(self, value): self.write(self.role, self.num, "hostname", value)
    hostname = property(get_hostname, set_hostname)        

    def get_ip_address(self): return self.read(self.role, self.num, "ip-address")
    def set_ip_address(self, value): self.write(self.role, self.num, "ip-address", value)
    ip_address = property(get_ip_address, set_ip_address)        

    def get_config_timestamp(self): return datetime(self.read(self.role, self.num, "config-state"), 1)
    def set_config_timestamp(self, value): self.write(self.role, self.num, "config-state", str(value), 1)
    config_timestamp = property(get_config_timestamp, set_config_timestamp)        

    def get_config_state(self): return self.read(self.role, self.num, "config-state", 2)
    def set_config_state(self, value): self.write(self.role, self.num, "config-state", value, 2)
    config_state = property(get_config_state, set_config_state)        

    def get_config_description(self): return self.read(self.role, self.num, "config-state", 3)
    def set_config_description(self, value): self.write(self.role, self.num, "config-state", value, 3)
    config_description = property(get_config_description, set_config_description)

    def put_config_state(self, state, description = None):
        self.config_state       = state
        self.config_description = description
        self.config_timestamp   = datetime.now()

    def get_node_status(self): return self.read(self.role, self.num, "node-status", 2)
    def set_node_status(self, value): self.write(self.role, self.num, "node-status", value, 2)
    node_status = property(get_node_status, set_node_status)

    def get_node_status_timestamp(self): return datetime(self.read(self.role, self.num, "node-status", 1))
    def set_node_status_timestamp(self, value): self.write(self.role, self.num, "node-status", str(value), 1)
    node_status_timestamp = property(get_node_status_timestamp, set_node_status_timestamp)

    def put_node_status(self, status):
        self.config_status = status
        self.config_status_timestamp = datetime.now()

    # Check operating system version
    def check_operating_system_version(self, cc, out, ssh):
        # Check operating system type and version
        c = ssh.receive_file_from("/etc/issue.net", "")
        mo = re.compile(r".*CentOS.*? release (\d+)\.(\d+).*", re.DOTALL).match(c)
        if mo != None:
            major_version = int(mo.group(1))
            minor_version = int(mo.group(2))
            if major_version != 6:
                raise mgmtexception.MgmtException("CentOS version %d.%d in %s is not supported" % (major_version, minor_version, self.hostname))
            out.info("The remote operating system is CentOS %d.%d" % (major_version, minor_version))
        else:
            raise mgmtexception.MgmtException("The remote operating system type is not CentOS 6.x but something else: %s" % c)

    # Check remote hostname
    def check_remote_hostname(self, cc, out, ssh):
        # Check remote hostname (CentOS/RedHat approach)
        c = ssh.receive_file_from("/etc/sysconfig/network", "")
        mo = re.compile(r".*?HOSTNAME=(\S+)\s+.*?", re.DOTALL).match(c)
        if mo != None:
            remote_hostname = mo.group(1)
            if remote_hostname != self.hostname:
                raise mgmtexception.MgmtException("Given host name %s doesn't match with the remote hostname %s" % (self.hostname, remote_hostname))
            out.info("The remote hostname is %s" % remote_hostname)
        else:
            out.warn("Warning: couldn't check validity of the hostname")

    # Check if there is an installation in the node already
    def check_possible_big_data_installation(self, cc, out, ssh):
        try:
            # Read the current config
            c = ssh.receive_file_from("/etc/bigdata", "")
            config = ConfigParser.ConfigParser()
            config.readfp(string_io.StringIO(c))
            
            # Check unique id and role
            if config.has_section('bigdata'):
                try:
                    remote_role = config.get("bigdata", "node-role")
                    if remote_role != self.role and remote_role != "":
                        self.drop()
                        raise mgmtexception.MgmtException("Configuration aborted because the node seem to have another role '%s' already" % remote_role)
                    if config.get("bigdata", "bigdata-id") != read_bigdata_unique_id():
                        self.drop()
                        raise mgmtexception.MgmtException("Unexpected big data id in the node that likely means the node is managed by a different management node instance")
                except ConfigParser.NoOptionError:
                    pass
                
            # Write a new config
            config = ConfigParser.ConfigParser()
            config.add_section("bigdata")
            config.set("bigdata", "bigdata-id", read_bigdata_unique_id())
            config.set("bigdata", "node-role", self.role)
            sio = string_io.StringIO()
            config.write(sio)
            ssh.send_file_to(sio.getvalue(), "/etc/bigdata")
        finally:
            pass

    # Regenerate host files of all the managed nodes
    def regenerate_etc_hosts_files(self, cc, out):
        if not cc.options.dns:
            BIGDATA_BEGIN_LINE = "# Bigdata begin -----------------------------------------------"
            BIGDATA_END_LINE   = "# Bigdata end -------------------------------------------------"
            
            out.info("Regenerating /etc/hosts files of all nodes")
            gen_content = generate_etc_hosts_content(cc)
            for node in cc.get_everything():
                out.debug("  %s" % node.hostname)
                rssh = SSHConnection(node.ip_address, out)
                try:
                    rssh.connect()
                    
                    etc_hosts = rssh.receive_file_from("/etc/hosts", "")
                    etc_hosts_splitted = etc_hosts.split('\n')
                    if (BIGDATA_BEGIN_LINE in etc_hosts_splitted) and (BIGDATA_END_LINE in etc_hosts_splitted):
                        a = etc_hosts_splitted.index(BIGDATA_BEGIN_LINE)
                        b = etc_hosts_splitted.index(BIGDATA_END_LINE)
                        c = etc_hosts_splitted[:(a + 1)] + [gen_content] + etc_hosts_splitted[b:]
                        etc_hosts = '\n'.join(c)
                    else:
                        etc_hosts += '\n' + BIGDATA_BEGIN_LINE + '\n' + gen_content + BIGDATA_END_LINE + '\n'

                    out.debug(etc_hosts)                
                    rssh.send_file_to(etc_hosts, "/etc/hosts", 0o0644)
                finally:
                    rssh.disconnect()
                    rssh = None
            

#            # ---- OLD ----            
#            etc_hosts = "127.0.0.1\tlocalhost localhost.localdomain\n\n"
#            etc_hosts += "\n"
#            etc_hosts += generate_etc_hosts_content(cc)
#            etc_hosts += "\n"
#            for node in cc.get_everything():
#                out.debug("  %s" % node.hostname)
#                rssh = SSHConnection(node.ip_address, out)
#                try:
#                    rssh.connect()
#                    rssh.send_file_to(etc_hosts, "/etc/hosts", 0o0644)
#                finally:
#                    rssh.disconnect()
#                    rssh = None


# Returns content to be added to /etc/hosts files (both local and remote)
def generate_etc_hosts_content(cc, new_hostname = None, new_ip_address = None):
    content = ""
    for node in cc.get_everything():
        if node.hostname != None and node.ip_address != None:
            content += "%s\t%s\t# %s %s\n" % (node.ip_address, node.hostname, node.role, node.num)
    if new_hostname != None and new_ip_address != None:
        content += "%s\t%s\t# new\n" % (new_ip_address, new_hostname)
    return content

# Update the local etc hosts
def update_local_etc_hosts(cc, new_hostname = None, new_ip_address = None):
    f = open("/etc/hosts", "r")
    etc_hosts = ""
    hosts_added = False
    inside_bigdata_hosts_list = False
    BIGDATA_BEGIN_LINE = "# Bigdata begin -----------------------------------------------\n"
    BIGDATA_END_LINE   = "# Bigdata end -------------------------------------------------\n"
    while True:
        line = f.readline()
        if line == BIGDATA_BEGIN_LINE:
            inside_bigdata_hosts_list = True
            
            # Generate bigdata nodes list
            etc_hosts += BIGDATA_BEGIN_LINE;
            etc_hosts += generate_etc_hosts_content(cc, new_hostname, new_ip_address)
            etc_hosts += BIGDATA_END_LINE;
            hosts_added = True
        elif line == BIGDATA_END_LINE:
            inside_bigdata_hosts_list = False
        elif line == "" or line == None:
            if not hosts_added:
                # Generate bigdata nodes list
                etc_hosts += BIGDATA_BEGIN_LINE;
                etc_hosts += generate_etc_hosts_content(cc, new_hostname, new_ip_address)
                etc_hosts += BIGDATA_END_LINE;
                hosts_added = True
            break
        else:
            if not inside_bigdata_hosts_list:
                etc_hosts += line
    f.close()

    f = open("/etc/hosts", "w")
    f.write(etc_hosts)
    f.close()

# Reads hashmap from a file. Expects key-value pairs to be separated by colon (:)
def read_hashmap(filename):
    h = {}

    f = open(filename, 'r')
    while True:
        line = f.readline()
        if line == "" or line == None:
            break
        mo = re.compile(r"^\s*(\S+)\s*:\s*(.*?)\s*$").match(line.strip())
        if mo != None:
            if len(mo.group(1)) > 0 and mo.group(1)[0] != '#':
                h[mo.group(1)] = mo.group(2)
        
    f.close()
    
    return h


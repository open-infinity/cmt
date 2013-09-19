
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
# sshconn.py
# 
# @author Timo Saarinen
# @author Tommi Siitonen
# @version 1.0.0
# @since 1.0.0
#

from __future__ import division       # Python 3 forward compatibility
from __future__ import print_function # Python 3 forward compatibility
from paramiko import SSHClient, WarningPolicy, AutoAddPolicy, AuthenticationException, SSHException
import sys, traceback, time
import tempfile
import os
import select
import logging
from mgmtexception import MgmtException
from os.path import join, basename, exists, dirname, isdir
from dircache import listdir
from config import *
import time

# Recursively try to find the file under the directory tree. Returns None if
# it's not found.
def find_file(directory, filename):
    for entry in listdir(directory):
        full = join(directory, entry)
        if isdir(full):
            fn = find_file(full, filename)
            if fn != None:
                return fn
        elif entry == filename:
            return full
    return None

# Returns (stderr, stdout) of two file-like streams and writes both to the logger
def read_stderrout(channel, logger):
    out = []
    err = []

    while True:
        (r, w, e) = select.select([channel], [], [], 10)
        if r:
            got_data = False
            if channel.recv_ready():
                data = r[0].recv(4096)
                if data:
                    got_data = True
                    data = data.replace('\n', '')
                    logger.debug("  %s" % data)
                    out.append(data)
            if channel.recv_stderr_ready():
                data = r[0].recv_stderr(4096)
                if data:
                    got_data = True
                    data = data.replace('\n', '')
                    logger.warn("  %s" % data)
                    err.append(data)
            if not got_data:
                break
            
    return ('\n'.join(out), '\n'.join(err))

# SSH connection object which is actually mostly a convenience wrapper around
# paramiko
class SSHConnection(object):
    def __init__(self, hostname, out):
        self.__hostname = hostname
        self.__client = SSHClient()
        self.__client.load_system_host_keys()
        self.__out = out
        
        if hostname == None:
            raise MgmtException("SSH connection can't be opened because no hostname was given")
        
        # Set up logging
        self.__logger = logging.getLogger('sshconn-%s' % hostname)
        
        logfilename = join(ssh_log_logpath, "ssh-%s.log" % (hostname))
        self.__hdlr = logging.FileHandler(logfilename)
        formatter = logging.Formatter('%(asctime)s %(levelname)s %(message)s')
        self.__hdlr.setFormatter(formatter)
        self.__logger.addHandler(self.__hdlr) 
        self.__logger.setLevel(logging.DEBUG)
        #self.__out.info("Writing SSH log file to %s" % (logfilename))

    # Connect to the host. It's expected that key-authentication works 
    # transparently
    def connect(self, username = "root", password=None, retries=2):
        #self.__out.info("Connecting to %s" % self.__hostname)
        try:
            if password != None:
                #self.__out.debug("Using password authentication")
                self.__client.set_missing_host_key_policy(AutoAddPolicy())
                self.__client.connect(self.__hostname, username=username, password=password, timeout=180)
                self.__logger.info("Connected %s using password authentication ------------------------------" % (self.__hostname))
            else:
                #self.__out.debug("Using key-based authentication")
                self.__client.set_missing_host_key_policy(AutoAddPolicy())
                self.__client.connect(self.__hostname, username=username, look_for_keys=True, timeout=180)
                self.__logger.info("Connected %s using key-based authentication -----------------------------" % (self.__hostname))
            #self.__out.info("Connected to %s" % self.__hostname)
            return True
        except AuthenticationException:
            self.__logger.critical("Connection to %s failed" % (self.__hostname))
            raise MgmtException("SSH authentication to %s failed for user %s" % (self.__hostname, username))
        except SSHException as e:
            if retries > 0:
                time.sleep(5)
                return self.connect(username, password, retries - 1)
            else:
                raise MgmtException("SSH connection error: %s" % (e))

    def disconnect(self):
        self.__client.close()
        self.__client = None
        self.__logger.info("Disconnected from %s\n" % (self.__hostname))
        self.__logger.removeHandler(self.__hdlr)
        self.__logger = None

    # Install list of RPM packages from the repositories. Retries twice using
    # 10 second interval. Return values are same as for execute method.
    def install(self, packages):
        return self.execute("yum --assumeyes --quiet install %s" % ' '.join(packages), retry_interval = 10, retry_count = 5)

    # Uninstalls list of RPM packages. Retries twice using 10 sec interval.
    def uninstall(self, packages):
        return self.execute("yum --assumeyes --quiet erase %s" %  ' '.join(packages), retry_interval = 10, retry_count = 2, raise_on_non_zero=False)

    # Execute a command in the remote server. Returns a tuple:
    # (return code, stdout as string, stderr as string, stdout+stderr).
    def execute(self, cmd, raise_on_non_zero=True, retry_interval = 30, retry_count = 0, raise_on_keywords=[], read_out_and_err = True):
        if self.__client:
            self.__logger.info("Executing command: %s" % cmd)
        
            channel = self.__client.get_transport().open_session()
            channel.exec_command(cmd)
            out = ''
            err = ''
            if read_out_and_err:
                (out, err) = read_stderrout(channel, self.__logger)
                #out = ''.join(file_out.readlines())
                #err = ''.join(file_err.readlines())
            r = channel.recv_exit_status()
            channel.close()
            
            # Check return value
            nice_err = "  %s" % (err.replace("\n", "\n  "))
            if r != 0:
                if retry_count > 0:
                    self.__out.warn(nice_err)
                    self.__out.warn("Executing command failed... retrying (%s) in %d secs" % (retry_count, retry_interval))
                    time.sleep(retry_interval)
                    return self.execute(cmd, retry_interval=retry_interval, retry_count=(retry_count - 1), raise_on_keywords=raise_on_keywords, read_out_and_err=read_out_and_err)
                elif raise_on_non_zero:
                    raise MgmtException("The following command failed in host %s: %s\n%s" % (self.__hostname, cmd, nice_err))
            if err != "":
                self.__out.warn(nice_err)
                
            # Check keywords
            for keyword in raise_on_keywords:
                if err.find(keyword) != -1:
                    raise MgmtException("The following command failed in host %s: %s\n%s" % (self.__hostname, cmd, nice_err))
                
            # Return on success
            return (r, out, err, all)
        else:
            raise MgmtException("SSH not connected")
            

    # Transfers the given file to the remote server. Parameter remote_filename
    # must be an absolute path
    def copy_file_to(self, local_filename, remote_filename, mode=None):
        if self.__client:
            self.__logger.info("Copying file from %s to %s" % (local_filename, remote_filename))
            sftp = self.__client.open_sftp()
            
            # Ensure that the directory exists
            d = ""
            for item in dirname(remote_filename).split('/'):
                if item != "":
                    d += "/%s" % item
                    try:
                        sftp.mkdir(d)
                    except IOError:
                        pass
            
            # Copy and close
            sftp.put(local_filename, remote_filename)
            if mode == None:
                mode = os.stat(local_filename).st_mode
            sftp.chmod(remote_filename, mode)
            sftp.close()
        else:
            raise MgmtException("SSH not connected")

    # A convinience method for sending file to the remote server
    def send_file_to(self, content, remote_filename, mode=0o600, retries=2):
        if self.__client:
            (dummy, tmpfilename) = tempfile.mkstemp()
            f = open(tmpfilename, "w")
            f.write(str(content))
            f.close()
            self.copy_file_to(tmpfilename, remote_filename, mode)
            os.remove(tmpfilename)
        else:
            raise MgmtException("SSH not connected")

    # Transfers the given file from the remote server
    def copy_file_from(self, remote_filename, local_filename):
        if self.__client:
            self.__logger.info("Copying remote file from %s to %s" % (remote_filename, local_filename))
            sftp = self.__client.open_sftp()
            sftp.get(remote_filename, local_filename)
            sftp.close()
        else:
            raise MgmtException("SSH not connected")

    # Return contents of the remote file as a string or default_content if the file doesn't exist
    def receive_file_from(self, remote_filename, default_content = None):
        if self.__client:
            self.__logger.info("Copying remote file from %s" % (remote_filename))
            (f, tmpfilename) = tempfile.mkstemp()
            try:
                sftp = self.__client.open_sftp()
                sftp.get(remote_filename, tmpfilename)
                sftp.close()
                f = open(tmpfilename, "r")
                r = f.read()
                f.close()
            except IOError:
                self.__out.debug("File %s not found in %s. Using default content." % (remote_filename, self.__hostname))
                return default_content
            finally:
                os.remove(tmpfilename)
            return r
        else:
            raise MgmtException("SSH not connected")

    # Remove the given file in the remote server
    def remove_file(self, remote_filename, raise_on_not_found=False):
        if self.__client:
            self.__logger.info("Removing remote file %s" % (remote_filename))
            sftp = self.__client.open_sftp()
            try:
                sftp.remove(remote_filename)
            except IOError as e:
                self.__logger.warn("Error while removing file: " + str(e))
            sftp.close()
            return True
        else:
            if raise_on_not_found:
                raise MgmtException("SSH not connected")
            else:
                return False

    # Copy the given RPM file to the server and install it
    def install_rpms(self, rpm_dir, rpm_filenames, extra_options = "", find_local_file=True):
        if self.__client:
            self.__logger.info("Installing RPMs")
            sftp = None
            try:
                sftp = self.__client.open_sftp()
                remote_rpm_filenames = []
                for rpm_filename in rpm_filenames:
                    # Find the local file
                    local_filename = join(rpm_dir, rpm_filename)
                    if not exists(local_filename):
                        if find_local_file:
                            local_filename = find_file(rpm_dir, rpm_filename)
                        if local_filename == None or not exists(local_filename):
                            raise MgmtException("File %s was not found" % rpm_filename)
                
                    remote_rpm_filename = join('/tmp/', rpm_filename)
                    sftp.put(local_filename, remote_rpm_filename)
                    remote_rpm_filenames.append(remote_rpm_filename)
                
                # Execute RPM install
                cmd = "cd /tmp && rpm --allfiles --quiet --force -i %s \"%s\"" % (extra_options, '" "'.join(rpm_filenames))
                (retcode, out, err, all) = self.execute(cmd);
                
                # Remove the remote files
                for remote_rpm_filename in remote_rpm_filenames:
                    sftp.remove(remote_rpm_filename)

                # Raise exception
                if retcode != 0:
                    raise MgmtException("RPM install in %s failed with a return code %s: %s" % (self.__hostname, retcode, err))
            finally:
                if sftp: sftp.close()
            return True
        else:
            raise MgmtException("SSH not connected")

    # Uninstall the given RPM packages from the server
    def uninstall_rpms(self, rpm_filenames):
        if len(rpm_filenames) > 0:
            self.__logger.info("Uninstalling RPMs")
            # Execute RPM install
            package_names = []
            for rpm_filename in rpm_filenames:
                package_names.append(rpm_filename[0:-4]) # drop .rpm extension
            pkgs = ' '.join(package_names)
            if len(pkgs.strip()) > 0:
                cmd = "rpm  -e %s" % (pkgs)
                (retcode, out, err, all) = self.execute(cmd);
                if retcode != 0:
                    return False
                    #raise MgmtException("RPM uninstall in %s failed with a return code %s: %s" % (self.__hostname, retcode, err))
        return True

    # Return user's home directory, like /home/billgates
    def get_homedir_of(self, username):
        if username:
            (r, out, err, all) = self.execute("egrep \"^%s:\" /etc/passwd | cut -d':' -f6" % username)
            if out:
                homedir = out.strip()
                if len(homedir) > 0:
                    self.__logger.debug("Returning homedir '%s' for user %s" % (homedir, username))
                    return homedir
                else:
                    self.__logger.debug("No homedir found for user %s" % username)
            else:
                self.__logger.warn("Can't get homedir, because ssh output is missing")
        else:
            self.__logger.warn("Can't get homedir, because username is not valid: %s" % username)
        return None

    def repr(self):
        return "SSHConnection:%s" % (self.__connection)
        
# This is for testing only
class DryConnection(object):
    def __init__(self, hostname):
        self.__hostname = hostname

    # Connect to the host. It's expected that key-authentication works 
    # transparently
    def connect(self, username = "root", password=None):
        print("Connect to host %s as %s" % (self.__hostname, username))
        return True

    def disconnect(self):
        print("Disconnect from host %s" % (self.__hostname))

    # Execute a command in the remote server. Returns contents of stdout and
    # stderr as a tuple.
    def execute(self, cmd, as_user = "root"):
        print("Execute: " + cmd)
        return (0, "", "", "")

    # Transfers the given file to the remote server
    def copy_file_to(self, local_filename, remote_filename):
        print("Copy file %s to %s:%s" % (local_filename, self.__hostname, remote_filename))

    # A convinience method for sending file to the remote server
    def send_file_to(self, content, remote_filename):
        print("Copy %d bytes to %s:%s" % (len(content), self.__hostname, remote_filename))

    # Transfers the given file from the remote server
    def copy_file_from(self, remote_filename, local_filename):
        print("Copy file %s:%s to %s" % (self.__hostname, remote_filename, local_filename))

    def receive_file_from(self, remote_filename):
        return ""

    def remove_file(self, remote_filename):
        print("Remove file %s:%s to %s" % (self.__hostname, remote_filename))

    def install_rpms(self, rpm_dir, rpm_filenames):
        for fn in rpm_filenames:
            print("Install RPM %s" % (fn))

    def repr(self):
        return "SSHConnection:%s" % (self.__connection)


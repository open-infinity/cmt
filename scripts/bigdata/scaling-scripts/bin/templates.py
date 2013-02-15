
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
# templates.py
# 
# @author Timo Saarinen
# @author Tommi Siitonen
# @version 1.0.0
# @since 1.0.0
#

import re
from os.path import join
from os import listdir, stat
from config import *

re_param = re.compile(r"^@role\:(\S+)\s+(\S+)\s*")

# Read tempalte file and substitute parameters. Returns a tuple:
# (content, remote filename, mode). If no matching roles are found, (None, None, None)
# tuple will be returned. Currently cluster_type should be "hbase".
def populate_template(cluster_type, role, filename, params):
    # Read the template file and parse file params
    content = ""
    remote_filename = None
    template_filename = join(template_dir, cluster_type, filename)
    f = open(template_filename, "r")
    while True:
        line = f.readline()
        if line != "":
            mo = re_param.match(line)
            if mo != None:
                if mo.group(1) == role:
                    remote_filename = mo.group(2)
            else:
                content += line
        else:
            break
    f.close()

    # Without a remote filename we don't need to continue further
    if remote_filename == None:
        return (None, None, None)

    # Substitute params
    for key in params:
        value = str(params[key])
        content = content.replace("[[%s]]" % (key), value)

    return (content, remote_filename, stat(template_filename).st_mode)

# Read all templates matching with the given role. Returns list of tuples
# with the same parameters as in read_template function. Naturally 
# (None,None,None) tuples won't be included in the result.
def populate_templates(cluster_type, role, params):
    r = []
    for item in listdir(join(template_dir, cluster_type)):
        if item[0] != '.':
            (c, rfn, mode) = populate_template(cluster_type, role, item, params)
            if rfn != None:
                r.append( (c, rfn, mode) )
    return r  


@role:hmaster /tmp/status-check.sh
#!/bin/sh

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
# hmaster_status-check.sh
# 
# @author Timo Saarinen
# @author Tommi Siitonen
# @version 1.0.0
# @since 1.0.0
#

grep /var/log/hadoop/hadoop* /var/log/hbase/hbase* | egrep " ERROR | WARN | Exception:" | tail -10 >/dev/stderr
exit 0


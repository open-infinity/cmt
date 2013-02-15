@role:shard /tmp/mongodb/mongod-replicaset-initiate.js

/*
* Copyright (c) 2011 the original author or authors.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

/**
* mongos-shard-remove.js
* 
* @author Timo Saarinen
* @author Tommi Siitonen
* @version 1.0.0
* @since 1.0.0
*/

config = {
    _id: '[[REPLICASET_NAME]]', 
    members: [
        [[REPLICASET_MEMBERS_AS_JSON]]
    ]
};

rs.initiate(config);

// Wait until startupStatus is gone
while (true) {
    r = rs.status();
    printjson(r);
    if (r['startupStatus'] == null) {
        break;
    }
    
    // Poll interval of two seconds
    sleep(2000);
}

// Wait until state string of each member is not unknown
all_known = false;
while (!all_known) {
    r = rs.status();
    printjson(r);

    all_known = true;    
    for (i in r['members']) {
        m = r['members'][i]
        if (m['stateStr'] == "ERROR") {
            quit(1);
        }
        if (m['stateStr'] != "PRIMARY" && m['stateStr'] != "SECONDARY") {
            all_known = false;
        }
    }
    
    // Poll interval of two seconds
    sleep(5000);
}


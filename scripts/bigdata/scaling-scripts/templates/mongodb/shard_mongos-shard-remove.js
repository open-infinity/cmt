@role:shard /tmp/mongodb/mongos-shard-remove.js

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

admin = db.getSisterDB("admin")
config = db.getSisterDB("config")

// Start draining
print("Starting draining [[SHARD_NAME]].");
admin.runCommand( { removeshard : "[[SHARD_NAME]]"} );

// Wait until the shard has been drained
print("Waiting until all chunks get drained.");
while (true) {
    r = admin.runCommand( { removeshard : "[[SHARD_NAME]]"} );
    if (r['remaining'] == null || r['remaining']['chunks'] == 0) {
        break;
    } else {
        if (r['remaining'] != null) {
            print("  [[SHARD_NAME]] chunks remaining " + r['remaining']['chunks']);
        }
    }
    
    // Poll interval of two seconds
    sleep(5000);
}

// Create a list of shards to be moved
print("Checking if [[SHARD_NAME]] is a primary for any databases. ");
var dbnames_to_be_moved = [];
config_databases = config.databases.find().toArray();
for (i in config_databases) {
    dbname = config_databases[i]['_id'];
    pname = config_databases[i]['primary'];
    if (pname == '[[SHARD_NAME]]') {
        dbnames_to_be_moved.push(dbname);
    }
}

if (dbnames_to_be_moved.length > 0) {
    // Find another shard
    print("Finding another shard. ");
    var another_shard = null;
    config_shards = config.shards.find().toArray();
    for (i in config_shards) {
        sname = config_shards[i]['_id'];
        if (sname != '[[SHARD_NAME]]') {
            another_shard = sname; 
            print("  " + sname + " (chosen)");
        } else {
            print("  " + sname);
        }
    }
    if (another_shard != null) {
        // Move primary needed in the latest versions of MongoDB
        // Move the primary to another shard
        for (i in dbnames_to_be_moved) {
            dbname = dbnames_to_be_moved[i];
            print("Moving database " + dbname + " from [[SHARD_NAME]] to " + another_shard + ". ");
            admin.runCommand( { moveprimary : dbname, to : another_shard } );
        }
    } else {
        print("Couldn't find a new shard for the database. Expecting this to be the last one. ");
    }

    // Wait until the shard has been drained
    print("Waiting until shard removal becomes completed");
    while (true) {
        r = admin.runCommand( { removeshard : "[[SHARD_NAME]]"} );
        print("  shard removal state: " + r['state']);
        if (r['state'] == 'completed') {
            break;
        } else if (r['state'] == null) {
            quit(1);
        }
        
        // Poll interval of two seconds
        sleep(5000);
    }
} else {
    print("Shard [[SHARD_NAME]] isn't primary for any databases.");
}
print("Shard [[SHARD_NAME]] removed successfully.");


#!/usr/bin/env python2

import pymongo
import time
import random
import optparse
from optparse import OptionParser

# Parse options
parser = OptionParser()
parser.add_option("-n", "--count", dest="writes", default='10000',
                  help="Number of writes", metavar="WRITES")
parser.add_option("-w", "--write-concern-size", dest="write_concerns", default='3',
                  help="Write concerns replication size", metavar="WC")
parser.add_option("-c", "--host", dest="host", default='localhost',
                  help="Hostname for mongos process", metavar="HOST")
parser.add_option("-p", "--port", dest="port", default='27017',
                  help="Port", metavar="PORT")
(options, args) = parser.parse_args()

# Connection
# w=0 disabled write concern replication
# w=3 enables write concern replication for replica sets of size 3
client = pymongo.MongoClient(options.host, int(options.port), w=options.write_concerns)

# Database and collection
db = client['stress-test']
collection = db['test']

# Aggressive writes
time0 = time.time()
n = int(options.writes)
for i in range(0, n):
    item = { 
        'A': random.uniform(0, 10), 
        'B': random.uniform(0, 10), 
        'C': random.uniform(0, 10), 
        'D': random.uniform(0, 10), 
        'E': random.uniform(0, 10), 
        'F': random.uniform(0, 10), 
        'G': random.uniform(0, 10), 
        'H': random.uniform(0, 10), 
        'I': random.uniform(0, 10), 
        'J': random.uniform(0, 10),
        'K': random.uniform(0, 10), 
        'L': random.uniform(0, 10), 
        'M': random.uniform(0, 10), 
        'N': random.uniform(0, 10), 
        'O': random.uniform(0, 10), 
        'P': random.uniform(0, 10), 
        'Q': random.uniform(0, 10), 
        'R': random.uniform(0, 10), 
        'S': random.uniform(0, 10), 
        'T': random.uniform(0, 10)
    }
    collection.insert(item)
time1 = time.time()

rate = n / (time1 - time0)
print("%.1f inserts per second" % (rate))
print("%.1f inserts per minute" % (rate*60))
print("%.1f million inserts per day" % (rate*24*60*60 / 1000000))


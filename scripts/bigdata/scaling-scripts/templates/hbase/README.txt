
REPLACABLE CONFIGURATION VARIABLES IN TEMPLATES
===============================================


hmaster_hbase-site.xml
-----------------------

    <name>hbase.rootdir</name>
    <value>hdfs://[[HMASTER_HOST]]:8020/hbase</value>
    
    <name>hbase.zookeeper.quorum</name>
    <value>[[ZOOKEEPER_HOSTNAMES_COMMA_SEPERATED]]</value>
    
zookeeper_zoo.cfg
-------------------

[[ZOOKEEPER_CFG]]
(example: server.3=ZK-3:2888:3888)

hmaster_slaves
--------------

[[SLAVE_HOSTNAMES_BY_LINE]]


hmaster_regionservers
----------------------
[[REGIONSERVERS_HOSTNAMES_BY_LINE]]

hmaster_core-site.xml
---------------------

    <name>fs.default.name</name>
    <value>hdfs://[[HMASTER_HOST]]:8020</value>
    
    
hmaster_hdfs-site.xml
--------------

-none

slave_hdfs-site.xml
--------------

-none

hmaster_mapred-site.xml
---------------

    <name>mapred.job.tracker</name>
    <value>[[HMASTER_HOST]]:8021</value>


hmaster_hadoop-env.sh
--------------

-none
-JAVA_HOME set to openjdk default

hmaster_hbase-env.sh
------------

-none
-JAVA_HOME set to openjdk default

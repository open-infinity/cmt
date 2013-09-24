@role:hive /etc/profile.d/pig-env.sh

# Sets environment variables needed for 'pig' command
export PIG_CONF_DIR=/usr/lib/pig/conf
export PIG_CLASSPATH=/usr/lib/hbase/hbase-0.94.2-cdh4.2.0-security.jar:/usr/lib/zookeeper/zookeeper-3.4.5-cdh4.2.0.jar


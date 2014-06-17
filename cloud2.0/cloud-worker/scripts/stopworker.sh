#!/bin/bash

export JAVA_HOME=/etc/alternatives/java_sdk_openjdk/

CLASS_PATH=/opt/toas/cloudmanagementtools/worker/cloud-worker-1.2.2.jar
PID_FILE=/opt/toas/cloudmanagementtools/worker/worker.pid
LOG_DIR=/opt/toas/cloudmanagementtools/worker/logs
MAIN_CLASS=org.openinfinity.cloud.application.worker.CloudWorker

/usr/bin/jsvc -user toas -home $JAVA_HOME -pidfile $PID_FILE -outfile $LOG_DIR/stdout.log -errfile $LOG_DIR/stderr.log -stop -cp  $CLASS_PATH $MAIN_CLASS



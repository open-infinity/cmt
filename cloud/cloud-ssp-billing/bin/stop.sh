#!/bin/bash

JAVA_HOME=/etc/alternatives/java_sdk_openjdk/ 
CLASS_PATH=/opt/toas/cloudmanagementtools/autoscaler/lib/java/cloud-autoscaler-1.2.0.RELEASE.jar
CLASS=org.openinfinity.cloud.ssp. 
PID_FILE=/opt/toas/cloudmanagementtools/autoscaler/var/run/autoscaler.pid

/usr/bin/jsvc -home $JAVA_HOME -pidfile $PID_FILE  -cp $CLASS_PATH $CLASS

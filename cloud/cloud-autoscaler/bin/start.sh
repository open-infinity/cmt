#!/bin/bash

USER=toas
CP=/opt/toas/cloudmanagementtools/autoscaler/lib/java/cloud-autoscaler-1.2.2.jar
PID_FILE=/opt/toas/cloudmanagementtools/autoscaler/var/run/autoscaler.pid
JAVA_HOME=/etc/alternatives/java_sdk_openjdk/

/usr/bin/jsvc -user $USER -home $JAVA_HOME -pidfile $PID_FILE  -cp $CP org.openinfinity.cloud.autoscaler.application.Autoscaler

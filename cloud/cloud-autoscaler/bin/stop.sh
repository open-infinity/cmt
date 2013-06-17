#!/bin/bash

CP=/opt/toas/cloudmanagementtools/autoscaler/lib/java/jar/cloud-autoscaler-1.0.0.jar
PID_FILE=/opt/toas/cloudmanagementtools/autoscaler/var/run/autoscaler.pid

/usr/bin/jsvc -stop -pidfile $PID_FILE  -cp $CP org.openinfinity.cloud.autoscaler.application.Autoscaler



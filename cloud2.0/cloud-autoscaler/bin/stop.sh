#!/bin/bash

CP=/opt/toas/cloudmanagementtools/autoscaler/lib/java/cloud-autoscaler.jar
PID_FILE=/opt/toas/cloudmanagementtools/autoscaler/var/run/autoscaler.pid

/usr/bin/jsvc -stop -pidfile $PID_FILE  -cp $CP org.openinfinity.cloud.autoscaler.application.Autoscaler



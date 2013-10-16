#!/bin/bash

#USER=vedran
#JAVA_HOME=/etc/alternatives/java_sdk_openjdk/ 
JAVA_HOME=/usr/lib/jvm/java-6-openjdk-amd64
#CLASS_PATH=/opt/toas/cloudmanagementtools/autoscaler/lib/java/cloud-autoscaler-1.2.0.RELEASE.jar
CLASS_PATH=/home/vedran/workspace/toas/github/cmt/cloud/cloud-ssp-billing/target/cloud-ssp-billing-1.2.2.jar
CLASS=org.openinfinity.cloud.ssp.billing.authorization.AuthorizationApplication 
PID_FILE=/home/vedran/workspace/toas/github/cmt/cloud/cloud-ssp-billing/ssp-billing.pid

/usr/bin/jsvc -debug -home $JAVA_HOME -pidfile $PID_FILE  -cp $CLASS_PATH $CLASS      
#/usr/bin/jsvc -debug -user $USER -home $JAVA_HOME -pidfile $PID_FILE  -cp $CLASS_PATH org.openinfinity.ssp.billing.account.Application

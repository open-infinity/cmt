#!/bin/bash

JAVA_HOME=/usr/lib/jvm/java-6-openjdk-amd64
CLASS_PATH=/home/vedran/workspace/toas/github/cmt/cloud/cloud-ssp-billing/target/cloud-ssp-billing-1.2.2.jar
CLASS=org.openinfinity.cloud.ssp.billing.authorization.AuthorizationApplication 
PID_FILE=/home/vedran/workspace/toas/github/cmt/cloud/cloud-ssp-billing/ssp-billing.pid

/usr/bin/jsvc -stop -pidfile $PID_FILE  -cp $CLASS_PATH $CLASS

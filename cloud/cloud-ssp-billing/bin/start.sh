#!/bin/bash

#USER=vedran
#JAVA_HOME=/etc/alternatives/java_sdk_openjdk/ 
JAVA_HOME=/usr/lib/jvm/java-6-openjdk-amd64
CLASS_PATH=/home/vedran/workspace/toas/github/cmt/cloud/cloud-ssp-billing/target/cloud-ssp-billing-1.2.2.jar
CLASS=org.openinfinity.cloud.ssp.billing.invoice.InvoiceApplication
PID_FILE=/home/vedran/workspace/toas/github/cmt/cloud/cloud-ssp-billing/ssp-billing.pid

/usr/bin/jsvc -debug -home $JAVA_HOME -pidfile $PID_FILE  -cp $CLASS_PATH $CLASS      

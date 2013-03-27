#!/bin/bash
export JAVA_HOME=/etc/alternatives/java_sdk_openjdk/
/usr/bin/jsvc -user toas -home /etc/alternatives/java_sdk_openjdk/ -pidfile /opt/toas/cloudmanagementtools/worker/worker.pid -cp /opt/toas/cloudmanagementtools/worker/cloud-worker-2.0.0.jar org.openinfinity.cloud.application.worker.CloudWorker

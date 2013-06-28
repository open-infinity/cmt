#!/bin/bash -x
export JAVA_HOME=/etc/alternatives/java_sdk_openjdk/
/usr/bin/jsvc -user toas -home /etc/alternatives/java_sdk_openjdk/ -pidfile /opt/toas/cloudmanagementtools/properties/batch.pid -cp /opt/toas/cloudmanagementtools/properties/bin/cloud-properties-batch-1.2.0.RELEASE.jar org.openinfinity.cloud.application.batch.properties.PeriodicCloudPropertiesExecutor

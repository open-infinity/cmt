#!/bin/bash -x
export JAVA_HOME=/etc/alternatives/java_sdk_openjdk/
/usr/bin/jsvc -user toas -home /etc/alternatives/java_sdk_openjdk/ -pidfile /opt/toas/cloudmanagementtools/deployer/deployer.pid -stop -cp /opt/toas/cloudmanagementtools/deployer/cloud-deployer-batch-1.2.0.RELEASE.jar org.openinfinity.cloud.application.deployer.batch.DeployerJsvcLauncher

#!/bin/sh

export JAVA_HOME=/opt/java6
export PATH=$JAVA_HOME/bin:$PATH

CWD=`pwd`
cd ..
mvn -pl cloud-backup $@ install || exit 1
cd $CWD

CLASSPATH=$(find "target" -name '*.jar' | xargs echo | tr ' ' ':')
java -cp target/cloud-backup-0.1-SNAPSHOT.jar:$CLASSPATH org.openinfinity.cloud.application.backup.App


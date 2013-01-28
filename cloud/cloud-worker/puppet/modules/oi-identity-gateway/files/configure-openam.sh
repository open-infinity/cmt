#!/bin/bash

echo "Sleeping a minute..."
sleep 1m
echo "Doing the configurator"
/usr/bin/java -jar /opt/openinfinity/2.0.0/sso-tools/configuratortools/configurator.jar -f /opt/openinfinity/2.0.0/sso-tools/configuratortools/sampleconfiguration

export JAVA_HOME=/etc/alternatives/jre_openjdk/
export PATH=$PATH:/etc/alternatives/jre_openjdk/bin

echo "Doing the setup"
cd /opt/openinfinity/2.0.0/sso-tools/admintools
./setup -p /opt/openinfinity/2.0.0/openam -d /opt/openinfinity/2.0.0/openam-debug -l /opt/openinfinity/2.0.0/openam-log

echo "Sleeping 30 seconds"
sleep 30s
echo "Doing create-realm"
/opt/openinfinity/2.0.0/sso-tools/admintools/openam/bin/ssoadm create-realm -u amAdmin -f /opt/openinfinity/2.0.0/sso-tools/amadmin.pwd -e openinfinity
echo "Doing create-cot"
/opt/openinfinity/2.0.0/sso-tools/admintools/openam/bin/ssoadm create-cot -t TOAS -u amadmin -f /opt/openinfinity/2.0.0/sso-tools/amadmin.pwd -e openinfinity

echo "Doing import-entity"
/opt/openinfinity/2.0.0/sso-tools/admintools/openam/bin/ssoadm import-entity -t TOAS -m /opt/openinfinity/2.0.0/sso-tools/sp1.xml -x /opt/openinfinity/2.0.0/sso-tools/sp1-x.xml -u amAdmin -f /opt/openinfinity/2.0.0/sso-tools/amadmin.pwd -e openinfinity
echo "Doint import entity part 2"
/opt/openinfinity/2.0.0/sso-tools/admintools/openam/bin/ssoadm import-entity -t TOAS -m /opt/openinfinity/2.0.0/sso-tools/idp1.xml -x /opt/openinfinity/2.0.0/sso-tools/idp1-x.xml -u amAdmin -f /opt/openinfinity/2.0.0/sso-tools/amadmin.pwd -e openinfinity
echo "Doing add-sub-schema"
/opt/openinfinity/2.0.0/sso-tools/admintools/openam/bin/ssoadm add-sub-schema -s sunIdentityRepositoryService -t Organization -F /opt/openinfinity/2.0.0/sso-tools/liferayDBrepo.xml -u amAdmin -f /opt/openinfinity/2.0.0/sso-tools/amadmin.pwd
echo "Doing create-datastore"
/opt/openinfinity/2.0.0/sso-tools/admintools/openam/bin/ssoadm create-datastore -u amAdmin -f /opt/openinfinity/2.0.0/sso-tools/amadmin.pwd -m Liferay -e openinfinity -t CustomRepo -a sunIdRepoClass=org.openinfinity.sso.openam.userdata.LiferayDatabaseRepo -D /opt/openinfinity/2.0.0/sso-tools/datasoreDatafile
echo "Doing delete default datastore"
/opt/openinfinity/2.0.0/sso-tools/admintools/openam/bin/ssoadm delete-datastores -u amadmin -f /opt/openinfinity/2.0.0/sso-tools/amadmin.pwd -m embedded -e openinfinity



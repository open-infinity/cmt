#!/bin/bash

echo "Sleeping 3 minutes.."
sleep 3m
echo "Doing the configurator"
/usr/bin/logger "Doing the configurator"
output=`/etc/alternatives/jre_1.6.0/bin/java -jar /opt/openinfinity/2.0.0/sso-tools/configuratortools/configurator.jar -f /opt/openinfinity/2.0.0/sso-tools/configuratortools/sampleconfiguration`
/usr/bin/logger $output
sleep 30s
export JAVA_HOME=/etc/alternatives/jre_1.6.0/
export PATH=$PATH:/etc/alternatives/jre_1.6.0/bin

echo "Doing the setup"
/usr/bin/logger "Doing the setup"
cd /opt/openinfinity/2.0.0/sso-tools/admintools
output=`/opt/openinfinity/2.0.0/sso-tools/admintools/setup -p /opt/openinfinity/2.0.0/openam -d /opt/openinfinity/2.0.0/openam-debug -l /opt/openinfinity/2.0.0/openam-log`
/usr/bin/logger $output

echo "Sleeping 30 seconds"
sleep 30s
echo "Doing create-realm"
/usr/bin/logger "Doing create-realm"
output=`/opt/openinfinity/2.0.0/sso-tools/admintools/openam/bin/ssoadm create-realm -u amAdmin -f /opt/openinfinity/2.0.0/sso-tools/amadmin.pwd -e openinfinity`
/usr/bin/logger $output
echo "Doing create-cot"
/usr/bin/logger "Doing create-cot"
output=`/opt/openinfinity/2.0.0/sso-tools/admintools/openam/bin/ssoadm create-cot -t TOAS -u amadmin -f /opt/openinfinity/2.0.0/sso-tools/amadmin.pwd -e openinfinity`
/usr/bin/logger $output
/usr/bin/logger "Doing import-entity"
echo "Doing import-entity"
output=`/opt/openinfinity/2.0.0/sso-tools/admintools/openam/bin/ssoadm import-entity -t TOAS -m /opt/openinfinity/2.0.0/sso-tools/sp1.xml -x /opt/openinfinity/2.0.0/sso-tools/sp1-x.xml -u amAdmin -f /opt/openinfinity/2.0.0/sso-tools/amadmin.pwd -e openinfinity`
/usr/bin/logger $output
echo "Doing import entity part 2"
/usr/bin/logger "Doing import entity part 2"
output=`/opt/openinfinity/2.0.0/sso-tools/admintools/openam/bin/ssoadm import-entity -t TOAS -m /opt/openinfinity/2.0.0/sso-tools/idp1.xml -x /opt/openinfinity/2.0.0/sso-tools/idp1-x.xml -u amAdmin -f /opt/openinfinity/2.0.0/sso-tools/amadmin.pwd -e openinfinity`
/usr/bin/logger $output
echo "Doing add-sub-schema"
/usr/bin/logger "Adding sub-schema"
output=`/opt/openinfinity/2.0.0/sso-tools/admintools/openam/bin/ssoadm add-sub-schema -s sunIdentityRepositoryService -t Organization -F /opt/openinfinity/2.0.0/sso-tools/liferayDBrepo.xml -u amAdmin -f /opt/openinfinity/2.0.0/sso-tools/amadmin.pwd`
/usr/bin/logger $output
echo "Doing create-datastore"
/usr/bin/logger "Creating datastore"
output=`/opt/openinfinity/2.0.0/sso-tools/admintools/openam/bin/ssoadm create-datastore -u amAdmin -f /opt/openinfinity/2.0.0/sso-tools/amadmin.pwd -m Liferay -e openinfinity -t CustomRepo -a sunIdRepoClass=org.openinfinity.sso.openam.userdata.LiferayDatabaseRepo -D /opt/openinfinity/2.0.0/sso-tools/datasoreDatafile`
/usr/bin/logger $output
echo "Doing delete default datastore"
/usr/bin/logger "Deleting default datastore"
output=`/opt/openinfinity/2.0.0/sso-tools/admintools/openam/bin/ssoadm delete-datastores -u amadmin -f /opt/openinfinity/2.0.0/sso-tools/amadmin.pwd -m embedded -e openinfinity`
/usr/bin/logger $output

touch /opt/openinfinity/2.0.0/sso-tools/configure-run

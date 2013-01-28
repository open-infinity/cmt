#!/bin/bash

cat /opt/openinfinity/2.0.0/tomcat/webapps/openam/WEB-INF/classes/amIdRepoService.properties /opt/openinfinity/2.0.0/tomcat/webapps/openam/WEB-INF/classes/addToIDRepositoryService.properties > /opt/openinfinity/2.0.0/tomcat/webapps/openam/WEB-INF/classes/amIdRepoService.properties.temp

rm -f /opt/openinfinity/2.0.0/tomcat/webapps/openam/WEB-INF/classes/amIdRepoService.properties

mv /opt/openinfinity/2.0.0/tomcat/webapps/openam/WEB-INF/classes/amIdRepoService.properties.temp /opt/openinfinity/2.0.0/tomcat/webapps/openam/WEB-INF/classes/amIdRepoService.properties

cat /opt/openinfinity/2.0.0/sso-tools/admintools/resources/amIdRepoService.properties /opt/openinfinity/2.0.0/sso-tools/admintools/resources/addToIDRepositoryService.properties > /opt/openinfinity/2.0.0/sso-tools/admintools/resources/amIdRepoService.properties.temp

rm -f /opt/openinfinity/2.0.0/sso-tools/admintools/resources/amIdRepoService.properties 

mv /opt/openinfinity/2.0.0/sso-tools/admintools/resources/amIdRepoService.properties.temp /opt/openinfinity/2.0.0/sso-tools/admintools/resources/amIdRepoService.properties

touch /opt/openinfinity/2.0.0/sso-tools/configs-generated

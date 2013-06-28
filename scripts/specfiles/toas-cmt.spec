%define name	toas_cmt
%define version 1.2.0
%define release 20

Name:		%{name}
Version:	%{version}
Release:	%{release}
Summary:	TOAS Cloud Management Tools for TOAS PaaS
Group:		Applications/System
License:	Apache
URL:		http://www.tieto.com/toas
Source0:	open-infinity-master.zip
Source1:	toas_cmt-HEAD.tar.gz
BuildArch:	noarch
BuildRequires:	apache-maven
Requires:	puppet-server, mysql-server, jakarta-commons-daemon-jsvc, java-1.6.0-openjdk, perl-DBI, perl-YAML, perl-DBD-MySQL, python, python-boto, MySQL-python
Requires(pre):	shadow-utils

%description
This package contains Cloud Management Tools for TOAS PaaS

%prep
%setup -T -a 0 -c 
%setup -D -T -a 1 -c 

%build
cd open-infinity-master/open-infinity-core/releases/1.1.0/open-infinity-core/ && /usr/share/apache-maven/bin/mvn clean lombok:delombok install
cd ../../../../../cloud && /usr/share/apache-maven/bin/mvn -DskipTests clean lombok:delombok install

%install
mkdir -p %{buildroot}/opt/toas/cloudmanagementtools/worker/
mkdir -p %{buildroot}/opt/toas/cloudmanagementtools/worker/logs

#Cloud Deployer
mkdir -p %{buildroot}/opt/toas/cloudmanagementtools/deployer/
mkdir -p %{buildroot}/opt/toas/cloudmanagementtools/deployer/logs
mkdir -p %{buildroot}/opt/toas/cloudmanagementtools/deployer/staging

mkdir -p %{buildroot}/opt/toas/cloudmanagementtools/portlet/
mkdir -p %{buildroot}/opt/toas/cloudmanagementtools/scripts/sql/
mkdir -p %{buildroot}/opt/toas/cloudmanagementtools/puppet/modules/oihealthmonitoring/files/empty
mkdir -p %{buildroot}/opt/toas/cloudmanagementtools/tools/bin/
mkdir -p %{buildroot}/opt/toas/cloudmanagementtools/tools/lib/
cp cloud/cloud-worker/target/cloud-worker-2.0.0.jar %{buildroot}/opt/toas/cloudmanagementtools/worker/
cp cloud/cloud-administrator/target/cloud-administrator-1.0.0.war %{buildroot}/opt/toas/cloudmanagementtools/portlet/
cp -r cloud/cloud-worker/puppet %{buildroot}/opt/toas/cloudmanagementtools/
cp cloud/cloud-worker/scripts/startworker.sh %{buildroot}/opt/toas/cloudmanagementtools/worker/
cp cloud/cloud-worker/scripts/stopworker.sh %{buildroot}/opt/toas/cloudmanagementtools/worker/

#Cloud Deployer
cp cloud/cloud-deployer-batch/target/cloud-deployer-batch-1.0.0.jar %{buildroot}/opt/toas/cloudmanagementtools/deployer/
cp cloud/cloud-deployer/target/cloud-deployer-1.0.0.war %{buildroot}/opt/toas/cloudmanagementtools/portlet/
cp cloud/cloud-deployer-batch/scripts/startdeployer.sh %{buildroot}/opt/toas/cloudmanagementtools/deployer/
cp cloud/cloud-deployer-batch/scripts/stopdeployer.sh %{buildroot}/opt/toas/cloudmanagementtools/deployer/

cp scripts/sql/openinfinity-tables.sql %{buildroot}/opt/toas/cloudmanagementtools/scripts/sql/
cp scripts/sql/initial_content.sql %{buildroot}/opt/toas/cloudmanagementtools/scripts/sql/
cp scripts/command_line_tools/toascommon.py %{buildroot}/opt/toas/cloudmanagementtools/tools/lib/
cp scripts/command_line_tools/toasconfig.py %{buildroot}/opt/toas/cloudmanagementtools/tools/lib/
cp scripts/command_line_tools/toasdomain.py %{buildroot}/opt/toas/cloudmanagementtools/tools/lib/
cp scripts/command_line_tools/toas-check-instances %{buildroot}/opt/toas/cloudmanagementtools/tools/bin/
cp scripts/command_line_tools/toas-fix-loadbalancer %{buildroot}/opt/toas/cloudmanagementtools/tools/bin/
cp scripts/command_line_tools/toas-get-instance-info %{buildroot}/opt/toas/cloudmanagementtools/tools/bin/
cp scripts/command_line_tools/toas-instace-authorize %{buildroot}/opt/toas/cloudmanagementtools/tools/bin/

#Autoscaler
mkdir -p %{buildroot}/opt/toas/cloudmanagementtools/autoscaler/var/log
mkdir -p %{buildroot}/opt/toas/cloudmanagementtools/autoscaler/var/run
mkdir -p %{buildroot}/opt/toas/cloudmanagementtools/autoscaler/var/backup
mkdir -p %{buildroot}/opt/toas/cloudmanagementtools/autosscaler/lib/java
cp -f cloud/cloud-autoscaler/target/cloud-autoscaler-1.2.0.RELEASE.jar %{buildroot}/opt/toas/cloudmanagementtools/autoscaler/lib/java
cp -f cloud/cloud-autoscaler/bin/* %{buildroot}/opt/toas/cloudmanagementtools/autoscaler/bin

%clean
rm -rf %{buildroot}

%pre
getent group toas > /dev/null || groupadd -r toas
getent passwd toas > /dev/null || useradd -r -g toas -s /sbin/nologin toas
exit 0

%files
%defattr(-,toas,toas)
%attr(0755,toas,toas)/opt/toas/cloudmanagementtools/worker/stopworker.sh
%attr(0755,toas,toas)/opt/toas/cloudmanagementtools/worker/startworker.sh
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/worker/cloud-worker-2.0.0.jar
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/portlet/cloud-administrator-1.0.0.war

#Cloud Deployer
%attr(0755,toas,toas)/opt/toas/cloudmanagementtools/deployer/stopdeployer.sh
%attr(0755,toas,toas)/opt/toas/cloudmanagementtools/deployer/startdeployer.sh
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/deployer/cloud-deployer-batch-1.0.0.jar
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/portlet/cloud-deployer-1.0.0.war
%dir /opt/toas/cloudmanagementtools/deployer/logs
%dir /opt/toas/cloudmanagementtools/deployer/staging

#Autoscaler
/opt/toas/cloudmanagementtools/autoscaler/

%attr(0755,toas,toas)/opt/toas/cloudmanagementtools/puppet/puppet_nodes.pl
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/scripts/sql/openinfinity-tables.sql
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/scripts/sql/initial_content.sql
%dir /opt/toas/cloudmanagementtools/worker/logs
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/tools/lib/toascommon.py
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/tools/lib/toasconfig.py
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/tools/lib/toasdomain.py
%attr(0755,toas,toas)/opt/toas/cloudmanagementtools/tools/bin/toas-check-instances
%attr(0755,toas,toas)/opt/toas/cloudmanagementtools/tools/bin/toas-fix-loadbalancer
%attr(0755,toas,toas)/opt/toas/cloudmanagementtools/tools/bin/toas-get-instance-info
%attr(0755,toas,toas)/opt/toas/cloudmanagementtools/tools/bin/toas-instace-authorize

%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/oi-identity-gateway/templates/amadmin.pwd.template
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/oi-identity-gateway/templates/idp1-x.xml.ssoidentityprovider.template
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/oi-identity-gateway/templates/sso_sampleconfiguration
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/oi-identity-gateway/templates/liferayDBrepo.xml.template
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/oi-identity-gateway/templates/datastoreDatafile.template
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/oi-identity-gateway/templates/sp1-x.xml.ssoidentityprovider.template
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/oi-identity-gateway/templates/sp1.xml.ssoidentityprovider.template
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/oi-identity-gateway/templates/idp1.xml.ssoidentityprovider.template
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/oi-identity-gateway/manifests/install.pp
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/oi-identity-gateway/manifests/init.pp
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/oi-identity-gateway/manifests/config.pp
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/oi-identity-gateway/manifests/service.pp
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/oi-identity-gateway/manifests/setup.pp
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/oi-identity-gateway/files/addToIDRepositoryService.properties.tmp1
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/oi-identity-gateway/files/configure-openam.sh
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/oi-identity-gateway/files/oi-tomcat
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/oi-identity-gateway/files/haproxy.cfg
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/oi-identity-gateway/files/generate-configs.sh
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/oi-identity-gateway/files/addToIDRepositoryService.properties.tmp2
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/loadbalancer/templates/haproxy.cfg.erb
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/loadbalancer/manifests/init.pp
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/oitomcat/manifests/install.pp
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/oitomcat/manifests/init.pp
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/oitomcat/manifests/config.pp
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/oitomcat/manifests/service.pp
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/oitomcat/files/server.xml
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/oitomcat/files/server_7.0.27.xml
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/oitomcat/files/catalina.properties
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/oitomcat/files/oi-tomcat
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/oitomcat/files/context.xml
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/oibackup/templates/cron_d_oi-daily-backup.template
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/oibackup/templates/oi-daily-backup.sh.template
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/oibackup/templates/rsync-backup.sh.template
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/oibackup/manifests/install.pp
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/oibackup/manifests/init.pp
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/oibackup/manifests/config.pp
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/oibackup/manifests/service.pp
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/oibackup/files/toas-backup-public.key
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/oibackup/files/toas-backup-private.key
%dir /opt/toas/cloudmanagementtools/puppet/modules/oihealthmonitoring/files/empty
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/oihealthmonitoring/templates/collectd.conf.template
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/oihealthmonitoring/templates/toas.sh.template
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/oihealthmonitoring/templates/nodechecker.conf.template
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/oihealthmonitoring/templates/nodelist.conf.template
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/oihealthmonitoring/manifests/init.pp
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/oihealthmonitoring/manifests/service_collectd.pp
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/oihealthmonitoring/manifests/service_monitoring.pp
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/oihealthmonitoring/manifests/install.pp
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/oihealthmonitoring/manifests/config.pp
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/toasbigdatamgmt/templates/id_rsa.erb
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/toasbigdatamgmt/manifests/install.pp
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/toasbigdatamgmt/manifests/init.pp
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/toasbigdatamgmt/manifests/config.pp
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/oiportal/templates/portal-ext.properties.erb
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/oiportal/manifests/init.pp
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/oiportal/files/portal-setup-wizard.properties
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/oiportal/files/server_7.0.27.xml
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/oiportal/files/ROOT.xml
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/oiportal/files/catalina.properties
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/oiportal/files/oi-tomcat
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/oiportal/files/context.xml
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/oiportal/files/setenv.sh
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/toasbigdatahost/templates/network.erb
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/toasbigdatahost/manifests/init.pp
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/oibasic/manifests/init.pp
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/oiebs/manifests/init.pp
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/oiserviceplatform/templates/activemq.xml.erb
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/oiserviceplatform/manifests/install.pp
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/oiserviceplatform/manifests/init.pp
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/oiserviceplatform/manifests/config.pp
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/oiserviceplatform/files/catalina.properties
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/oiserviceplatform/files/oi-tomcat
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/oiserviceplatform/files/setenv.sh
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/oiportal-sso/templates/org.openinfinity.security.properties.template
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/oiportal-sso/templates/hazelcast.xml.erb
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/oiportal-sso/templates/securityContext.xml.template
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/oiportal-sso/templates/toasidp-samlr2-metadata.xml.ssoserviceprovider.template
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/oiportal-sso/templates/portal-ext.properties.erb
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/oiportal-sso/templates/sp1.xml.ssoserviceprovider.template
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/oiportal-sso/manifests/init.pp
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/oiportal-sso/files/portal-setup-wizard.properties
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/oiportal-sso/files/server_7.0.27.xml
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/oiportal-sso/files/ROOT.xml
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/oiportal-sso/files/catalina.properties
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/oiportal-sso/files/liferay.web.xml_6.1.ssoserviceprovider
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/oiportal-sso/files/oi-tomcat
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/oiportal-sso/files/samljaas.conf.ssoserviceprovider
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/oiportal-sso/files/context.xml
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/oiportal-sso/files/setenv.sh
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/oideploy/manifests/init.pp
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/baslb/templates/haproxy.cfg.erb
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/baslb/manifests/init.pp
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/oitomee/manifests/init.pp
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/mountdata/manifests/init.pp
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/oibas/templates/hazelcast.xml.erb
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/oibas/manifests/install.pp
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/oibas/manifests/init.pp
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/oibas/manifests/config.pp
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/oibas/manifests/service.pp
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/oibas/files/server.xml
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/oibas/files/server_7.0.27.xml
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/oibas/files/catalina.properties
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/oibas/files/oi-tomcat
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/oibas/files/context.xml
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/oibas/files/setenv.sh
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/oilttwatch/templates/ltt.xml.template
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/oilttwatch/manifests/install.pp
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/oilttwatch/manifests/init.pp
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/oilttwatch/manifests/preconfig.pp
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/oimariadb/templates/mariadb_backup.sh.template
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/oimariadb/manifests/install.pp
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/oimariadb/manifests/init.pp
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/oimariadb/manifests/config.pp
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/oimariadb/manifests/service.pp
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/oimariadb/files/my.cnf
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/oimariadb/files/toas-backup-public.key
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/oimariadb/files/oi-mariadb
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/puppet/modules/oimariadb/files/toas-backup-private.key


%changelog

%post
echo "*" > /etc/puppet/autosign.conf

echo "Add these lines to puppet.conf under [master]:"
echo "node_terminus=exec"
echo "external_nodes=/opt/toas/cloudmanagementtools/puppet/puppet_nodes.pl"
echo "modulepath = /opt/toas/cloudmanagementtools/puppet/modules:/etc/puppet/modules"
echo ""
echo "Will write these also to file /etc/puppet/puppet.conf.toas"
echo "[master]" > /etc/puppet/puppet.conf.toas
echo "node_terminus=exec" >> /etc/puppet/puppet.conf.toas
echo "external_nodes=/opt/toas/cloudmanagementtools/puppet/puppet_nodes.pl" >> /etc/puppet/puppet.conf.toas
echo "modulepath = /opt/toas/cloudmanagementtools/puppet/modules:/etc/puppet/modules" >> /etc/puppet/puppet.conf.toas


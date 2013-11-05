%define name	oi_cmt
%define version 1.3.0_RC1
%define release 1
#%define sourcetag CMT_%{version}
%define sourcetag master

Name:	%{name}
Version:	%{version}
Release:	%{release}
Summary:	OpenInfinity Cloud Management Tools for OpenInfinity PaaS
Group:	Applications/System
License:	Apache
URL:		http://www.tieto.com/openinfinity
#Source0:	open-infinity-master.zip
#Source1:	openinfinity_cmt-HEAD.tar.gz
#Source0:        https://github.com/open-infinity/cmt/archive/%{sourcetag}
Source0:         https://codeload.github.com/open-infinity/cmt/zip/%{sourcetag}
#Source1:        https://github.com/ileinone/open-infinity/archive/master.zip
Source1:         https://codeload.github.com/ileinone/open-infinity/zip/master


BuildArch:	noarch
BuildRequires:	apache-maven
Requires:	puppet-server, mysql-server, jakarta-commons-daemon-jsvc, java-1.7.0-openjdk, perl-DBI, perl-YAML, perl-DBD-MySQL, python, python-boto, MySQL-python,oi-puppetmodules
Requires(pre):	shadow-utils

%description
This package contains Cloud Management Tools for OpenInfinity PaaS

%prep
%setup -T -a 0 -c 
%setup -D -T -a 1 -c 

%build
cd open-infinity-master/open-infinity-core/releases/1.2.0/open-infinity-core/ && /usr/share/apache-maven/bin/mvn clean lombok:delombok install
cd ../../../../../cloud && /usr/share/apache-maven/bin/mvn -DskipTests clean lombok:delombok install

%install
mkdir -p %{buildroot}/opt/toas/cloudmanagementtools/worker/
mkdir -p %{buildroot}/opt/toas/cloudmanagementtools/worker/logs

#Cloud Deployer
mkdir -p %{buildroot}/opt/toas/cloudmanagementtools/deployer/
mkdir -p %{buildroot}/opt/toas/cloudmanagementtools/deployer/logs
mkdir -p %{buildroot}/opt/toas/cloudmanagementtools/deployer/staging

#Cloud Properties Batch
mkdir -p %{buildroot}/opt/toas/cloudmanagementtools/properties/
mkdir -p %{buildroot}/opt/toas/cloudmanagementtools/properties/logs
mkdir -p %{buildroot}/opt/toas/cloudmanagementtools/properties/bin


mkdir -p %{buildroot}/opt/toas/cloudmanagementtools/portlet/
mkdir -p %{buildroot}/opt/toas/cloudmanagementtools/scripts/sql/
mkdir -p %{buildroot}/opt/toas/cloudmanagementtools/puppet/modules/oihealthmonitoring/files/empty
mkdir -p %{buildroot}/opt/toas/cloudmanagementtools/tools/bin/
mkdir -p %{buildroot}/opt/toas/cloudmanagementtools/tools/lib/
cp cloud/cloud-worker/target/cloud-worker-%{version}.jar %{buildroot}/opt/toas/cloudmanagementtools/worker/
cp cloud/cloud-administrator/target/cloud-administrator-%{version}.war %{buildroot}/opt/toas/cloudmanagementtools/portlet/
cp -r cloud/cloud-worker/puppet %{buildroot}/opt/toas/cloudmanagementtools/
cp cloud/cloud-worker/scripts/startworker.sh %{buildroot}/opt/toas/cloudmanagementtools/worker/
cp cloud/cloud-worker/scripts/stopworker.sh %{buildroot}/opt/toas/cloudmanagementtools/worker/

#Cloud Deployer
cp cloud/cloud-deployer-batch/target/cloud-deployer-batch-%{version}.jar %{buildroot}/opt/toas/cloudmanagementtools/deployer/
cp cloud/cloud-deployer/target/cloud-deployer-%{version}.war %{buildroot}/opt/toas/cloudmanagementtools/portlet/
cp cloud/cloud-deployer-batch/scripts/startdeployer.sh %{buildroot}/opt/toas/cloudmanagementtools/deployer/
cp cloud/cloud-deployer-batch/scripts/stopdeployer.sh %{buildroot}/opt/toas/cloudmanagementtools/deployer/

#Cloud Properties Batch
cp cloud/cloud-properties-batch/target/cloud-properties-batch-%{version}.jar %{buildroot}/opt/toas/cloudmanagementtools/properties/bin/
cp cloud/cloud-properties/target/cloud-properties-%{version}.war %{buildroot}/opt/toas/cloudmanagementtools/portlet/
#cp cloud/cloud-properties-batch/scripts/start.sh %{buildroot}/opt/toas/cloudmanagementtools/properties/bin/
#cp cloud/cloud-properties-batch/scripts/stop.sh %{buildroot}/opt/toas/cloudmanagementtools/properties/bin/

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
mkdir -p %{buildroot}/opt/toas/cloudmanagementtools/autoscaler/lib/java
mkdir -p %{buildroot}/opt/toas/cloudmanagementtools/autoscaler/bin
cp -f cloud/cloud-autoscaler/target/cloud-autoscaler-%{version}.jar %{buildroot}/opt/toas/cloudmanagementtools/autoscaler/lib/java/
cp -f cloud/cloud-autoscaler/bin/* %{buildroot}/opt/toas/cloudmanagementtools/autoscaler/bin/

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
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/worker/cloud-worker-%{version}.jar
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/portlet/cloud-administrator-%{version}.war

#Cloud Deployer
%attr(0755,toas,toas)/opt/toas/cloudmanagementtools/deployer/stopdeployer.sh
%attr(0755,toas,toas)/opt/toas/cloudmanagementtools/deployer/startdeployer.sh
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/deployer/cloud-deployer-batch-%{version}.jar
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/portlet/cloud-deployer-%{version}.war
%dir /opt/toas/cloudmanagementtools/deployer/logs
%dir /opt/toas/cloudmanagementtools/deployer/staging

#Cloud Properties
#%attr(0755,toas,toas)/opt/toas/cloudmanagementtools/properties/bin/stop.sh
#%attr(0755,toas,toas)/opt/toas/cloudmanagementtools/properties/bin/start.sh
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/properties/bin/cloud-properties-batch-%{version}.jar
%attr(-,toas,toas)/opt/toas/cloudmanagementtools/portlet/cloud-properties-%{version}.war
%dir /opt/toas/cloudmanagementtools/properties/logs
%dir /opt/toas/cloudmanagementtools/properties/bin


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

# puppet files removed since installed with separate package

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


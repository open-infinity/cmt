%define name	toas_paas_packages
%define version 1.1.0
%define release 1

Name:		%{name}
Version:	%{version}
Release:	%{release}
Summary:	TOAS PaaS RPM packages
Group:		Applications/System
License:	Apache
URL:		http://www.tieto.com/toas
Source0:	toas_paas_rpms-2.0.tar
BuildArch:	noarch
Requires:	httpd, createrepo
Requires(pre):  shadow-utils

%description
This package contains TOAS PaaS RPM packages.

%prep
%setup -T -a 0 -c 

%build
exit 0

%install
mkdir -p %{buildroot}/var/www/html/rhel/6/toas
cp rpms/*.rpm %{buildroot}/var/www/html/rhel/6/toas

%clean
rm -rf %{buildroot}

%pre
getent group toas > /dev/null || groupadd -r toas
getent passwd toas > /dev/null || useradd -r -g toas -s /sbin/nologin toas
exit 0

%files
%defattr(-,toas,toas)
%attr(-,toas,toas)/var/www/html/rhel/6/toas/toas-bigdata-mgmt-1.0.1-1.noarch.rpm
%attr(-,toas,toas)/var/www/html/rhel/6/toas/hadoop-hbase-master-0.90.4+49.1-1.noarch.rpm
%attr(-,toas,toas)/var/www/html/rhel/6/toas/hadoop-hbase-regionserver-0.90.4+49.1-1.noarch.rpm
%attr(-,toas,toas)/var/www/html/rhel/6/toas/oi-liferay-6.1.0-1.noarch.rpm
%attr(-,toas,toas)/var/www/html/rhel/6/toas/oi-hazelcast-libs-1.0-1.noarch.rpm
%attr(-,toas,toas)/var/www/html/rhel/6/toas/haproxy-1.4.20-1.el6.x86_64.rpm
%attr(-,toas,toas)/var/www/html/rhel/6/toas/toas-liferay-dependencies-1.0-1.noarch.rpm
%attr(-,toas,toas)/var/www/html/rhel/6/toas/oi-springdatamongodb-2.0.0-1.noarch.rpm
%attr(-,toas,toas)/var/www/html/rhel/6/toas/oi-mule-3.2.1-1.noarch.rpm
%attr(-,toas,toas)/var/www/html/rhel/6/toas/toas-mule-patches-1.0.1-1.noarch.rpm
%attr(-,toas,toas)/var/www/html/rhel/6/toas/oi-tomee-2.0.0-svn1390813.noarch.rpm
%attr(-,toas,toas)/var/www/html/rhel/6/toas/oi-mariadb-5.2.8-1.x86_64.rpm
%attr(-,toas,toas)/var/www/html/rhel/6/toas/hadoop-hbase-0.90.4+49.1-1.noarch.rpm
%attr(-,toas,toas)/var/www/html/rhel/6/toas/oi-sso-2.0.0-1.noarch.rpm
%attr(-,toas,toas)/var/www/html/rhel/6/toas/hadoop-0.20-datanode-0.20.2+923.142-1.noarch.rpm
%attr(-,toas,toas)/var/www/html/rhel/6/toas/oi-ltt-watch-2.0.0-1.noarch.rpm
%attr(-,toas,toas)/var/www/html/rhel/6/toas/oi-activemq-2.0.0-1.noarch.rpm
%attr(-,toas,toas)/var/www/html/rhel/6/toas/oi-mule-2.0.0-1.noarch.rpm
%attr(-,toas,toas)/var/www/html/rhel/6/toas/mongo-10gen-2.0.2-mongodb_1.x86_64.rpm
%attr(-,toas,toas)/var/www/html/rhel/6/toas/toas-springdata-libs-1.0-1.noarch.rpm
%attr(-,toas,toas)/var/www/html/rhel/6/toas/oi-usagemonitor-2.0.0-1.noarch.rpm
%attr(-,toas,toas)/var/www/html/rhel/6/toas/hadoop-hive-metastore-0.7.1+42.27-2.noarch.rpm
%attr(-,toas,toas)/var/www/html/rhel/6/toas/toas-liferay-extra-libs-1.0-1.noarch.rpm
%attr(-,toas,toas)/var/www/html/rhel/6/toas/epel-release-6-5.noarch.rpm
%attr(-,toas,toas)/var/www/html/rhel/6/toas/oi-theme-2.0.0-1.noarch.rpm
%attr(-,toas,toas)/var/www/html/rhel/6/toas/toas-liferay-theme-1.0-1.noarch.rpm
%attr(-,toas,toas)/var/www/html/rhel/6/toas/oi-connectorj-5.1.14-1.noarch.rpm
%attr(-,toas,toas)/var/www/html/rhel/6/toas/toas-tomcat-1.0-1.noarch.rpm
%attr(-,toas,toas)/var/www/html/rhel/6/toas/hadoop-0.20-secondarynamenode-0.20.2+923.142-1.noarch.rpm
%attr(-,toas,toas)/var/www/html/rhel/6/toas/hadoop-0.20-namenode-0.20.2+923.142-1.noarch.rpm
%attr(-,toas,toas)/var/www/html/rhel/6/toas/hadoop-hive-server-0.7.1+42.27-2.noarch.rpm
%attr(-,toas,toas)/var/www/html/rhel/6/toas/oi-activemq-5.7-1.noarch.rpm
%attr(-,toas,toas)/var/www/html/rhel/6/toas/toas-datanucleus-libs-1.0-1.noarch.rpm
%attr(-,toas,toas)/var/www/html/rhel/6/toas/hadoop-0.20-tasktracker-0.20.2+923.142-1.noarch.rpm
%attr(-,toas,toas)/var/www/html/rhel/6/toas/oi-healthmonitoring-2.0.0-1.noarch.rpm
%attr(-,toas,toas)/var/www/html/rhel/6/toas/oi-core-2.0-1.noarch.rpm
%attr(-,toas,toas)/var/www/html/rhel/6/toas/oi-connectorj-2.0.0-1.noarch.rpm
%attr(-,toas,toas)/var/www/html/rhel/6/toas/mongo-10gen-server-2.0.2-mongodb_1.x86_64.rpm
%attr(-,toas,toas)/var/www/html/rhel/6/toas/toas-querydsl-libs-1.0-1.noarch.rpm
%attr(-,toas,toas)/var/www/html/rhel/6/toas/toas-mule-libs-1.0.1-1.noarch.rpm
%attr(-,toas,toas)/var/www/html/rhel/6/toas/oi-tomee-2.0.0-svn1374504.noarch.rpm
%attr(-,toas,toas)/var/www/html/rhel/6/toas/oi-openam-2.0.0-1.noarch.rpm
%attr(-,toas,toas)/var/www/html/rhel/6/toas/toas-activemq-web-console-1.0-1.noarch.rpm
%attr(-,toas,toas)/var/www/html/rhel/6/toas/toas-activiti-libs-1.0-1.noarch.rpm
%attr(-,toas,toas)/var/www/html/rhel/6/toas/oi-tomcat-liferay-7.0.27-1.noarch.rpm
%attr(-,toas,toas)/var/www/html/rhel/6/toas/oi-springdatamongodb-1.0.1-1.noarch.rpm
%attr(-,toas,toas)/var/www/html/rhel/6/toas/oi-tomcat-2.0.0-1.noarch.rpm
%attr(-,toas,toas)/var/www/html/rhel/6/toas/oi-ltt-watch-2.0.0-2.noarch.rpm
%attr(-,toas,toas)/var/www/html/rhel/6/toas/hadoop-zookeeper-server-3.3.3+12.16-1.noarch.rpm
%attr(-,toas,toas)/var/www/html/rhel/6/toas/oi-tomee-2.0.0-svn105.noarch.rpm
%attr(-,toas,toas)/var/www/html/rhel/6/toas/perl-Collectd-5.0.1-1.el6.rft.x86_64.rpm
%attr(-,toas,toas)/var/www/html/rhel/6/toas/toas-mysql-connector-1.0-1.noarch.rpm
%attr(-,toas,toas)/var/www/html/rhel/6/toas/oi-ssotools-2.0.0-1.noarch.rpm
%attr(-,toas,toas)/var/www/html/rhel/6/toas/toas-mariadb-1.0-1.x86_64.rpm
%attr(-,toas,toas)/var/www/html/rhel/6/toas/toas-core-libs-1.0.1-1.noarch.rpm
%attr(-,toas,toas)/var/www/html/rhel/6/toas/oi-autologin-hook-2.0.0-1.noarch.rpm
%attr(-,toas,toas)/var/www/html/rhel/6/toas/oi-hazelcast-1.0.1-1.noarch.rpm
%attr(-,toas,toas)/var/www/html/rhel/6/toas/oi-core-2.0.0-1.noarch.rpm
%attr(-,toas,toas)/var/www/html/rhel/6/toas/oi-hazelcast-2.0.0-1.noarch.rpm
%attr(-,toas,toas)/var/www/html/rhel/6/toas/oi-activiti-2.0.0-1.noarch.rpm
%attr(-,toas,toas)/var/www/html/rhel/6/toas/toas-activemq-libs-1.0-1.noarch.rpm
%attr(-,toas,toas)/var/www/html/rhel/6/toas/collectd-5.0.1-0.x86_64.rpm
%attr(-,toas,toas)/var/www/html/rhel/6/toas/oi-activiti-5.7-1.noarch.rpm
%attr(-,toas,toas)/var/www/html/rhel/6/toas/hadoop-0.20-jobtracker-0.20.2+923.142-1.noarch.rpm
%attr(-,toas,toas)/var/www/html/rhel/6/toas/oi-springdatahadoop-2.0.0-1.noarch.rpm
%attr(-,toas,toas)/var/www/html/rhel/6/toas/oi-identitygateway-2.0.0-1.noarch.rpm
%attr(-,toas,toas)/var/www/html/rhel/6/toas/toas-fonts-1.0-1.noarch.rpm
%attr(-,toas,toas)/var/www/html/rhel/6/toas/hadoop-0.20-0.20.2+923.142-1.noarch.rpm
%attr(-,toas,toas)/var/www/html/rhel/6/toas/oi-mariadbclient-2.0.0-1.x86_64.rpm
%attr(-,toas,toas)/var/www/html/rhel/6/toas/oi-springdatahadoop-1.0.1-1.noarch.rpm
%attr(-,toas,toas)/var/www/html/rhel/6/toas/oi-liferay-2.0.1-1.noarch.rpm
%attr(-,toas,toas)/var/www/html/rhel/6/toas/oi-alfrescoportlet-2.0.0-1.noarch.rpm
%attr(-,toas,toas)/var/www/html/rhel/6/toas/toas-liferay-1.0-1.noarch.rpm
%attr(-,toas,toas)/var/www/html/rhel/6/toas/toas-mulebisource-1.0-1.noarch.rpm
%attr(-,toas,toas)/var/www/html/rhel/6/toas/hadoop-hive-0.7.1+42.27-2.noarch.rpm
%attr(-,toas,toas)/var/www/html/rhel/6/toas/hadoop-zookeeper-3.3.3+12.16-1.noarch.rpm
%changelog

%post
/usr/bin/createrepo /var/www/html/rhel/6/toas/

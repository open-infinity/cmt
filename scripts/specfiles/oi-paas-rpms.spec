%define name	oi_paas_packages
%define version 3.0.0_MS2
%define release 1

Name:	%{name}
Version:	%{version}
Release:	%{release}
Summary:	OpenInfinity PaaS RPM packages
Group:		Applications/System
License:	Apache
URL:		http://www.tieto.com/openinfinity
Source0:	oi_paas_rpms-%{version}.tar
BuildArch:	noarch
Requires:	httpd, createrepo
Requires(pre):  shadow-utils

%description
This package contains OpenInfinity PaaS RPM packages.

%prep
%setup -T -a 0 -c 

%build
exit 0

%install
mkdir -p %{buildroot}/var/www/html/rhel/6/openinfinity
cp rpms/*.rpm %{buildroot}/var/www/html/rhel/6/openinfinity

%clean
rm -rf %{buildroot}

%pre
getent group oiuser > /dev/null || groupadd -r oiuser
getent passwd oiuser > /dev/null || useradd -r -g oiuser -s /sbin/nologin oiuser
exit 0

%files
%defattr(-,oiuser,oiuser)
%attr(-,oiuser,oiuser)/var/www/html/rhel/6/openinfinity

%changelog

%post
/usr/bin/createrepo /var/www/html/rhel/6/openinfinity/

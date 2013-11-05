%define oipuppetmodules_version 1.2.1
#%define oi_version 3.0.0
%define sourcetag CMT_{oi-puppetmodules_version}

%define release 1
%define CMT_HOME opt/toas/cloudmanagementtools
%define PUPPET_HOME opt/toas/cloudmanagementtools/puppet

Name:           oi-puppetmodules
Version:        %{oi-puppetmodules_version}
Release:        %{release}
Summary:        OpenInfinity puppet modules for MWS installation

Group:         Development/Java
License:        Apache
URL:            http://www.tieto.com/openinfinity

#Source0:        https://github.com/open-infinity/cmt/archive/CMT-1.2.1.zip
Source0:         https://codeload.github.com/open-infinity/cmt/zip/%{sourcetag}

BuildArch:	noarch
#BuildRequires:  
#Requires:   

%description
This package contains OpenInfinity Core libraries %{oicore_version} for OpenInfinity BAS %{oi_version}

%prep
%setup -T -a 0 -c


%build

%install
mkdir -p %{buildroot}/%{PUPPET_HOME}/

cp -r ./cmt-CMT-1.2.1/cloud/cloud-worker/puppet/*  %{buildroot}/%{PUPPET_HOME}/

%clean
rm -rf %{buildroot}

%pre
#getent group oiuser > /dev/null || groupadd -r oiuser
#getent passwd oiuser > /dev/null || useradd -r -g oiuser -s /sbin/nologin oiuser
#exit 0

%files
%defattr(-,root,root)
%attr(-,root,root)/%{PUPPET_HOME}


#%defattr(-,oiuser,oiuser)
#%attr(-,oiuser,oiuser)/%{OI_HOME}
#%attr(0755,oiuser,oiuser)/%{TOMCAT_HOME}/lib/oi-core-libs

%doc

%changelog

%post


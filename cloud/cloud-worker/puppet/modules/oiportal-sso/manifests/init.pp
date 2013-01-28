class oiportal-sso::install {
	package { ["java-1.6.0-openjdk", "oi-connectorj-5.1.14-1", "oi-liferay-2.0.1-1", "oi-core-2.0-1", "oi-sso-2.0.0-1"]:
		ensure => present,
		require => Class["oibasic"],
	}

	file {"/opt/openinfinity/2.0.0/deploy":
                ensure => directory,
                owner => 'toas',
                group => 'toas',
                mode => 0755,
                require => Class["oibasic"],
        }

#	package { ["oi-theme-2.0.0-1"]:
#		ensure => present,
#		require => File["/opt/openinfinity/2.0.0/deploy"],
#	}

	file {"/opt/openinfinity/2.0.0/data":
		ensure => directory,
		owner => 'toas',
		group => 'toas',
		mode => 0755,
		require => Class["oibasic"],
	}
}

class oiportal-sso::config {
	exec {"set-privileges":
		command => "/bin/chown -R toas:toas /opt/openinfinity/2.0.0",
		require => Class["oiportal-sso::install"],
	}

	file {"/opt/openinfinity/2.0.0/tomcat/webapps/ROOT/WEB-INF/classes/portal-ext.properties":
		ensure => present,
		owner => 'toas',
		group => 'toas',
		mode => 0644,
		content => template("oiportal-sso/portal-ext.properties.erb"),
		require => Class["oiportal-sso::install"],
		notify => Service["oi-tomcat"],
	}

	file {"opt/openinfinity/2.0.0/tomcat/lib/org.openinfinity.security.properties":
		ensure => present,
		owner => 'toas',
		group => 'toas',
		mode => 0644,
		content => template("oiportal-sso/org.openinfinity.security.properties.template"),
		require => Class["oiportal-sso::install"],
	}

	file {"/opt/openinfinity/2.0.0/tomcat/conf/catalina.properties":
                ensure => present,
                owner => 'toas',
                group => 'toas',
                mode => 0600,
                source => "puppet:///modules/oiportal-sso/catalina.properties",
                require => Class["oiportal-sso::install"],
        }

	file {"/opt/openinfinity/2.0.0/tomcat/conf/Catalina/localhost/ROOT.xml":
		ensure => present,
		owner => 'toas',
		group => 'toas',
		mode => 0600,
		source => "puppet:///modules/oiportal-sso/ROOT.xml",
		require => File["/opt/openinfinity/2.0.0/tomcat/conf/Catalina/localhost"],
	}

	file {"/opt/openinfinity/2.0.0/tomcat/conf/Catalina/localhost":
                ensure => directory,
                owner => 'toas',
                group => 'toas',
                mode => 0755,
                require => File["/opt/openinfinity/2.0.0/tomcat/conf/Catalina"],
        }

	file {"/opt/openinfinity/2.0.0/tomcat/conf/Catalina":
                ensure => directory,
                owner => 'toas',
                group => 'toas',
                mode => 0755,
                require => Class["oiportal-sso::install"],
        }

        file {"/opt/openinfinity/2.0.0/tomcat/conf/server.xml":
                ensure => present,
                owner => 'toas',
                group => 'toas',
                mode => 0600,
                source => "puppet:///modules/oiportal-sso/server_7.0.27.xml",
                require => Class["oiportal-sso::install"],
        }

	file {"/opt/openinfinity/2.0.0/tomcat/lib/sp1.xml":
		ensure => present,
                owner => 'toas',
                group => 'toas',
		mode => 0644,
		content => template("oiportal-sso/sp1.xml.ssoserviceprovider.template"),
		require => Class["oiportal-sso::install"],
	}

	file {"/opt/openinfinity/2.0.0/tomcat/lib/toasidp-samlr2-metadata.xml":
                ensure => present,
                owner => 'toas',
                group => 'toas',
                mode => 0644,
                content => template("oiportal-sso/toasidp-samlr2-metadata.xml.ssoserviceprovider.template"),
                require => Class["oiportal-sso::install"],
        }

	file {"/opt/openinfinity/2.0.0/tomcat/webapps/ROOT/WEB-INF/web.xml":
                ensure => present,
                owner => 'toas',
                group => 'toas',
                mode => 0644,
                source => "puppet:///modules/oiportal-sso/liferay.web.xml_6.1.ssoserviceprovider",
                require => Class["oiportal-sso::install"],
        }

	file {"/opt/openinfinity/2.0.0/tomcat/lib/samljaas.conf":
                ensure => present,
                owner => 'toas',
                group => 'toas',
                mode => 0644,
                source => "puppet:///modules/oiportal-sso/samljaas.conf.ssoserviceprovider",
                require => Class["oiportal-sso::install"],
        }

        file {"/opt/openinfinity/2.0.0/tomcat/conf/context.xml":
                ensure => present,
                owner => 'toas',
                group => 'toas',
                mode => 0600,
                source => "puppet:///modules/oiportal-sso/context.xml",
                require => Class["oiportal-sso::install"],
        }

        file {"/etc/init.d/oi-tomcat":
                ensure => present,
                owner => 'root',
                group => 'root',
                mode => 0755,
                source => "puppet:///modules/oiportal-sso/oi-tomcat",
                require => Class["oiportal-sso::install"],
        }
}

class oiportal-sso::service {
	service {"oi-tomcat":
		ensure => running,
		hasrestart => true,
		enable => true,
		require => Class["oiportal-sso::config"],
	}
}

class oiportal-sso {
	include oiportal-sso::install
	include oiportal-sso::config
	include oiportal-sso::service
}

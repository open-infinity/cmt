class oiportal::install {
	package { ["java-1.6.0-openjdk", "oi-connectorj-5.1.14-1", "oi-liferay-2.0.1-1", "oi-core-2.0-1"]:
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

class oiportal::config {
	exec {"set-privileges":
		command => "/bin/chown -R toas:toas /opt/openinfinity/2.0.0",
		require => Class["oiportal::install"],
	}

	file {"/opt/openinfinity/2.0.0/tomcat/webapps/ROOT/WEB-INF/classes/portal-ext.properties":
		ensure => present,
		owner => 'toas',
		group => 'toas',
		mode => 0644,
		content => template("oiportal/portal-ext.properties.erb"),
		require => Class["oiportal::install"],
		notify => Service["oi-tomcat"],
	}

	file {"/opt/openinfinity/2.0.0/tomcat/conf/catalina.properties":
                ensure => present,
                owner => 'toas',
                group => 'toas',
                mode => 0600,
                source => "puppet:///modules/oiportal/catalina.properties",
                require => Class["oiportal::install"],
        }

	file {"/opt/openinfinity/2.0.0/tomcat/conf/Catalina/localhost/ROOT.xml":
		ensure => present,
		owner => 'toas',
		group => 'toas',
		mode => 0600,
		source => "puppet:///modules/oiportal/ROOT.xml",
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
                require => Class["oiportal::install"],
        }

        file {"/opt/openinfinity/2.0.0/tomcat/conf/server.xml":
                ensure => present,
                owner => 'toas',
                group => 'toas',
                mode => 0600,
                source => "puppet:///modules/oiportal/server_7.0.27.xml",
                require => Class["oiportal::install"],
        }

        file {"/opt/openinfinity/2.0.0/tomcat/conf/context.xml":
                ensure => present,
                owner => 'toas',
                group => 'toas',
                mode => 0600,
                source => "puppet:///modules/oiportal/context.xml",
                require => Class["oiportal::install"],
        }

        file {"/etc/init.d/oi-tomcat":
                ensure => present,
                owner => 'root',
                group => 'root',
                mode => 0755,
                source => "puppet:///modules/oiportal/oi-tomcat",
                require => Class["oiportal::install"],
        }
}

class oiportal::service {
	service {"oi-tomcat":
		ensure => running,
		hasrestart => true,
		enable => true,
		require => Class["oiportal::config"],
	}
}

class oiportal {
	include oiportal::install
	include oiportal::config
	include oiportal::service
}

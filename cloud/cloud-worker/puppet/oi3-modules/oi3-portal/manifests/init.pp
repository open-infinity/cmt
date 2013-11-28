class oi3-portal::install {
	package { ["java-1.7.0-openjdk", "oi3-connectorj", "oi3-liferay", "oi3-core", "oi3-tomcat"]:
		ensure => present,
		require => Class["oi3-basic"],
	}

	file {"/opt/openinfinity/3.0.0/deploy":
                ensure => directory,
                owner => 'oiuser',
                group => 'oiuser',
                mode => 0755,
                require => Class["oi3-basic"],
        }

#	package { ["oi-theme-2.0.0-1"]:
#		ensure => present,
#		require => File["/opt/openinfinity/2.0.0/deploy"],
#	}

	file {"/opt/openinfinity/3.0.0/data":
		ensure => directory,
		owner => 'oiuser',
		group => 'oiuser',
		mode => 0755,
		require => Class["oi3-basic"],
	}
}

class oi3-portal::config {
	exec {"set-privileges":
		command => "/bin/chown -R oiuser:oiuser /opt/openinfinity/3.0.0",
		require => Class["oi3-portal::install"],
	}

	file {"/opt/openinfinity/3.0.0/tomcat/webapps/ROOT/WEB-INF/classes/portal-ext.properties":
		ensure => present,
		owner => 'oiuser',
		group => 'oiuser',
		mode => 0644,
		content => template("oi3-portal/portal-ext.properties.erb"),
		require => Class["oi3-portal::install"],
		notify => Service["oi-tomcat"],
	}

	file {"/opt/openinfinity/3.0.0/tomcat/conf/catalina.properties":
                ensure => present,
                owner => 'oiuser',
                group => 'oiuser',
                mode => 0600,
                source => "puppet:///modules/oi3-portal/catalina.properties",
                require => Class["oi3-portal::install"],
        }

	file {"/opt/openinfinity/3.0.0/tomcat/bin/setenv.sh":
                ensure => present,
                owner => 'oiuser',
                group => 'oiuser',
                mode => 0755,
                content => template("oi3-portal/setenv.sh.erb"),
                require => Class["oi3-portal::install"],
        }

	file {"/opt/openinfinity/3.0.0/tomcat/conf/Catalina/localhost/ROOT.xml":
		ensure => present,
		owner => 'oiuser',
		group => 'oiuser',
		mode => 0600,
		source => "puppet:///modules/oi3-portal/ROOT.xml",
		require => File["/opt/openinfinity/3.0.0/tomcat/conf/Catalina/localhost"],
	}

	file {"/opt/openinfinity/3.0.0/tomcat/conf/Catalina/localhost":
                ensure => directory,
                owner => 'oiuser',
                group => 'oiuser',
                mode => 0755,
                require => File["/opt/openinfinity/3.0.0/tomcat/conf/Catalina"],
        }

	file {"/opt/openinfinity/3.0.0/tomcat/conf/Catalina":
                ensure => directory,
                owner => 'oiuser',
                group => 'oiuser',
                mode => 0755,
                require => Class["oi3-portal::install"],
        }

        file {"/opt/openinfinity/3.0.0/tomcat/conf/server.xml":
                ensure => present,
                owner => 'oiuser',
                group => 'oiuser',
                mode => 0600,
                source => "puppet:///modules/oi3-portal/server.xml",
                require => Class["oi3-portal::install"],
        }

        file {"/opt/openinfinity/3.0.0/tomcat/conf/context.xml":
                ensure => present,
                owner => 'oiuser',
                group => 'oiuser',
                mode => 0600,
                source => "puppet:///modules/oi3-portal/context.xml",
                require => Class["oi3-portal::install"],
        }

        file {"/opt/openinfinity/3.0.0/tomcat/conf/jmxremote.password":
                ensure => present,
                owner => 'oiuser',
                group => 'oiuser',
                mode => 0600,
                content => template("oi3-portal/jmxremote.password.erb"),
                require => Class["oi3-portal::install"],
        }
        
        file {"/opt/openinfinity/3.0.0/tomcat/conf/jmxremote.access":
                ensure => present,
                owner => 'oiuser',
                group => 'oiuser',
                mode => 0644,
                source => "puppet:///modules/oi3-portal/jmxremote.access",
                require => Class["oi3-portal::install"],
        }

        file {"/etc/init.d/oi-tomcat":
                ensure => present,
                owner => 'root',
                group => 'root',
                mode => 0755,
                source => "puppet:///modules/oi3-portal/oi-tomcat",
                require => Class["oi3-portal::install"],
        }
	
	file {"/opt/openinfinity/3.0.0/portal-setup-wizard.properties":
		ensure => present,
		owner => 'oiuser',
		group => 'oiuser',
		mode => 0644,
		source => "puppet:///modules/oi3-portal/portal-setup-wizard.properties",
		require => Class["oi3-portal::install"],
	}
#  file { "/opt/openinfinity/3.0.0/oi-core-libs/deps/ehcache-core-2.6.6.jar":
#    ensure  => absent,
#                require => Class["oi3-portal::install"],
#  }
}

class oi3-portal::service {
	service {"oi-tomcat":
		ensure => running,
		hasrestart => true,
		enable => true,
		require => Class["oi3-portal::config"],
	}
}

class oi3-portal {
	require oi3-ebs
	require oi3-basic	
	include oi3-portal::install
	include oi3-portal::config
	include oi3-portal::service
}


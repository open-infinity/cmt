class oiportal-sso::install {
	package { ["java-1.7.0-openjdk", "oi3-connectorj", "oi3-liferay", "oi3-core", "oi3-tomcat", "oi3-secvault", "oi3-hazelcast", "oi3-preauthautologin"]:
		ensure => present,
		require => Class["oi3-basic"],
	}

#	package {"oi-autologin-3.0.0-1":
#		ensure => present,
#		require => Package["oi3-liferay"],
#	}

	file {"/opt/openinfinity/3.0.0/deploy":
                ensure => directory,
                owner => 'oiuser',
                group => 'oiuser',
                mode => 0755,
                require => Class["oi3-basic"],
        }

#	package { ["oi-theme-3.0.0-1"]:
#		ensure => present,
#		require => File["/opt/openinfinity/3.0.0/deploy"],
#	}

	file {"/opt/openinfinity/3.0.0/data":
		ensure => directory,
		owner => 'oiuser',
		group => 'oiuser',
		mode => 0755,
		require => Class["oi3-basic"],
	}
}

class oiportal-sso::config {
	exec {"set-privileges":
		command => "/bin/chown -R oiuser:oiuser /opt/openinfinity/3.0.0",
		require => Class["oi3-portal-sso::install"],
	}

# ok
	file {"/opt/openinfinity/3.0.0/tomcat/webapps/ROOT/WEB-INF/classes/portal-ext.properties":
		ensure => present,
		owner => 'oiuser',
		group => 'oiuser',
		mode => 0644,
		content => template("oi3-portal-sso/portal-ext.properties.erb"),
		require => Class["oi3-portal-sso::install"],
		notify => Service["oi-tomcat"],
	}

	file {"/opt/openinfinity/3.0.0/tomcat/conf/catalina.properties":
                ensure => present,
		        owner => 'oiuser',
	 	        group => 'oiuser',
                mode => 0600,
                source => "puppet:///modules/oi3-portal-sso/catalina.properties",
                require => Class["oi3-portal-sso::install"],
        }

	file {"/opt/openinfinity/3.0.0/tomcat/bin/setenv.sh":
                ensure => present,
                owner => 'oiuser',
                group => 'oiuser',
                mode => 0755,
                content => template("oi3-portal-sso/setenv.sh.erb"),
                require => Class["oi3-portal-sso::install"],
        }

	file {"/opt/openinfinity/3.0.0/tomcat/conf/Catalina/localhost/ROOT.xml":
		ensure => present,
		owner => 'oiuser',
		group => 'oiuser',
		mode => 0600,
		source => "puppet:///modules/oi3-portal-sso/ROOT.xml",
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
                require => Class["oi3-portal-sso::install"],
        }

        file {"/opt/openinfinity/3.0.0/tomcat/conf/server.xml":
                ensure => present,
			    owner => 'oiuser',
			    group => 'oiuser',
                mode => 0600,
                source => "puppet:///modules/oi3-portal-sso/server.xml",
                require => Class["oi3-portal-sso::install"],
        }


	# Security Vault configuration
	file {"/opt/openinfinity/3.0.0/tomcat/conf/securityvault.properties":
		ensure => present,
		owner => 'oiuser',
		group => 'oiuser',
		mode => 0600,
		source => "puppet:///modules/oi3-portal-sso/securityvault.properties",
		require => Class["oi3-portal-sso::install"],
	}

	file {"/opt/openinfinity/3.0.0/tomcat/conf/hazelcast.xml":
		ensure => present,
		owner => 'oiuser',
		group => 'oiuser',
		mode => 0600,
		content => template("oi3-portal-sso/hazelcast.xml.erb"),
		require => Class["oi3-portal-sso::install"],
	}
#preauthfilter need to be added, verify right base xml for 6.2, web.xml or liferay_web.xml
#	file {"/opt/openinfinity/3.0.0/tomcat/webapps/ROOT/WEB-INF/web.xml":
#                ensure => present,
#			    owner => 'oiuser',
#	    		group => 'oiuser',
#                mode => 0644,
#                source => "puppet:///modules/oi3-portal-sso/liferay.web.xml_6.1.ssoserviceprovider",
#                require => Class["oi3-portal-sso::install"],
#        }

        file {"/opt/openinfinity/3.0.0/tomcat/conf/context.xml":
                ensure => present,
			    owner => 'oiuser',
	    		group => 'oiuser',
                mode => 0600,
                source => "puppet:///modules/oi3-portal-sso/context.xml",
                require => Class["oi3-portal-sso::install"],
        }

        file {"/opt/openinfinity/3.0.0/tomcat/conf/jmxremote.password":
                ensure => present,
                owner => 'oiuser',
                group => 'oiuser',
                mode => 0600,
                content => template("oi3-portal-sso/jmxremote.password.erb"),
                require => Class["oi3-portal-sso::install"],
        }
        
        file {"/opt/openinfinity/3.0.0/tomcat/conf/jmxremote.access":
                ensure => present,
                owner => 'oiuser',
                group => 'oiuser',
                mode => 0644,
                source => "puppet:///modules/oi3-portal-sso/jmxremote.access",
                require => Class["oi3-portal-sso::install"],
        }
        
        file {"/etc/init.d/oi-tomcat":
                ensure => present,
                owner => 'root',
                group => 'root',
                mode => 0755,
                source => "puppet:///modules/oi3-portal-sso/oi-tomcat",
                require => Class["oi3-portal-sso::install"],
        }

	file {"/opt/openinfinity/3.0.0/portal-setup-wizard.properties":
                ensure => present,
			    owner => 'oiuser',
			    group => 'oiuser',
                mode => 0644,
                source => "puppet:///modules/oi3-portal-sso/portal-setup-wizard.properties",
                require => Class["oi3-portal-sso::install"],
        }
}

class oiportal-sso::service {
	service {"oi-tomcat":
		ensure => running,
		hasrestart => true,
		enable => true,
		require => Class["oi3-portal-sso::config"],
	}
}

class oi3-portal-sso {
	require oi3-ebs
	require oi3-basic		
	include oi3-portal-sso::install
	include oi3-portal-sso::config
	include oi3-portal-sso::service
}

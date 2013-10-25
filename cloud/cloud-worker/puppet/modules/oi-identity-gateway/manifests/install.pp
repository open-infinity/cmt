class oi-identity-gateway::install {
	package { ["java-1.6.0-openjdk", "oi-ssotools-2.0.0-1", "oi-tomcat-2.0.0-1"]:
		ensure => present,
		require => Class["oibasic"]
	}

	package { ["oi-identitygateway-2.0.0-1"]:
		ensure => present,
		require => Package["oi-tomcat-2.0.0-1"]
	}
	
	package {"haproxy":
		ensure => installed
	}

	exec {"set-privileges":
                command => "/bin/chown -R toas:toas /opt/openinfinity/2.0.0",
                require => Package["oi-identitygateway-2.0.0-1"],
		tries => 3
        }
}

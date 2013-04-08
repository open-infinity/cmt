class oibas::config {
	exec {"set-privileges":
                command => "/bin/chown -R toas:toas /opt/openinfinity/2.0.0",
                require => Class["oibas::install"],
        }

	file {"/opt/openinfinity/2.0.0/tomcat/bin/setenv.sh":
                ensure => present,
                owner => 'toas',
                group => 'toas',
                mode => 0755,
                source => "puppet:///modules/oibas/setenv.sh",
                require => Class["oibas::install"],
        }

	file {"/opt/openinfinity/2.0.0/tomcat/conf/catalina.properties":
		ensure => present,
		owner => 'toas',
		group => 'toas',
		mode => 0600,
		source => "puppet:///modules/oibas/catalina.properties",
		require => Class["oibas::install"],
		notify => Service["oi-tomcat"],
	}

	file {"/opt/openinfinity/2.0.0/tomcat/conf/server.xml":
		ensure => present,
		owner => 'toas',
		group => 'toas',
		mode => 0600,
		source => "puppet:///modules/oibas/server_7.0.27.xml",
		require => Class["oibas::install"],
	}

	file {"/opt/openinfinity/2.0.0/tomcat/conf/context.xml":
		ensure => present,
		owner => 'toas',
		group => 'toas',
		mode => 0600,
		source => "puppet:///modules/oibas/context.xml",
		require => Class["oibas::install"],
	}

	file {"/opt/openinfinity/2.0.0/tomcat/conf/hazelcast.xml":
		ensure => present,
		owner => 'toas',
		group => 'toas',
		mode => 0600,
		content => template("oibas/hazelcast.xml.erb"),
		require => Class["oibas::install"],
	}

	file {"/etc/init.d/oi-tomcat":
                ensure => present,
                owner => 'root',
                group => 'root',
                mode => 0755,
                source => "puppet:///modules/oibas/oi-tomcat",
                require => Class["oibas::install"],
    }

    # Try ensure, that the supported Java is chosen
    exec { "choose-java":
            command => "/usr/sbin/alternatives --install /usr/bin/java java /usr/lib/jvm/jre-1.6.0-openjdk.x86_64/bin/java 190000",
            require => Package["java-1.6.0-openjdk"]
    }
}


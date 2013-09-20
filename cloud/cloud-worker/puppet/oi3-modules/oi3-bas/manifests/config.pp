class oi3-bas::config {
	exec {"set-privileges":
                command => "/bin/chown -R oiuser:oiuser /opt/openinfinity/3.0.0",
                require => Class["oi3-bas::install"],
        }

	file {"/opt/openinfinity/3.0.0/tomcat/bin/setenv.sh":
                ensure => present,
                owner => 'oiuser',
                group => 'oiuser',
                mode => 0755,
                source => "puppet:///modules/oi3-bas/setenv.sh",
                require => Class["oi3-bas::install"],
        }

	file {"/opt/openinfinity/3.0.0/tomcat/conf/catalina.properties":
		ensure => present,
		owner => 'oiuser',
		group => 'oiuser',
		mode => 0600,
		source => "puppet:///modules/oi3-bas/catalina.properties",
		require => Class["oi3-bas::install"],
		notify => Service["oi-tomcat"],
	}

	file {"/opt/openinfinity/3.0.0/tomcat/conf/server.xml":
		ensure => present,
		owner => 'oiuser',
		group => 'oiuser',
		mode => 0600,
		source => "puppet:///modules/oi3-bas/server.xml",
		require => Class["oi3-bas::install"],
	}

	file {"/opt/openinfinity/3.0.0/tomcat/conf/context.xml":
		ensure => present,
		owner => 'oiuser',
		group => 'oiuser',
		mode => 0600,
		source => "puppet:///modules/oi3-bas/context.xml",
		require => Class["oi3-bas::install"],
	}

	file {"/opt/openinfinity/3.0.0/tomcat/conf/hazelcast.xml":
		ensure => present,
		owner => 'oiuser',
		group => 'oiuser',
		mode => 0600,
		content => template("oi3-bas/hazelcast.xml.erb"),
		require => Class["oi3-bas::install"],
	}

	file {"/etc/init.d/oi-tomcat":
                ensure => present,
                owner => 'root',
                group => 'root',
                mode => 0755,
                source => "puppet:///modules/oi3-bas/oi-tomcat",
                require => Class["oi3-bas::install"],
    }

    # Try ensure, that the supported Java is chosen
    exec { "choose-java":
            command => "/usr/sbin/alternatives --install /usr/bin/java java /usr/lib/jvm/jre-1.6.0-openjdk.x86_64/bin/java 190000",
            require => Package["java-1.6.0-openjdk"]
    }
}


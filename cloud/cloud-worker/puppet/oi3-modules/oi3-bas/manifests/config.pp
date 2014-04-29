class oi3-bas::config {

	file {"/opt/openinfinity/3.0.0/tomcat/bin/setenv.sh":
                ensure => present,
                owner => 'oiuser',
                group => 'oiuser',
                mode => 0755,
                content => template("oi3-bas/setenv.sh.erb"),
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
		#source => "puppet:///modules/oi3-bas/server.xml",
		content => template("oi3-bas/server.xml.erb"),
		require => Class["oi3-bas::install"],
	}

	# Security Vault configuration
	file {"/opt/openinfinity/3.0.0/tomcat/conf/securityvault.properties":
		ensure => present,
		owner => 'oiuser',
		group => 'oiuser',
		mode => 0600,
		source => "puppet:///modules/oi3-bas/securityvault.properties",
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
    
        file {"/opt/openinfinity/3.0.0/tomcat/conf/jmxremote.password":
                ensure => present,
                owner => 'oiuser',
                group => 'oiuser',
                mode => 0600,
                content => template("oi3-bas/jmxremote.password.erb"),
                require => Class["oi3-bas::install"],
        }
        
        file {"/opt/openinfinity/3.0.0/tomcat/conf/jmxremote.access":
                ensure => present,
                owner => 'oiuser',
                group => 'oiuser',
                mode => 0644,
                source => "puppet:///modules/oi3-bas/jmxremote.access",
                require => Class["oi3-bas::install"],
        }
    

    # Try ensure, that the supported Java is chosen
    exec { "choose-java":
            command => "/usr/sbin/alternatives --install /usr/bin/java java /usr/lib/jvm/jre-1.7.0-openjdk.x86_64/bin/java 190000",
            require => Package["java-1.7.0-openjdk"]
    }
}


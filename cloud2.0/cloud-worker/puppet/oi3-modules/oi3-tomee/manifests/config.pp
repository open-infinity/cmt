class oi3-tomee::config {

    file {"/opt/openinfinity/3.1.0/tomcat/bin/setenv.sh":
        ensure => present,
        owner => 'oiuser',
        group => 'oiuser',
        mode => 0755,
        content => template("oi3-tomee/setenv.sh.erb"),
        require => Class["oi3-tomee::install"],
    }

	file {"/opt/openinfinity/3.1.0/tomcat/conf/catalina.properties":
		ensure => present,
		owner => 'oiuser',
		group => 'oiuser',
		mode => 0600,
		source => "puppet:///modules/oi3-tomee/catalina.properties",
		require => Class["oi3-tomee::install"],
		notify => Service["oi-tomcat"],
	}

    file {"/opt/openinfinity/3.1.0/tomcat/conf/server.xml":
        ensure => present,
        owner => 'oiuser',
        group => 'oiuser',
        mode => 0644,
		content => template("oi3-tomee/server.xml.erb"),
        #source => "puppet:///modules/oi3-tomee/server.xml",
        require => Class["oi3-tomee::install"],
    }

    file {"/opt/openinfinity/3.1.0/tomcat/conf/tomcat-users.xml":
        ensure => present,
        owner => 'oiuser',
        group => 'oiuser',
        mode => 0644,
        source => "puppet:///modules/oi3-tomee/tomcat-users.xml",
        require => Class["oi3-tomee::install"],
    }

    file {"/opt/openinfinity/3.1.0/tomcat/conf/logging.properties":
        ensure => present,
        owner => 'oiuser',
        group => 'oiuser',
        mode => 0644,
        source => "puppet:///modules/oi3-tomee/logging.properties",
        require => Class["oi3-tomee::install"],
    }

    file {"/opt/openinfinity/3.1.0/tomcat/conf/system.properties":
        ensure => present,
        owner => 'oiuser',
        group => 'oiuser',
        mode => 0644,
        source => "puppet:///modules/oi3-tomee/system.properties",
        require => Class["oi3-tomee::install"],
    }

    file {"/opt/openinfinity/3.1.0/tomcat/conf/tomee.xml":
        ensure => present,
        owner => 'oiuser',
        group => 'oiuser',
        mode => 0644,
        source => "puppet:///modules/oi3-tomee/tomee.xml",
        require => Class["oi3-tomee::install"],
    }

    file {"/opt/openinfinity/3.1.0/tomcat/conf/web.xml":
        ensure => present,
        owner => 'oiuser',
        group => 'oiuser',
        mode => 0644,
        source => "puppet:///modules/oi3-tomee/web.xml",
        require => Class["oi3-tomee::install"],
    }

	file { "/opt/openinfinity/3.1.0/tomcat/apps":
		ensure => directory,
		group => "oiuser",
		owner => "oiuser",
		require => Class["oi3-tomee::install"],
	}	

    # ---- From BAS only -------------------------------------------------------

	# Security Vault configuration
	file {"/opt/openinfinity/3.1.0/tomcat/conf/securityvault.properties":
		ensure => present,
		owner => 'oiuser',
		group => 'oiuser',
		mode => 0600,
		source => "puppet:///modules/oi3-tomee/securityvault.properties",
		require => Class["oi3-tomee::install"],
	}

	file {"/opt/openinfinity/3.1.0/tomcat/conf/context.xml.openinfinity_example":
		ensure => present,
		owner => 'oiuser',
		group => 'oiuser',
		mode => 0600,
		source => "puppet:///modules/oi3-tomee/context.xml",
		require => Class["oi3-tomee::install"],
	}

	file {"/opt/openinfinity/3.1.0/tomcat/conf/hazelcast.xml":
		ensure => present,
		owner => 'oiuser',
		group => 'oiuser',
		mode => 0600,
		content => template("oi3-tomee/hazelcast.xml.erb"),
		require => Class["oi3-tomee::install"],
	}

	file {"/etc/init.d/oi-tomcat":
        ensure => present,
        owner => 'root',
        group => 'root',
        mode => 0755,
        source => "puppet:///modules/oi3-tomee/oi-tomcat",
        require => Class["oi3-tomee::install"],
	}
    
    file {"/opt/openinfinity/3.1.0/tomcat/conf/jmxremote.password":
        ensure => present,
        owner => 'oiuser',
        group => 'oiuser',
        mode => 0600,
        content => template("oi3-tomee/jmxremote.password.erb"),
        require => Class["oi3-tomee::install"],
    }
    
    file {"/opt/openinfinity/3.1.0/tomcat/conf/jmxremote.access":
        ensure => present,
        owner => 'oiuser',
        group => 'oiuser',
        mode => 0644,
        source => "puppet:///modules/oi3-tomee/jmxremote.access",
        require => Class["oi3-tomee::install"],
    }

    # Try ensure, that the supported Java is chosen
    exec { "choose-java":
        command => "/usr/sbin/alternatives --install /usr/bin/java java /usr/lib/jvm/jre-1.7.0-openjdk.x86_64/bin/java 190000",
        require => Package["java-1.7.0-openjdk"]
    }

    # --------------------------------------------------------------------------
}


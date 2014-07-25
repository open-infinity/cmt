class oi3-bas::config inherits oi3variables {

    file { "/opt/openinfinity/3.1.0/tomcat/bin/setenv.sh":
        ensure => present,
        owner => 'oiuser',
        group => 'oiuser',
        mode => 0755,
        content => template("oi3-bas/setenv.sh.erb"),
        require => Class["oi3-bas::install"],
        notify => Service["oi-tomcat"],
    }

    file {"/opt/openinfinity/3.1.0/tomcat/conf/catalina.properties":
        ensure => present,
        owner => 'oiuser',
        group => 'oiuser',
        mode => 0600,
        source => "puppet:///modules/oi3-bas/catalina.properties",
        require => Class["oi3-bas::install"],
        notify => Service["oi-tomcat"],
    }

    file {"/opt/openinfinity/3.1.0/tomcat/conf/server.xml":
        ensure => present,
        owner => 'oiuser',
        group => 'oiuser',
        mode => 0600,
        #source => "puppet:///modules/oi3-bas/server.xml",
        content => template("oi3-bas/server.xml.erb"),
        require => Class["oi3-bas::install"],
    }

    # Security Vault configuration
    file {"/opt/openinfinity/3.1.0/tomcat/conf/securityvault.properties":
        ensure => present,
        owner => 'oiuser',
        group => 'oiuser',
        mode => 0600,
        source => "puppet:///modules/oi3-bas/securityvault.properties",
        require => Class["oi3-bas::install"],
    }

    file {"/opt/openinfinity/3.1.0/tomcat/conf/context.xml.openinfinity_example":
        ensure => present,
        owner => 'oiuser',
        group => 'oiuser',
        mode => 0600,
        source => "puppet:///modules/oi3-bas/context.xml",
        require => Class["oi3-bas::install"],
    }

    file {"/opt/openinfinity/3.1.0/tomcat/conf/hazelcast.xml":
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
        #source => "puppet:///modules/oi3-bas/oi-tomcat",
        content => template("oi3-bas/oi-tomcat.erb"),
        require => Class["oi3-bas::install"],
    }
    
    file {"/opt/openinfinity/3.1.0/tomcat/conf/jmxremote.password":
        ensure => present,
        owner => 'oiuser',
        group => 'oiuser',
        mode => 0600,
        content => template("oi3-bas/jmxremote.password.erb"),
        require => Class["oi3-bas::install"],
    }
        
    file {"/opt/openinfinity/3.1.0/tomcat/conf/jmxremote.access":
        ensure => present,
        owner => 'oiuser',
        group => 'oiuser',
        mode => 0644,
        source => "puppet:///modules/oi3-bas/jmxremote.access",
        require => Class["oi3-bas::install"],
    }
    

    # Try ensure, that the supported Java is chosen
    exec { "choose-java":
        path => "/",
        command => "${alternativesPath} --install /usr/bin/java java ${javaHome}/bin/java 190000",
        unless => "${alternativesPath} --display java | /bin/grep 'link currently points to ${javaHome}/bin/java'",
        require => Package[$javaPackageName],
    }
}



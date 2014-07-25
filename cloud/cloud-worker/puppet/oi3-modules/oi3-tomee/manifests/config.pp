class oi3-tomee::config inherits oi3variables {

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
        content => template("oi3-tomee/oi-tomcat.erb"),
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
        path => "/",
        command => "${alternativesPath} --install /usr/bin/java java ${javaHome}/bin/java 190000",
        unless => "${alternativesPath} --display java | /bin/grep 'link currently points to ${javaHome}/bin/java'",
        require => Package[$javaPackageName],
    }

    # --------------------------------------------------------------------------
}


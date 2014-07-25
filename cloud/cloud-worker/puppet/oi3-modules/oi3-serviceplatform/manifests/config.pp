class oi3-serviceplatform::config inherits oi3variables {

    file { "/opt/openinfinity/3.1.0/tomcat/bin/setenv.sh":
        ensure => present,
        owner => 'oiuser',
        group => 'oiuser',
        mode => 0755,
        content => template("oi3-serviceplatform/setenv.sh.erb"),
        require => Class["oi3-serviceplatform::install"],
        notify => Service["oi-tomcat"],
    }

    file {"/opt/openinfinity/3.1.0/tomcat/conf/catalina.properties":
        ensure => present,
        owner => 'oiuser',
        group => 'oiuser',
        mode => 0600,
        source => "puppet:///modules/oi3-serviceplatform/catalina.properties",
        require => Class["oi3-serviceplatform::install"],
        notify => Service["oi-tomcat"],
    }

    file {"/opt/openinfinity/3.1.0/tomcat/conf/tomcat-users.xml":
        ensure => present,
        owner => 'oiuser',
        group => 'oiuser',
        mode => 0644,
        content => template("oi3-serviceplatform/tomcat-users.xml.erb"),
        require => Class["oi3-serviceplatform::install"],
    }


    #rights may require change
    file {"/opt/openinfinity/3.1.0/tomcat/conf/activemq.xml":
        ensure => present,
        owner => 'oiuser',
        group => 'oiuser',
        mode => 0644,
        content => template("oi3-serviceplatform/activemq.xml.erb"),
        require => Class["oi3-serviceplatform::install"],
    }

    file {"/opt/data/.mule":
        ensure => directory,
        owner => 'oiuser',
        group => 'oiuser',
        mode => 0644,
        require => Class["oi3-serviceplatform::install"],
    }

    # activity webapp configuration override

    file {"/opt/openinfinity/3.1.0/tomcat/webapps/activiti-explorer2/WEB-INF/classes/db.properties":
        ensure => present,
        owner => 'oiuser',
        group => 'oiuser',
        mode => 0644,
        content => template("oi3-serviceplatform/activiti.db.properties.erb"),
        require => Class["oi3-serviceplatform::install"],
    }

    file {"/opt/openinfinity/3.1.0/tomcat/webapps/activiti-rest2/WEB-INF/classes/db.properties":
        ensure => present,
        owner => 'oiuser',
        group => 'oiuser',
        mode => 0644,
        content => template("oi3-serviceplatform/activiti.db.properties.erb"),
        require => Class["oi3-serviceplatform::install"],
    }
    
    file {"/opt/openinfinity/3.1.0/tomcat/webapps/activiti-explorer2/WEB-INF/activiti-standalone-context.xml":
        ensure => present,
        owner => 'oiuser',
        group => 'oiuser',
        mode => 0644,
        source => "puppet:///modules/oi3-serviceplatform/activiti-explorer-standalone-context.xml",     
        require => Class["oi3-serviceplatform::install"],
    }
    
    file {"/opt/openinfinity/3.1.0/tomcat/webapps/activiti-rest2/WEB-INF/classes/activiti-context.xml":
        ensure => present,
        owner => 'oiuser',
        group => 'oiuser',
        mode => 0644,
        source => "puppet:///modules/oi3-serviceplatform/activiti-rest-context.xml",        
        require => Class["oi3-serviceplatform::install"],
    }
    
    file {"/opt/openinfinity/3.1.0/tomcat/webapps/activiti-rest2/WEB-INF/web.xml":
        ensure => present,
        owner => 'oiuser',
        group => 'oiuser',
        mode => 0644,
        source => "puppet:///modules/oi3-serviceplatform/activiti-rest-web.xml",        
        require => Class["oi3-serviceplatform::install"],
    }

    # activemq-web-console webapp configuration override

    file {"/opt/openinfinity/3.1.0/tomcat/webapps/activemq-web-console/WEB-INF/webconsole-embedded.xml":
        ensure => present,
        owner => 'oiuser',
        group => 'oiuser',
        mode => 0644,
        source => "puppet:///modules/oi3-serviceplatform/webconsole-embedded.xml",      
        require => Class["oi3-serviceplatform::install"],
    }

    file {"/opt/openinfinity/3.1.0/tomcat/webapps/activemq-web-console/WEB-INF/web.xml":
        ensure => present,
        owner => 'oiuser',
        group => 'oiuser',
        mode => 0644,
        source => "puppet:///modules/oi3-serviceplatform/amqwebconsole_web.xml",        
        require => Class["oi3-serviceplatform::install"],
    }

    # oauth webapp configuration override
    file {"/opt/openinfinity/3.1.0/tomcat/webapps/oauth/WEB-INF/classes/oauth-repository.properties":
        ensure => present,
        owner => 'oiuser',
        group => 'oiuser',
        mode => 0644,
        content => template("oi3-serviceplatform/oauth-repository.properties.erb"),
        require => Class["oi3-serviceplatform::install"],
    }

#    # ActiveMQ web console credentials
#   file {"/opt/openinfinity/3.1.0/tomcat/conf/credentials.properties":
#       ensure => present,
#       owner => 'oiuser',
#       group => 'oiuser',
#       mode => 0600,
#       content => template("oi3-serviceplatform/credentials.properties.erb"),
#       require => Class["oi3-serviceplatform::install"],
#   }

#   # ActiveMQ SiteMesh dependency file
#   file {"/opt/openinfinity/3.1.0/tomcat/webapps/activemq-web-console/WEB-INF/decorators.xml":
#       ensure => present,
#       owner => 'oiuser',
#       group => 'oiuser',
#       mode => 0600,
#       content => template("oi3-serviceplatform/decorators.xml.erb"),
#       require => Class["oi3-serviceplatform::install"],
#   }

    # ---- From oi3-bas --------------------------------------------------------
    file {"/opt/openinfinity/3.1.0/tomcat/conf/server.xml":
        ensure => present,
        owner => 'oiuser',
        group => 'oiuser',
        mode => 0600,
        #source => "puppet:///modules/oi3-bas/server.xml",
        content => template("oi3-bas/server.xml.erb"),
        require => Class["oi3-serviceplatform::install"],
    }

    # Security Vault configuration
    file {"/opt/openinfinity/3.1.0/tomcat/conf/securityvault.properties":
        ensure => present,
        owner => 'oiuser',
        group => 'oiuser',
        mode => 0600,
        source => "puppet:///modules/oi3-bas/securityvault.properties",
        require => Class["oi3-serviceplatform::install"],
    }

    file {"/opt/openinfinity/3.1.0/tomcat/conf/context.xml.openinfinity_example":
        ensure => present,
        owner => 'oiuser',
        group => 'oiuser',
        mode => 0600,
        source => "puppet:///modules/oi3-bas/context.xml",
        require => Class["oi3-serviceplatform::install"],
    }

    file {"/opt/openinfinity/3.1.0/tomcat/conf/hazelcast.xml":
        ensure => present,
        owner => 'oiuser',
        group => 'oiuser',
        mode => 0600,
        content => template("oi3-bas/hazelcast.xml.erb"),
        require => Class["oi3-serviceplatform::install"],
    }

    file {"/etc/init.d/oi-tomcat":
        ensure => present,
        owner => 'root',
        group => 'root',
        mode => 0755,
        #source => "puppet:///modules/oi3-bas/oi-tomcat",
        content => template("oi3-bas/oi-tomcat.erb"),
        require => Class["oi3-serviceplatform::install"],
    }
    
    file {"/opt/openinfinity/3.1.0/tomcat/conf/jmxremote.password":
        ensure => present,
        owner => 'oiuser',
        group => 'oiuser',
        mode => 0600,
        content => template("oi3-bas/jmxremote.password.erb"),
        require => Class["oi3-serviceplatform::install"],
    }
        
    file {"/opt/openinfinity/3.1.0/tomcat/conf/jmxremote.access":
        ensure => present,
        owner => 'oiuser',
        group => 'oiuser',
        mode => 0644,
        source => "puppet:///modules/oi3-bas/jmxremote.access",
        require => Class["oi3-serviceplatform::install"],
    }

    # Try ensure, that the supported Java is chosen
        exec { "choose-java":
        path => "/",
        command => "${alternativesPath} --install /usr/bin/java java ${javaHome}/bin/java 190000",
        unless => "${alternativesPath} --display java | /bin/grep 'link currently points to ${javaHome}/bin/java'",
        require => Package[$javaPackageName],
    }
}


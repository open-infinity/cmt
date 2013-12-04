class oi3-tomee {
        require oi3-ebs
        require oi3-basic
        include oi3-bas
        include oi3-tomee::install
        include oi3-tomee::config
        include oi3-tomee::service
}

class oi3-tomee::install {
        package { ["oi3-tomeeplus" ]:
                ensure => present,
                require => Class["oi3-bas"],
        }
}

class oi3-tomee::config inherits oi3-bas::config {

        File ["/opt/openinfinity/3.0.0/tomcat/conf/server.xml"] {
                ensure => present,
                owner => 'oiuser',
                group => 'oiuser',
                mode => 0644,
                source => "puppet:///modules/oi3-tomee/server.xml",
                require => Class["oi3-tomee::install"],
        }

        File ["/opt/openinfinity/3.0.0/tomcat/bin/setenv.sh"] {
                ensure => present,
                owner => 'oiuser',
                group => 'oiuser',
                mode => 0644,
                content => template("oi3-tomee/setenv.sh.erb"),
                require => Class["oi3-tomee::install"],
        }

        file {"/opt/openinfinity/3.0.0/tomcat/conf/tomcat-users.xml":
                ensure => present,
                owner => 'oiuser',
                group => 'oiuser',
                mode => 0644,
                source => "puppet:///modules/oi3-tomee/tomcat-users.xml",
                require => Class["oi3-tomee::install"],
        }

        file {"/opt/openinfinity/3.0.0/tomcat/conf/logging.properties":
                ensure => present,
                owner => 'oiuser',
                group => 'oiuser',
                mode => 0644,
                source => "puppet:///modules/oi3-tomee/logging.properties",
                require => Class["oi3-tomee::install"],
        }

        file {"/opt/openinfinity/3.0.0/tomcat/conf/system.properties":
                ensure => present,
                owner => 'oiuser',
                group => 'oiuser',
                mode => 0644,
                source => "puppet:///modules/oi3-tomee/system.properties",
                require => Class["oi3-tomee::install"],
        }

        file {"/opt/openinfinity/3.0.0/tomcat/conf/tomee.xml":
                ensure => present,
                owner => 'oiuser',
                group => 'oiuser',
                mode => 0644,
                source => "puppet:///modules/oi3-tomee/tomee.xml",
                require => Class["oi3-tomee::install"],
        }

        file {"/opt/openinfinity/3.0.0/tomcat/conf/web.xml":
                ensure => present,
                owner => 'oiuser',
                group => 'oiuser',
                mode => 0644,
                source => "puppet:///modules/oi3-tomee/web.xml",
                require => Class["oi3-tomee::install"],
        }
}

class oi3-tomee::service inherits oi3-bas::service {
        service ["oi-tomcat"] {
                ensure => running,
                hasrestart => true,
                enable => true,
                require => Class["oi3-bas::config"],
        }
}

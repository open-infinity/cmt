class oi3-tomee::service {
        service { "oi-tomcat":
                ensure => running,
                hasrestart => true,
                enable => true,
                require => Class["oi3-tomee::config"],
        }
}


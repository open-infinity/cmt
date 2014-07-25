class oi3-bas::service inherits oi3variables {

    service {"oi-tomcat":
        ensure => running,
        hasrestart => true,
        enable => true,
        provider => $serviceProvider,
        require => Class["oi3-bas::config"],
    }
}

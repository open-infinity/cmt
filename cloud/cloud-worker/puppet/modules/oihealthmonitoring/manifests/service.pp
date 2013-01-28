class oihealthmonitoring::service {
    service { "collectd":
        ensure => running,
        hasrestart => true,
        enable => true,
        require => Class["oihealthmonitoring::config"],
    }    
    service { "oi-healthmonitoring":
        ensure => running,
        hasrestart => true,
        enable => true,
        require => Class["oihealthmonitoring::config"],
    }    
}


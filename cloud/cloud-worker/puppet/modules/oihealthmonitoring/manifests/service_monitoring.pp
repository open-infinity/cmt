class oihealthmonitoring::service_monitoring {
    service { "oi-healthmonitoring":
        ensure => running,
        hasrestart => true,
        enable => true,
        require => Class["oihealthmonitoring::config", "oihealthmonitoring::service_collectd"],
    }    
}


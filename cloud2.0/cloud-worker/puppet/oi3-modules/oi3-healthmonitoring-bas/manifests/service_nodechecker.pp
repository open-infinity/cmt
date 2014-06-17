class oi3-healthmonitoring-bas::service_nodechecker {
    service { "oi3-healthmonitoring":
        ensure => running,
        hasrestart => true,
        enable => true,
        require => Class["oi3-healthmonitoring-bas::config", "oi3-healthmonitoring-bas::service_collectd"],
    }    
}


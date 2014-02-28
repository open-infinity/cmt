class oi3-healthmonitoring-rdbms::service_nodechecker {
    service { "oi3-healthmonitoring":
        ensure => running,
        hasrestart => true,
        enable => true,
        require => Class["oi3-healthmonitoring-rdbms::config", "oi3-healthmonitoring-rdbms::service_collectd"],
    }    
}


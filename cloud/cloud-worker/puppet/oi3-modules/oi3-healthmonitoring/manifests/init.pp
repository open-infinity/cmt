class oi3-healthmonitoring {
    require oi3-basic
    include oi3-healthmonitoring::install
    include oi3-healthmonitoring::config
    include oi3-healthmonitoring::service_collectd
    include oi3-healthmonitoring::service_nodechecker
}


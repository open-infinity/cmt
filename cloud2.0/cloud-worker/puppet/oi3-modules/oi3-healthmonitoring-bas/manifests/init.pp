class oi3-healthmonitoring-bas {
    require oi3-basic
    include oi3-healthmonitoring-bas::install
    include oi3-healthmonitoring-bas::config
    include oi3-healthmonitoring-bas::service_collectd
    include oi3-healthmonitoring-bas::service_nodechecker
}


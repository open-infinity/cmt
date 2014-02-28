class oi3-healthmonitoring-rdbms {
    require oi3-basic
    include oi3-healthmonitoring-rdbms::install
    include oi3-healthmonitoring-rdbms::config
    include oi3-healthmonitoring-rdbms::service_collectd
    include oi3-healthmonitoring-rdbms::service_nodechecker
}


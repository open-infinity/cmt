class oihealthmonitoring {
    require oibasic
    include oihealthmonitoring::install
    include oihealthmonitoring::config
    include oihealthmonitoring::service_collectd
    include oihealthmonitoring::service_monitoring
}


class oi3-healthmonitoring-rdbms::service_collectd {
	service { "oi3-collectd":
		ensure => running,
		hasrestart => true,
		enable => true,
		require => Class["oi3-healthmonitoring-rdbms::config"],
	}    
}



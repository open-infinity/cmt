class oihealthmonitoring::service_collectd {
	service { "collectd":
		ensure => running,
		hasrestart => true,
		enable => true,
		require => Class["oihealthmonitoring::config"],
	}    
}



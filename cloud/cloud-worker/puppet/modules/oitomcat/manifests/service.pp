class oitomcat::service {
	service {"oi-tomcat":
		ensure => running,
		hasrestart => true,
		enable => true,
		require => Class["oitomcat::config"],
	}
}

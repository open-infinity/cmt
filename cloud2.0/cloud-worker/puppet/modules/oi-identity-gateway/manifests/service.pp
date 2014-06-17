class oi-identity-gateway::service {
	service {"oi-tomcat":
		ensure => running,
		hasrestart => true,
		enable => true,
		require => Class["oi-identity-gateway::config"],
	}

	service {"haproxy":
		ensure => running,
		enable => true,
		require => Package["haproxy"],
	}
}

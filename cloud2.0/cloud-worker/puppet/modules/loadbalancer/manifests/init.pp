class loadbalancer {
	package { "haproxy":
		ensure => installed
	}

	service { "haproxy":
		ensure => running,
		enable => true,
		require => Package["haproxy"],
	}

	file { "/etc/haproxy/haproxy.cfg":
		content => template("loadbalancer/haproxy.cfg.erb"),
		require => Package["haproxy"],
		notify => Service["haproxy"],
	}
}

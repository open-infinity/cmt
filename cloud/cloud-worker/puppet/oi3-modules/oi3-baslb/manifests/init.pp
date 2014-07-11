class oi3-baslb {
	$portal_addresses = parsejson($portal_addresses_array)

	package { "haproxy":
		ensure => installed
	}

	service { "haproxy":
		ensure => running,
		enable => true,
		require => Package["haproxy"],
	}

	file { "/etc/haproxy/haproxy.cfg":
		content => template("oi3-baslb/haproxy.cfg.erb"),
		require => Package["haproxy"],
		notify => Service["haproxy"],
	}
}

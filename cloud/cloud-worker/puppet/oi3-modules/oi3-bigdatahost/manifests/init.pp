class oi3-bigdatahost::config {
	file {"/etc/sysconfig/network":
		ensure => present,
		owner => 'root',
		group => 'root',
		mode => 0644,
		content => template("oi3-bigdatahost/network.erb"),
	}
}

class oi3-bigdatahost {
	include oi3-bigdatahost::config
}

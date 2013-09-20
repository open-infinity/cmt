class oibigdatahost::config {
	file {"/etc/sysconfig/network":
		ensure => present,
		owner => 'root',
		group => 'root',
		mode => 0644,
		content => template("oi3-bigdatahost/network.erb"),
	}
}

class oibigdatahost {
	include oibigdatahost::config
}

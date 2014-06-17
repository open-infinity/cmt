class toasbigdatahost::config {
	file {"/etc/sysconfig/network":
		ensure => present,
		owner => 'root',
		group => 'root',
		mode => 0644,
		content => template("toasbigdatahost/network.erb"),
	}
}

class toasbigdatahost {
	include toasbigdatahost::config
}

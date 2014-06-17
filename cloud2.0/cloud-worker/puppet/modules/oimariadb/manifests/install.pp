class oimariadb::install {
	file { "/etc/my.cnf":
                ensure => present,
                source => "puppet:///modules/oimariadb/my.cnf",
                owner => "root",
                group => "root",
		require => User["mysql"],
                notify => Class["oimariadb::service"],
        }	

	package { "oi-mariadb-5.2.8":
		ensure => present,
		require => file["/etc/my.cnf"],
	}

	user { "mysql":
		ensure => present,
		comment => "MariaDB user",
		gid => "mysql",
		shell => "/bin/false",
		require => Group["mysql"],
	}

	group { "mysql":
		ensure => present,
	}
}

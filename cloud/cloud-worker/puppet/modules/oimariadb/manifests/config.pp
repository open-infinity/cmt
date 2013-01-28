class oimariadb::config {

	file { "/etc/init.d/oi-mariadb":
		ensure => present,
		source => "puppet:///modules/oimariadb/oi-mariadb",
		owner => "root",
		group => "root",
		mode => 0755,
		require => Class["oimariadb::install"],
	}

	file { "/var/lib/mysql":
		ensure => directory,
		group => "mysql",
		owner => "mysql",
		require => File["/etc/my.cnf"],
	}
	
	file { "/var/run/mysqld":
		ensure => directory,
		group => "mysql",
		owner => "mysql",
		require => File["/etc/my.cnf"],
	}

	file { "/var/log/mariadb-slow.log":
		ensure => present,
		group => "mysql",
		owner => "mysql",
		require => File["/etc/my.cnf"],
	}
}

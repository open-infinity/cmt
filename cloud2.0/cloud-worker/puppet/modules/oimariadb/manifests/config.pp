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

	# Directory for database dumps
	file { "/opt/openinfinity/2.0.0/backup/dumps":
		ensure => directory,
		group => "root",
		owner => "root",
		require => File["/opt/openinfinity/2.0.0/backup"], # Created by oibackup module
	}

	# Database backup script
	file { "/opt/openinfinity/2.0.0/backup/scripts/pre-backup.d/mariadb_backup.sh":
		ensure => present,
	        content => template("oimariadb/mariadb_backup.sh.template"),
		owner => "root",
		group => "root",
		mode => 0755,
		require => File["/opt/openinfinity/2.0.0/backup/scripts/pre-backup.d"], # Created by oibackup module
	}
}


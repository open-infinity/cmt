class oi3-rdbms::config {
	file {"/opt/openinfinity/3.0.0/backup/before-node-backup.d/mariadb-before-backup":
		ensure => present,
		source => "puppet:///modules/oi3-rdbms/mariadb-before-backup",
		owner => "root",
		group => "root",
		mode => 0700,
		require => Class["oi3-rdbms::install"],
	}

	file {"/opt/openinfinity/3.0.0/backup/before-node-backup.d/mariadb-before-backup":
		ensure => present,
		source => "puppet:///modules/oi3-rdbms/mariadb-before-backup",
		owner => "root",
		group => "root",
		mode => 0700,
		require => Class["oi3-rdbms::install"],
	}
	
	file {"/opt/openinfinity/3.0.0/backup/before-node-backup.d/mariadb-before-restore":
		ensure => present,
		source => "puppet:///modules/oi3-rdbms/mariadb-before-restore",
		owner => "root",
		group => "root",
		mode => 0700,
		require => Class["oi3-rdbms::install"],
	}

	file {"/opt/openinfinity/3.0.0/backup/before-node-backup.d/mariadb-before-restore":
		ensure => present,
		source => "puppet:///modules/oi3-rdbms/mariadb-before-restore",
		owner => "root",
		group => "root",
		mode => 0700,
		require => Class["oi3-rdbms::install"],
	}
}


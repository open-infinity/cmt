class oi3-rdbms::backup {
	file {"/opt/openinfinity/3.0.0/backup/node-backup-before.d/mariadb-before-backup":
		ensure => present,
		source => "puppet:///modules/oi3-rdbms/mariadb-before-backup",
		owner => "root",
		group => "root",
		mode => 0700,
		require => Class["oi3-rdbms::install"],
	}

	file {"/opt/openinfinity/3.0.0/backup/node-backup-after.d/mariadb-after-backup":
		ensure => present,
		source => "puppet:///modules/oi3-rdbms/mariadb-after-backup",
		owner => "root",
		group => "root",
		mode => 0700,
		require => Class["oi3-rdbms::install"],
	}
	
	file {"/opt/openinfinity/3.0.0/backup/node-restore-before.d/mariadb-before-restore":
		ensure => present,
		source => "puppet:///modules/oi3-rdbms/mariadb-before-restore",
		owner => "root",
		group => "root",
		mode => 0700,
		require => Class["oi3-rdbms::install"],
	}

	file {"/opt/openinfinity/3.0.0/backup/node-restore-after.d/mariadb-after-restore":
		ensure => present,
		source => "puppet:///modules/oi3-rdbms/mariadb-after-restore",
		owner => "root",
		group => "root",
		mode => 0700,
		require => Class["oi3-rdbms::install"],
	}
}


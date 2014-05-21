class oi3-rdbms-backup::config {
	file {"/opt/openinfinity/3.1.0/backup/node-backup-before.d/mariadb-before-backup":
		ensure => present,
		source => "puppet:///modules/oi3-rdbms-backup/mariadb-before-backup",
		owner => "root",
		group => "root",
		mode => 0700,
		require => Class["oi3-backup::install"],
	}

	file {"/opt/openinfinity/3.1.0/backup/node-backup-after.d/mariadb-after-backup":
		ensure => present,
		source => "puppet:///modules/oi3-rdbms-backup/mariadb-after-backup",
		owner => "root",
		group => "root",
		mode => 0700,
		require => Class["oi3-backup::install"],
	}
	
	file {"/opt/openinfinity/3.1.0/backup/node-restore-before.d/mariadb-before-restore":
		ensure => present,
		source => "puppet:///modules/oi3-rdbms-backup/mariadb-before-restore",
		owner => "root",
		group => "root",
		mode => 0700,
		require => Class["oi3-backup::install"],
	}

	file {"/opt/openinfinity/3.1.0/backup/node-restore-after.d/mariadb-after-restore":
		ensure => present,
		source => "puppet:///modules/oi3-rdbms-backup/mariadb-after-restore",
		owner => "root",
		group => "root",
		mode => 0700,
		require => Class["oi3-backup::install"],
	}
}


class oi3mariadbgalera_backup::config {
	file {"/opt/openinfinity/3.0.0/backup/node-backup-before.d/galera-before-backup":
		ensure => present,
		source => "puppet:///modules/oi3mariadbgalera_backup/galera-before-backup",
		owner => "root",
		group => "root",
		mode => 0700,
		require => Class["oi3-backup::install"],
	}

	file {"/opt/openinfinity/3.0.0/backup/node-backup-after.d/galera-after-backup":
		ensure => present,
		source => "puppet:///modules/oi3mariadbgalera_backup/galera-after-backup",
		owner => "root",
		group => "root",
		mode => 0700,
		require => Class["oi3-backup::install"],
	}
	
	file {"/opt/openinfinity/3.0.0/backup/node-restore-before.d/galera-before-restore":
		ensure => present,
		source => "puppet:///modules/oi3mariadbgalera_backup/galera-before-restore",
		owner => "root",
		group => "root",
		mode => 0700,
		require => Class["oi3-backup::install"],
	}

	file {"/opt/openinfinity/3.0.0/backup/node-restore-after.d/galera-after-restore":
		ensure => present,
		source => "puppet:///modules/oi3mariadbgalera_backup/galera-after-restore",
		owner => "root",
		group => "root",
		mode => 0700,
		require => Class["oi3-backup::install"],
	}
}


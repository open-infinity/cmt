class oi3rdbms_dump::config {

	# Backup directory
	file { "/opt/openinfinity/3.1.0/backup":
		ensure => directory,
	}

	# Backup directory
	file { "/opt/openinfinity/3.1.0/backup/dump":
		ensure => directory,
		require => File["/opt/openinfinity/3.1.0/backup"],
	}

	# MariaDB database dump script
	file { "/opt/openinfinity/3.1.0/backup/mariadb-dump":
		ensure => present,
		source => "puppet:///modules/oi3rdbms_dump/mariadb-dump",
		owner => "root",
		group => "root",
		mode => 0755,
		require => File["/opt/openinfinity/3.1.0/backup"],
	}
	
	# Daily backup cron row
	file { "/etc/cron.d/oi-daily-backup":
		ensure => present,
		content => template("oi3rdbms_dump/cron_d_oi-daily-backup.erb"),
		owner => "root",
		group => "root",
		mode => 0644,
	}
}


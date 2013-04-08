class oibackup::config {

	# Backup directory
	file { "/opt/openinfinity/2.0.0/backup":
		ensure => directory,
		group => "toas",
		owner => "toas",
		require => Class["oibackup::install"],
	}

	# SSH-key directory
	file { "/opt/openinfinity/2.0.0/backup/ssh-keys":
		ensure => directory,
		group => "toas",
		owner => "toas",
		require => File["/opt/openinfinity/2.0.0/backup"],
	}

	# SSH-key for backup
	file { "/opt/openinfinity/2.0.0/backup/ssh-keys/toas-backup-private.key":
		ensure => present,
                source => "puppet:///modules/oibackup/toas-backup-private.key",
		owner => "root",
		group => "root",
		mode => 0600,
		require => File["/opt/openinfinity/2.0.0/backup/scripts"],
	}
	file { "/opt/openinfinity/2.0.0/backup/ssh-keys/toas-backup-public.key":
		ensure => present,
                source => "puppet:///modules/oibackup/toas-backup-public.key",
		owner => "root",
		group => "root",
		mode => 0644,
		require => File["/opt/openinfinity/2.0.0/backup/scripts"],
	}

	
	# Scripts directory
	file { "/opt/openinfinity/2.0.0/backup/scripts":
		ensure => directory,
		group => "toas",
		owner => "toas",
		require => Class["oibackup::install"],
	}

	# Directory for scripts to be run before the actual backup
	file { "/opt/openinfinity/2.0.0/backup/scripts/pre-backup.d":
		ensure => directory,
		group => "toas",
		owner => "toas",
		require => Class["oibackup::install"],
	}

	# Rsync backup script
	file { "/opt/openinfinity/2.0.0/backup/scripts/rsync-backup.sh":
		ensure => present,
	        content => template("oibackup/rsync-backup.sh.template"),
		owner => "root",
		group => "root",
		mode => 0755,
		require => File["/opt/openinfinity/2.0.0/backup/scripts"],
	}
	
	# Backup cron file
	file { "/opt/openinfinity/2.0.0/backup/scripts/oi-daily-backup.sh":
		ensure => present,
	        content => template("oibackup/oi-daily-backup.sh.template"),
		owner => "root",
		group => "root",
		mode => 0755,
		require => Class["oibackup::install"],
	}
	
	# Daily backup cron row
	file { "/etc/cron.d/oi-daily-backup":
		ensure => present,
	        content => template("oibackup/cron_d_oi-daily-backup.template"),
		owner => "root",
		group => "root",
		mode => 0644,
		require => Class["oibackup::install"],
	}
}


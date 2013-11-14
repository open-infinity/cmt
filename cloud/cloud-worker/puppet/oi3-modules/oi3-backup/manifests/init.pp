class oi3-backup::config {

    # Directories
    $backup_directories = [
        "/opt/openinfinity/3.0.0/backup",
        "/opt/openinfinity/3.0.0/backup/before-backup.d",
        "/opt/openinfinity/3.0.0/backup/after-backup.d",
        "/opt/openinfinity/3.0.0/backup/before-restore.d",
        "/opt/openinfinity/3.0.0/backup/after-restore.d",
        "/opt/openinfinity/3.0.0/backup/exclude-rules.d",
        "/opt/openinfinity/3.0.0/backup/include-dirs.d",
    ]
    file { $backup_directories:
        ensure => "directory",
		owner => 'oiuser',
		group => 'oiuser',
		mode => 0755,
    }

    # Stream backup
	file {"/opt/openinfinity/3.0.0/backup/stream-backup":
		ensure => present,
		owner => 'oiuser',
		group => 'oiuser',
		mode => 0755,
		source => "puppet:///modules/oi3-backup/stream-backup",
		require => File['/opt/openinfinity/3.0.0/backup'],
	}

    # Stream restore
	file {"/opt/openinfinity/3.0.0/backup/stream-restore":
		ensure => present,
		owner => 'oiuser',
		group => 'oiuser',
		mode => 0755,
		source => "puppet:///modules/oi3-backup/stream-restore",
		require => File['/opt/openinfinity/3.0.0/backup'],
	}
	
}


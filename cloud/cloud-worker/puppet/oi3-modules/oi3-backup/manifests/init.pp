class oi3-backup {
	include oi3-backup::install
	include oi3-backup::config
}

class oi3-backup::install {
    # LZMA compression utilities rpm needed by tar
	package { "xz":
		ensure => present,
	}
}

class oi3-backup::config {

    # Directories
    $backup_directories = [
        "/opt/openinfinity/3.1.0/backup",

        "/opt/openinfinity/3.1.0/backup/common",

        "/opt/openinfinity/3.1.0/backup/cluster-backup-before.d",
        "/opt/openinfinity/3.1.0/backup/cluster-backup-after.d",
        
        "/opt/openinfinity/3.1.0/backup/node-backup-before.d",
        "/opt/openinfinity/3.1.0/backup/node-backup-after.d",

        "/opt/openinfinity/3.1.0/backup/cluster-restore-before.d",
        "/opt/openinfinity/3.1.0/backup/cluster-restore-after.d",
        
        "/opt/openinfinity/3.1.0/backup/node-restore-before.d",
        "/opt/openinfinity/3.1.0/backup/node-restore-after.d",
        
        "/opt/openinfinity/3.1.0/backup/exclude-rules.d",
        "/opt/openinfinity/3.1.0/backup/include-dirs.d",
    ]
    file { $backup_directories:
        ensure => "directory",
		owner => 'oiuser',
		group => 'oiuser',
		mode => 0755,
    }

    # Temporary directory for backups
	file {"/opt/openinfinity/3.1.0/backup/tmp":
        ensure => "directory",
		owner => 'oiuser',
		group => 'oiuser',
		mode => 0700,
		require => File['/opt/openinfinity/3.1.0/backup'],
	}

    # Stream backup
	file {"/opt/openinfinity/3.1.0/backup/stream-backup":
		ensure => present,
		owner => 'oiuser',
		group => 'oiuser',
		mode => 0755,
		source => "puppet:///modules/oi3-backup/stream-backup",
		require => File['/opt/openinfinity/3.1.0/backup'],
	}

    # Stream restore
	file {"/opt/openinfinity/3.1.0/backup/stream-restore":
		ensure => present,
		owner => 'oiuser',
		group => 'oiuser',
		mode => 0755,
		source => "puppet:///modules/oi3-backup/stream-restore",
		require => File['/opt/openinfinity/3.1.0/backup'],
	}
	
    # Cluster sync
	file {"/opt/openinfinity/3.1.0/backup/cluster-sync":
		ensure => present,
		owner => 'oiuser',
		group => 'oiuser',
		mode => 0755,
		source => "puppet:///modules/oi3-backup/cluster-sync",
		require => File['/opt/openinfinity/3.1.0/backup'],
	}
	
    # README
	file {"/opt/openinfinity/3.1.0/backup/README":
		ensure => present,
		owner => 'oiuser',
		group => 'oiuser',
		mode => 0444,
		source => "puppet:///modules/oi3-backup/README",
		require => File['/opt/openinfinity/3.1.0/backup'],
	}
	
    # More sensitive version of CentOS's run-parts script
	file {"/opt/openinfinity/3.1.0/backup/common/run-parts-e":
		ensure => present,
		owner => 'oiuser',
		group => 'oiuser',
		mode => 0777,
		source => "puppet:///modules/oi3-backup/run-parts-e",
		require => File['/opt/openinfinity/3.1.0/backup/common'],
	}
	
}


class oi3apacheds_dump::config {

    # Directories to be created
    $backup_directories = [
        "/opt/openinfinity/3.1.0/backup",
        "/opt/openinfinity/3.1.0/backup/dumps",
        "/opt/openinfinity/3.1.0/backup/scripts",
    ]
    file { $backup_directories:
        ensure => "directory",
		owner => 'oiuser',
		group => 'oiuser',
		mode => 0755,
    }

    # Dump script
	file { "/opt/openinfinity/3.1.0/backup/scripts/apacheds-dump.sh":
        ensure => present,
        owner => 'oiuser',
        group => 'oiuser',
        mode => 0755,
		source => "puppet:///modules/oi3apacheds_dump/apacheds-dump.sh",
		require => File['/opt/openinfinity/3.1.0/backup/scripts'],
    }

    # Cron file
	file {"/etc/cron.d/apacheds-dump":
		ensure => present,
		owner => 'root',
		group => 'root',
		mode => 0644,
        content => template("oi3apacheds_dump/apacheds-dump.erb"),
	}
	
}


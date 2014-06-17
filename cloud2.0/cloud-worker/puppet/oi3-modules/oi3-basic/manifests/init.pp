class oi3-basic::config {
	require oi3-ebs
        file {"/etc/logrotate.d/oi-tomcat":
		ensure => present,
		source => "puppet:///modules/oi3-basic/oi-tomcat.logrotate",
		owner => "root",
		group => "root",
		mode => 0644,
	}

	file {"/opt/data":
                ensure => directory,
                owner => 'root',
                group => 'root',
                mode => 777,
        }

        file {"/data":
                ensure => link,
                target => '/opt/data',
		force => true,
                require => File["/opt/data"],
        }

	file {"/opt/openinfinity":
		ensure => directory,
		owner => 'oiuser',
		group => 'oiuser',
		mode => 644,
		require => User["oiuser"],
	}

	file {"/opt/openinfinity/3.1.0":
		ensure => directory,
		owner => 'oiuser',
		group => 'oiuser',
		mode => 644,
		require => File["/opt/openinfinity"],
	}

	file {"/home/oiuser":
		ensure => directory,
		owner => 'oiuser',
		group => 'oiuser',
		mode => 750,
		require => [user['oiuser'],group['oiuser']]
	}

	user { "oiuser":
                ensure => present,
                comment => "Open Infinity user",
                gid => "oiuser",
                shell => "/bin/bash",
		managehome => true,
                require => Group["oiuser"],
        }

        group {"oiuser":
                ensure => present,
        }
}

class oi3-basic::service {
	service { "crond":
		ensure => running,
		hasstatus => true,
		hasrestart => true,
		enable => true,
		require => Class["oi3-basic::install"],
	}
}

class oi3-basic::install {
    package { ["cronie"]:
        ensure => installed,
	require => Class["oi3-basic::config"],
    }
}

class oi3-basic {
	require oi3-ebs
        include  oi3-basic::install, oi3-basic::config,  oi3-basic::service
}
	


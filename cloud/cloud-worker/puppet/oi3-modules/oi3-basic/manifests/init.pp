class oi3-basic::config {
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

	file {"/opt/openinfinity/3.0.0":
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
        include  oi3-basic::install, oi3-basic::config,  oi3-basic::service
}
	


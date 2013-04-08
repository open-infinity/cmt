class oibasic::config {
        file {"/opt/data":
                ensure => directory,
                owner => 'root',
                group => 'root',
                mode => 777,
#		require => Class["oiebs"],
        }

        file {"/data":
                ensure => link,
                target => '/opt/data',
		force => true,
                require => File["/opt/data"],
        }

	file {"/opt/openinfinity":
		ensure => directory,
		owner => 'toas',
		group => 'toas',
		mode => 644,
		require => User["toas"],
	}

	file {"/opt/openinfinity/2.0.0":
		ensure => directory,
		owner => 'toas',
		group => 'toas',
		mode => 644,
		require => File["/opt/openinfinity"],
	}

	file {"/home/toas":
		ensure => directory,
		owner => 'toas',
		group => 'toas',
		mode => 750,
		require => [user['toas'],group['toas']]
	}

	user { "toas":
                ensure => present,
                comment => "TOAS user",
                gid => "toas",
                shell => "/bin/bash",
		managehome => true,
                require => Group["toas"],
        }

        group {"toas":
                ensure => present,
#		require => Class["oiebs"],
        }
}

class oibasic::service {
	service { "crond":
		ensure => running,
		hasstatus => true,
		hasrestart => true,
		enable => true,
		require => Class["oibasic::install"],
	}
}

class oibasic::install {
    package { ["cronie"]:
        ensure => installed,
	require => Class["oibasic::config"],
    }
}

class oibasic {
        include  oibasic::install, oibasic::config,  oibasic::service
}


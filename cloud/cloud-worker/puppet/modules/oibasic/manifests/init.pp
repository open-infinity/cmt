class oibasic::config {
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
        }

}

class oibasic {
        include oibasic::config
}


class oihealthmonitoring::install {
# Cronie is installed by oibackup
#    package { ["cronie"]:
#        ensure => installed,
#    }
    package { ["balance"]:
        ensure => installed,
	require => Class["oibasic"]
    }
    package { ["nodechecker"]:
        ensure => installed,
	require => Class["oibasic"]
    }
    package { ["collectd"]:
        ensure => installed,
        require => Class["oibasic"]
    }
}


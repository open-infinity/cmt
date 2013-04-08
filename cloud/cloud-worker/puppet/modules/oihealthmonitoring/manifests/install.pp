class oihealthmonitoring::install {
# Cronie is installed by oibackup
#    package { ["cronie"]:
#        ensure => installed,
#    }
    package { ["collectd"]:
        ensure => "5.0.1-0",
	require => Class["oibasic"]
    }
    package { ["balance"]:
        ensure => installed,
	require => Class["oibasic"]
    }
    package { ["oi-healthmonitoring"]:
        ensure => installed,
	require => Class["oibasic"]
    }
}


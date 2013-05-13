class oihealthmonitoring::install {
    package { ["Pound"]:
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
    package { ["rrd-http-server"]:
        ensure => installed,
        require => Class["oibasic"]
    }

}


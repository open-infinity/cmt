class oi3-healthmonitoring-bas::install {
    package { ["Pound"]:
        ensure => installed,
	require => Class["oi3-basic"]
    }
    package { ["oi3-nodechecker"]:
        ensure => installed,
	require => Class["oi3-basic"]
    }
    package { ["oi3-collectd"]:
        ensure => installed,
        require => Class["oi3-basic"]
    }
    package { ["oi3-rrd-http-server"]:
        ensure => installed,
        require => Class["oi3-basic"]
    }
}


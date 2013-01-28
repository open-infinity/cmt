class oihealthmonitoring::install {
    package { ["collectd"]:
        ensure => "5.0.1-0",
    }
    package { ["oi-healthmonitoring"]:
        ensure => installed,
    }
}


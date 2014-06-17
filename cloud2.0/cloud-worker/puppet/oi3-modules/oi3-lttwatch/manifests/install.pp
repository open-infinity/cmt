class oi3-lttwatch::install {
    package { ["oi3-lttwatch"]:
        ensure => installed,
        require => Class["oi3-lttwatch::preconfig"],
    }
}


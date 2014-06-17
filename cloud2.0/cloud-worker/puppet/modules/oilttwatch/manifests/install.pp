class oilttwatch::install {
    package { ["oi-ltt-watch"]:
        ensure => installed,
        require => Class["oilttwatch::preconfig"],
    }
}


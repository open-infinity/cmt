class oi3-tomee::install {
    package { ["java-1.7.0-openjdk", "oi3-tomeeplus" ]:
        ensure => present,
        require => Class["oi3-basic"],
    }
}


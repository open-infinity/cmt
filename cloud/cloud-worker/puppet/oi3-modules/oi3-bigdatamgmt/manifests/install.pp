class oi3-bigdatamgmt::install {
    package { "oi3-bigdata-mgmt":
        ensure => present,
        require => Class["oi3-basic"]
    }
}


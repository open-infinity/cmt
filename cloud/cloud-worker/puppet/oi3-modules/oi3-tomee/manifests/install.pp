class oi3-tomee::install inherits oi3variables {

    package { [ $javaPackageName, "oi3-tomeeplus" ]:
        ensure => present,
        require => Class["oi3-basic"],
    }
}


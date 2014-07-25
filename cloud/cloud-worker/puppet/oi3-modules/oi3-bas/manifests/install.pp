class oi3-bas::install inherits oi3variables {

    package { $javaPackageName:
        ensure => present,
    }

    package { "oi3-bas":
        ensure => present,
        require => Class["oi3-basic"],
    }
}

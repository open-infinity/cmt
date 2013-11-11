class oi3-tomee {
    package { "oi3-tomee":
         ensure => present,
	require => Class["oi3-bas"]
    }
}


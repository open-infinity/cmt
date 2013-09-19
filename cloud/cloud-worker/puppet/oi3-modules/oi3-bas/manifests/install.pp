class oi3-bas::install {
	package { ["oi3-bas"]:
		ensure => present,
		require => Class["oi3-basic"]
	}
}

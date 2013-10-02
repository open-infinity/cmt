class oi3-serviceplatform::install {
	package { ["oi3-serviceplatform" ]:
		ensure => present,
		require => Class["oi3-bas"],
	}

}

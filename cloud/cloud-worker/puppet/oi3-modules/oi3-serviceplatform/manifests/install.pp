class oi3-serviceplatform::install {
	package { ["java-1.7.0-openjdk", "oi3-serviceplatform", "oi3-bas"]:
		ensure => present,
		require => Class["oi3-basic"],
	}

}

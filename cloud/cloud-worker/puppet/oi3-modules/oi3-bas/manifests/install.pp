class oi3-bas::install {
	package { ["java-1.6.0-openjdk","oi3-bas"]:
		ensure => present,
		require => Class["oi3-basic"]
	}
}

class oitomcat::install {
	package { ["java-1.6.0-openjdk", "oi-tomcat-7.0.27-1", "oi-connectorj-5.1.14-1" ]:
		ensure => present,
		require => Class["oibasic"]
	}

	package { ["oi-core-2.0-1"]:
		ensure => present,
		require => Package["oi-tomcat-7.0.27-1"],
	}
}

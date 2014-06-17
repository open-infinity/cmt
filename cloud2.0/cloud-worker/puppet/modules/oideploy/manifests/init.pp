class oideploy {
	file {"/opt/toas/tomcat/webapps/$pkgname":
		ensure => present,
		owner => 'toas',
		group => 'toas',
		mode => 0644,
		source => "puppet:///openinfinity/$pkgname",
		require => Class["toasbas::install"],
	}
	
}

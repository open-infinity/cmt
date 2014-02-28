class oi3idp {
	package { ["java-1.7.0-openjdk", "java-1.7.0-openjdk-devel"]:
		ensure => present
	}

	package { "oi3-idp":
		ensure => present,
	}

	file { "example-confs":
		path => "/usr/local/src/example-confs.tar.gz",
		source => "puppet:///modules/oi3idp/example-confs.tar.gz",
		owner => "root",
		group => "root",
		mode => 0644,
	} ->
	exec { "example_confs_untar":
		command => "tar zxf /usr/local/src/example-confs.tar.gz",
		cwd => "/root",
		creates => "/root/idp-example-confs",
		path => ["/bin",],
	}
}

class oilttwatch::preconfig {

	# Make config dir
	file { "/opt/openinfinity/2.0.0/ltt-watch":
		ensure => directory, owner => "toas", group => "toas", mode => 0774,
	}

	# LTT configuration file
	file { "/opt/openinfinity/2.0.0/ltt-watch/ltt.xml":
		ensure => file,
		owner => 'toas',
		group => 'toas',
		mode => 0755,
		content => template("oilttwatch/ltt.xml.template"),
		require => File["/opt/openinfinity/2.0.0/ltt-watch"],
	} 
}


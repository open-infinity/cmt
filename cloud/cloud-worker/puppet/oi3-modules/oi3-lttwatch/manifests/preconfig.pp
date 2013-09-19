class oi3-lttwatch::preconfig {

	# Make config dir
	file { "/opt/openinfinity/3.0.0/ltt-watch":
		ensure => directory, owner => "oiuser", group => "oiuser", mode => 0774,
	}

	# LTT configuration file
	file { "/opt/openinfinity/3.0.0/ltt-watch/ltt.xml":
		ensure => file,
		owner => 'oiuser',
		group => 'oiuser',
		mode => 0755,
		content => template("oi3-lttwatch/ltt.xml.template"),
		require => File["/opt/openinfinity/3.0.0/ltt-watch"],
	} 
}


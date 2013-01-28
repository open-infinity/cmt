class oitomcat::config {
	file {"/opt/openinfinity/2.0.0/tomcat/conf/catalina.properties":
		ensure => present,
		owner => 'toas',
		group => 'toas',
		mode => 0600,
		source => "puppet:///modules/oitomcat/catalina.properties",
		require => Class["oitomcat::install"],
	}

	file {"/opt/openinfinity/2.0.0/tomcat/conf/server.xml":
		ensure => present,
		owner => 'toas',
		group => 'toas',
		mode => 0600,
		source => "puppet:///modules/oitomcat/server_7.0.27.xml",
		require => Class["oitomcat::install"],
	}

	file {"/opt/openinfinity/2.0.0/tomcat/conf/context.xml":
		ensure => present,
		owner => 'toas',
		group => 'toas',
		mode => 0600,
		source => "puppet:///modules/oitomcat/context.xml",
		require => Class["oitomcat::install"],
	}

	file {"/etc/init.d/oi-tomcat":
                ensure => present,
                owner => 'root',
                group => 'root',
                mode => 0755,
                source => "puppet:///modules/oitomcat/oi-tomcat",
                require => Class["oitomcat::install"],
        }
}

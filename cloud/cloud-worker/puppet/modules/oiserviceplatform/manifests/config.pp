class oiserviceplatform::config inherits oibas::config {
	file {"/opt/openinfinity/2.0.0/tomcat/bin/setenv.sh":
		ensure => present,
		owner => 'toas',
		group => 'toas',
		mode => 0755,
		source => "puppet:///modules/oiserviceplatform/setenv.sh",
		require => Class["oiserviceplatform::install"],
	}
	
	file {"/opt/openinfinity/2.0.0/tomcat/conf/activemq.xml":
		ensure => present,
		owner => 'toas',
		group => 'toas',
		mode => 0644,
		content => template("oiserviceplatform/activemq.xml.erb"),
		require => Class["oiserviceplatform::install"],
	}

	File["/opt/openinfinity/2.0.0/tomcat/conf/catalina.properties"] {source => "puppet:///modules/oiserviceplatform/catalina.properties"}

	file {"/opt/openinfinity/2.0.0/tomcat/.mule":
		ensure => directory,
		owner => 'toas',
		group => 'toas',
		mode => 0644,
		require => Class["oiserviceplatform::install"],
	}
}

class oi3-serviceplatform::config inherits oi3-bas::config {
	file {"/opt/openinfinity/2.0.0/tomcat/conf/activemq.xml":
		ensure => present,
		owner => 'oiuser',
		group => 'oiuser',
		mode => 0644,
		content => template("oi3-serviceplatform/activemq.xml.erb"),
		require => Class["oi3-serviceplatform::install"],
	}

	File["/opt/openinfinity/3.0.0/tomcat/conf/catalina.properties"] {source => "puppet:///modules/oi3-serviceplatform/catalina.properties"}

	file {"/opt/openinfinity/3.0.0/tomcat/.mule":
		ensure => directory,
		owner => 'oiuser',
		group => 'oiuser',
		mode => 0644,
		require => Class["oi3-serviceplatform::install"],
	}

	# acitivity webapp configuratio override

	file {"/opt/openinfinity/3.0.0/tomcat/webapps/activiti-explorer2/WEB-INF/classes/db.properties":
		ensure => present,
		owner => 'oiuser',
		group => 'oiuser',
		mode => 0644,
		content => template("oiserviceplatform/db.properties.erb"),
		require => Class["oi3-serviceplatform::install"],
	}

	file {"/opt/openinfinity/3.0.0/tomcat/webapps/activiti-rest2/WEB-INF/classes/db.properties":
		ensure => present,
		owner => 'oiuser',
		group => 'oiuser',
		mode => 0644,
		content => template("oi3-serviceplatform/db.properties.erb"),
		require => Class["oiserviceplatform::install"],
	}


}

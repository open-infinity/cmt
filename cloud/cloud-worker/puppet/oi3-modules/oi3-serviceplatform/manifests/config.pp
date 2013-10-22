class oi3-serviceplatform::config inherits oi3-bas::config {
	#new, not tested. may not be required if bas version used
	file {"/opt/openinfinity/3.0.0/tomcat/bin/setenv.sh":
		ensure => present,
		owner => 'oiuser',
		group => 'oiuser',
		mode => 0644,
		content => template("oi3-serviceplatform/setenv.sh.erb"),
		require => Class["oi3-serviceplatform::install"],
	}

	#new, not tested
	file {"/opt/openinfinity/3.0.0/tomcat/conf/catalina.properties":
		ensure => present,
		owner => 'oiuser',
		group => 'oiuser',
		mode => 0644,
		source => "puppet:///modules/oi3-serviceplatform/catalina.properties",		
		require => Class["oi3-serviceplatform::install"],
	}


	#rights may require change
	file {"/opt/openinfinity/3.0.0/tomcat/conf/activemq.xml":
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
		content => template("oi3-serviceplatform/db.properties.erb"),
		require => Class["oi3-serviceplatform::install"],
	}

	file {"/opt/openinfinity/3.0.0/tomcat/webapps/activiti-rest2/WEB-INF/classes/db.properties":
		ensure => present,
		owner => 'oiuser',
		group => 'oiuser',
		mode => 0644,
		content => template("oi3-serviceplatform/db.properties.erb"),
		require => Class["oi3-serviceplatform::install"],
	}
	
	file {"/opt/openinfinity/3.0.0/tomcat/webapps/activemq-web-console/WEB-INF/webconsole-embedded.xml":
		ensure => present,
		owner => 'oiuser',
		group => 'oiuser',
		mode => 0644,
		source => "puppet:///modules/oi3-serviceplatform/webconsole-embedded.xml",		
		require => Class["oi3-serviceplatform::install"],
	}
	
	# new not tested
	# Http basic authentication enabling for activemq webconsole. Note tomcat_users
	file {"/opt/openinfinity/3.0.0/tomcat/webapps/activemq-web-console/WEB-INF/web.xml":
		ensure => present,
		owner => 'oiuser',
		group => 'oiuser',
		mode => 0644,
		content => template("oi3-serviceplatform/amqwebconsole_web.xml.erb"),
		require => Class["oi3-serviceplatform::install"],
	}


}

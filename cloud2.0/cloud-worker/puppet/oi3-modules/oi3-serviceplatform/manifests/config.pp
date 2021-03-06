class oi3-serviceplatform::config inherits oi3-bas::config {
#    file { "/opt/openinfinity/3.1.0/tomcat/bin/setenv.sh":
#                ensure => present,
#                owner => 'oiuser',
#                group => 'oiuser',
#                mode => 0644,
#                content => template("oi3-serviceplatform/setenv.sh.erb"),
#                require => Class["oi3-serviceplatform::install"],
#    }
#
#    file {"/opt/openinfinity/3.1.0/tomcat/conf/catalina.properties":
#                ensure => present,
#                owner => 'oiuser',
#                group => 'oiuser',
#                mode => 0644,
#                source => "puppet:///modules/oi3-serviceplatform/catalina.properties",
#                require => Class["oi3-serviceplatform::install"],
#    }

	file {"/opt/openinfinity/3.1.0/tomcat/conf/tomcat-users.xml":
		ensure => present,
		owner => 'oiuser',
		group => 'oiuser',
		mode => 0644,
		content => template("oi3-serviceplatform/tomcat-users.xml.erb"),
		require => Class["oi3-serviceplatform::install"],
	}


	#rights may require change
	file {"/opt/openinfinity/3.1.0/tomcat/conf/activemq.xml":
		ensure => present,
		owner => 'oiuser',
		group => 'oiuser',
		mode => 0644,
		content => template("oi3-serviceplatform/activemq.xml.erb"),
		require => Class["oi3-serviceplatform::install"],
	}

	file {"/opt/data/.mule":
		ensure => directory,
		owner => 'oiuser',
		group => 'oiuser',
		mode => 0644,
		require => Class["oi3-serviceplatform::install"],
	}

	# activity webapp configuration override

	file {"/opt/openinfinity/3.1.0/tomcat/webapps/activiti-explorer2/WEB-INF/classes/db.properties":
		ensure => present,
		owner => 'oiuser',
		group => 'oiuser',
		mode => 0644,
		content => template("oi3-serviceplatform/activiti.db.properties.erb"),
		require => Class["oi3-serviceplatform::install"],
	}

	file {"/opt/openinfinity/3.1.0/tomcat/webapps/activiti-rest2/WEB-INF/classes/db.properties":
		ensure => present,
		owner => 'oiuser',
		group => 'oiuser',
		mode => 0644,
		content => template("oi3-serviceplatform/activiti.db.properties.erb"),
		require => Class["oi3-serviceplatform::install"],
	}
	
	file {"/opt/openinfinity/3.1.0/tomcat/webapps/activiti-explorer2/WEB-INF/activiti-standalone-context.xml":
		ensure => present,
		owner => 'oiuser',
		group => 'oiuser',
		mode => 0644,
		source => "puppet:///modules/oi3-serviceplatform/activiti-explorer-standalone-context.xml",		
		require => Class["oi3-serviceplatform::install"],
	}
	
	file {"/opt/openinfinity/3.1.0/tomcat/webapps/activiti-rest2/WEB-INF/classes/activiti-context.xml":
		ensure => present,
		owner => 'oiuser',
		group => 'oiuser',
		mode => 0644,
		source => "puppet:///modules/oi3-serviceplatform/activiti-rest-context.xml",		
		require => Class["oi3-serviceplatform::install"],
	}
	
	file {"/opt/openinfinity/3.1.0/tomcat/webapps/activiti-rest2/WEB-INF/web.xml":
		ensure => present,
		owner => 'oiuser',
		group => 'oiuser',
		mode => 0644,
		source => "puppet:///modules/oi3-serviceplatform/activiti-rest-web.xml",		
		require => Class["oi3-serviceplatform::install"],
	}

	# activemq-web-console webapp configuration override

	file {"/opt/openinfinity/3.1.0/tomcat/webapps/activemq-web-console/WEB-INF/webconsole-embedded.xml":
		ensure => present,
		owner => 'oiuser',
		group => 'oiuser',
		mode => 0644,
		source => "puppet:///modules/oi3-serviceplatform/webconsole-embedded.xml",		
		require => Class["oi3-serviceplatform::install"],
	}

	file {"/opt/openinfinity/3.1.0/tomcat/webapps/activemq-web-console/WEB-INF/web.xml":
		ensure => present,
		owner => 'oiuser',
		group => 'oiuser',
		mode => 0644,
		source => "puppet:///modules/oi3-serviceplatform/amqwebconsole_web.xml",		
		require => Class["oi3-serviceplatform::install"],
	}

	# oauth webapp configuration override
	file {"/opt/openinfinity/3.1.0/tomcat/webapps/oauth/WEB-INF/classes/oauth-repository.properties":
		ensure => present,
		owner => 'oiuser',
		group => 'oiuser',
		mode => 0644,
		content => template("oi3-serviceplatform/oauth-repository.properties.erb"),
		require => Class["oi3-serviceplatform::install"],
	}

#    # ActiveMQ web console credentials
#	file {"/opt/openinfinity/3.1.0/tomcat/conf/credentials.properties":
#		ensure => present,
#		owner => 'oiuser',
#		group => 'oiuser',
#		mode => 0600,
#		content => template("oi3-serviceplatform/credentials.properties.erb"),
#		require => Class["oi3-serviceplatform::install"],
#	}

#   # ActiveMQ SiteMesh dependency file
#	file {"/opt/openinfinity/3.1.0/tomcat/webapps/activemq-web-console/WEB-INF/decorators.xml":
#		ensure => present,
#		owner => 'oiuser',
#		group => 'oiuser',
#		mode => 0600,
#		content => template("oi3-serviceplatform/decorators.xml.erb"),
#		require => Class["oi3-serviceplatform::install"],
#	}


}

class oi-identity-gateway::config {
	file {"/opt/openinfinity/2.0.0/sso-tools/configuratortools/sampleconfiguration":
		ensure => present,
		owner => 'toas',
		group => 'toas',
		mode => 0644,
		content => template("oi-identity-gateway/sso_sampleconfiguration"),
		require => Class["oi-identity-gateway::install"],
	}

	file {"/opt/openinfinity/2.0.0/tomcat/webapps/openam/WEB-INF/classes/addToIDRepositoryService.properties":
		ensure => present,
		owner => 'toas',
		group => 'toas',
		mode => 0644,
		source => "puppet:///modules/oi-identity-gateway/addToIDRepositoryService.properties.tmp1",
		require => File["/opt/openinfinity/2.0.0/sso-tools/configuratortools/sampleconfiguration"],
	}

	file {"/opt/openinfinity/2.0.0/sso-tools/admintools/resources/addToIDRepositoryService.properties":
		ensure => present,
		owner => 'toas',
		group => 'toas',
		mode => 0644,
		source => "puppet:///modules/oi-identity-gateway/addToIDRepositoryService.properties.tmp2",
		require => File ["/opt/openinfinity/2.0.0/tomcat/webapps/openam/WEB-INF/classes/addToIDRepositoryService.properties"],
	}

        file {"/etc/init.d/oi-tomcat":
                ensure => present,
                owner => 'root',
                group => 'root',
                mode => 0755,
                source => "puppet:///modules/oi-identity-gateway/oi-tomcat",
                require => Class["oi-identity-gateway::install"],
        }

	file {"/opt/openinfinity/2.0.0/sso-tools/generate-configs.sh":
		ensure => present,
		owner => 'toas',
		group => 'toas',
		mode => 0755,
		source => "puppet:///modules/oi-identity-gateway/generate-configs.sh",
		require => File ["/opt/openinfinity/2.0.0/sso-tools/admintools/resources/addToIDRepositoryService.properties"],
	}

	file {"/opt/openinfinity/2.0.0/sso-tools/sp1.xml":
                ensure => present,
                owner => 'toas',
                group => 'toas',
                mode => 0644,
                content => template("oi-identity-gateway/sp1.xml.ssoidentityprovider.template"),
                require => Class["oi-identity-gateway::install"],
        }

	file {"/opt/openinfinity/2.0.0/sso-tools/sp1-x.xml":
                ensure => present,
                owner => 'toas',
                group => 'toas',
                mode => 0644,
                content => template("oi-identity-gateway/sp1-x.xml.ssoidentityprovider.template"),
                require => Class["oi-identity-gateway::install"],
        }

	file {"/opt/openinfinity/2.0.0/sso-tools/idp1.xml":
		ensure => present,
		owner => 'toas',
		group => 'toas',
		mode => 0644,
		content => template("oi-identity-gateway/idp1.xml.ssoidentityprovider.template"),
		require => Class["oi-identity-gateway::install"],
	}

	file {"/opt/openinfinity/2.0.0/sso-tools/idp1-x.xml":
                ensure => present,
                owner => 'toas',
                group => 'toas',
                mode => 0644,
                content => template("oi-identity-gateway/idp1-x.xml.ssoidentityprovider.template"),
                require => Class["oi-identity-gateway::install"],
        }

	file {"/opt/openinfinity/2.0.0/sso-tools/liferayDBrepo.xml":
		ensure => present,
		owner => 'toas',
                group => 'toas',
                mode => 0644,
                content => template("oi-identity-gateway/liferayDBrepo.xml.template"),
                require => Class["oi-identity-gateway::install"],
	}

	file {"/opt/openinfinity/2.0.0/sso-tools/datasoreDatafile":
                ensure => present,
                owner => 'toas',
                group => 'toas',
                mode => 0644,
                content => template("oi-identity-gateway/datastoreDatafile.template"),
                require => Class["oi-identity-gateway::install"],
        }

	file {"/opt/openinfinity/2.0.0/sso-tools/amadmin.pwd":
                ensure => present,
                owner => 'toas',
                group => 'toas',
                mode => 0400,
                content => template("oi-identity-gateway/amadmin.pwd.template"),
                require => Class["oi-identity-gateway::install"],
        }

	file {"/opt/openinfinity/2.0.0/sso-tools/configure-openam.sh":
                ensure => present,
                owner => 'toas',
                group => 'toas',
                mode => 0755,
                source => "puppet:///modules/oi-identity-gateway/configure-openam.sh",
                require => Class["oi-identity-gateway::install"],
        } 

	exec { "create-configs":
		command => "/opt/openinfinity/2.0.0/sso-tools/generate-configs.sh",
		user => 'toas',
		refreshonly => true,
		subscribe => File ["/opt/openinfinity/2.0.0/sso-tools/generate-configs.sh"],
	}

	file {"/etc/haproxy/haproxy.cfg":
		ensure => present,
		owner => 'root',
		group => 'root',
		mode => 0644,
		source => "puppet:///modules/oi-identity-gateway/haproxy.cfg",
		require => Package["haproxy"],
		notify => Service["haproxy"],
	}

}

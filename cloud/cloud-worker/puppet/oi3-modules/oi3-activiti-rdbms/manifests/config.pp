class oi3-activiti-rdbms::config {
	
	# Directory for activiti schema files
	file { "/opt/openinfinity/3.0.0/activiti":
		ensure => directory,
		group => "root",
		owner => "root",
		require => Class["oi3-rdbms::service"],
	}

	# Directory for activiti schema files
	file { "/opt/openinfinity/3.0.0/activiti/dbschema":
		ensure => directory,
		group => "root",
		owner => "root",
		require => file["/opt/openinfinity/3.0.0/activiti"],
	}
	
	# Activiti schema create scripts
	file { "/opt/openinfinity/3.0.0/activiti/dbschema/activiti.mysql.create.engine.sql":
                ensure => present,
                source => "puppet:///modules/oi3-activiti-rdbms/activiti.mysql.create.engine.sql",
                owner => "root",
                group => "root",
	      require => Class["oi3-rdbms::service"],
                notify => Class["oi3-activiti-rdbms::service"],
        }		
	file { "/opt/openinfinity/3.0.0/activiti/dbschema/activiti.mysql.create.history.sql":
                ensure => present,
                source => "puppet:///modules/oi3-activiti-rdbms/activiti.mysql.create.history.sql",
                owner => "root",
                group => "root",
	      require => Class["oi3-rdbms::service"],
                notify => Class["oi3-activiti-rdbms::service"],
        }		
	file { "/opt/openinfinity/3.0.0/activiti/dbschema/activiti.mysql.create.identity.sql":
                ensure => present,
                source => "puppet:///modules/oi3-activiti-rdbms/activiti.mysql.create.identity.sql",
                owner => "root",
                group => "root",
	      require => Class["oi3-rdbms::service"],
                notify => Class["oi3-activiti-rdbms::service"],
        }		
	
}


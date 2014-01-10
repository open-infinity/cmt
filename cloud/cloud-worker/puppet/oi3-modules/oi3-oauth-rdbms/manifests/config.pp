class oi3-oauth-rdbms::config {
	
	# Directory for oauth schema files
	file { "/opt/openinfinity/3.0.0/oauth":
		ensure => directory,
		group => "root",
		owner => "root",
		require => Class["oi3-rdbms::service"],
	}

	# Directory for oauth schema files
	file { "/opt/openinfinity/3.0.0/oauth/dbschema":
		ensure => directory,
		group => "root",
		owner => "root",
		require => file["/opt/openinfinity/3.0.0/oauth"],
	}

	# Oauth schema create scripts
	file { "/opt/openinfinity/3.0.0/oauth/dbschema/oauth2-schema.sql":
                ensure => present,
                source => "puppet:///modules/oi3-oauth-rdbms/oauth2-schema.sql",
                owner => "root",
                group => "root",
	      require => Class["oi3-rdbms::service"],
                notify => Class["oi3-oauth-rdbms::service"],
        }		
}


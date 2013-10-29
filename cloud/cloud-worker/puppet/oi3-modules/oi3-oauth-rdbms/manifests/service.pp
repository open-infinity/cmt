class oi3-oauth-rdbms::service {
	
	exec { "oi3-create-oauth-db-and-schema":
                unless => "/opt/openinfinity/2.0.0/mariadb/bin/mysql -uroot -p${mysql_password} oauth",
                command => "/opt/openinfinity/3.0.0/mariadb/bin/mysql -uroot -p${mysql_password} -e \"create database oauth; grant all privileges on oauth.* to
                        'openinfinity'@'%' identified by 'cloudtools'; flush privileges; use oauth;  source /opt/openinfinity/3.0.0/oauth/dbschema/oauth-schema.sql;\"",
		require => Class["oi3-oauth-rdbms::config"],
      }
	
}

class oi3-oauth-rdbms::service {
	
	exec { "oi3-create-oauth-db-and-schema":
                unless => "/usr/bin/mysql -uroot -p${mysql_password} oauth",
                command => "/usr/bin/mysql -uroot -p${mysql_password} -e \"create database oauth; grant all privileges on oauth.* to
                        'openinfinity'@'%' identified by '${oi_dbuser_pwd}'; flush privileges; use oauth;  source /opt/openinfinity/3.0.0/oauth/dbschema/oauth2-schema.sql;\"",
		require => Class["oi3-oauth-rdbms::config"],
      }
	
}

class oi3-portal-rdbms::service {

	exec { "oi3-create-portal-db":
		unless => "/usr/bin/mysql -uroot -p${mysql_password} lportal",
		command => "/usr/bin/mysql -uroot -p${mysql_password} -e \"create database lportal; grant all privileges on lportal.* to
			'liferay'@'%' identified by '${liferay_db_password}'; flush privileges;\"",
		require => Class["oi3-rdbms::service"],
	}

}

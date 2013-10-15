class oi3-activemq-rdbms::service {

	exec { "create-activemq-db":
		unless => "/opt/openinfinity/2.0.0/mariadb/bin/mysql -uroot -p${mysql_password} toasactivemq",
		command => "/opt/openinfinity/2.0.0/mariadb/bin/mysql -uroot -p${mysql_password} -e \"create database toasactivemq; grant all privileges on toasactivemq.* to
			'activemq'@'%' identified by 'toasactivemq'; flush privileges;\"",
		#require => Class["oi3-mariadb::service"],
		require => Class["oimariadb::service"],
	}
	
}

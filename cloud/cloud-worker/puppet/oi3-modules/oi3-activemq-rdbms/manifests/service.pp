class oi3-activemq-rdbms::service {

	exec { "oi3-create-activemq-db":
		unless => "/usr/bin/mysql -uroot -p${mysql_password} toasactivemq",
		command => "/usr/bin/mysql -uroot -p${mysql_password} -e \"create database toasactivemq; grant all privileges on toasactivemq.* to
			'activemq'@'%' identified by '${amq_password}'; flush privileges;\"",
		require => Class["oi3-rdbms::service"],
	}
	
}

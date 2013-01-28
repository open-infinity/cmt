class oimariadb::service {
	service { "oi-mariadb":
		ensure => running,
		hasstatus => true,
		hasrestart => true,
		enable => true,
		require => Class["oimariadb::config"],
	}

	exec { "set-mariadb-password":
		unless => "/opt/openinfinity/2.0.0/mariadb/bin/mysqladmin -uroot -p${mysql_password} status",
		command => "/opt/openinfinity/2.0.0/mariadb/bin/mysqladmin -uroot password ${mysql_password}",
		require => Service["oi-mariadb"],
	}

	exec { "create-lportal-db":
                unless => "/opt/openinfinity/2.0.0/mariadb/bin/mysql -uroot -ptoas lportal",
                command => "/opt/openinfinity/2.0.0/mariadb/bin/mysql -uroot -ptoas -e \"create database lportal; grant all privileges on lportal.* to
                        'liferay'@'%' identified by 'toasliferay'; flush privileges;\"",
                require => exec["set-mariadb-password"],
        }

	exec { "create-activemq-db":
		unless => "/opt/openinfinity/2.0.0/mariadb/bin/mysql -uroot -ptoas toasactivemq",
		command => "/opt/openinfinity/2.0.0/mariadb/bin/mysql -uroot -ptoas -e \"create database toasactivemq; grant all privileges on toasactivemq.* to
			'activemq'@'%' identified by 'toasactivemq'; flush privileges;\"",
		require => exec["set-mariadb-password"],
	}
}

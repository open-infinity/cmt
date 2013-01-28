class oimariadb {
	include oimariadb::install, oimariadb::config, oimariadb::service, oimariadb::util
}

class oimariadb::util {
define db ( $user, $password) {
	include oimariadb
	
	exec { "create-${name}-db":
		unless => "/opt/openinfinity/2.0.0/mariadb/bin/mysql -u${user} -p${password} ${name}",
		command => "/opt/openinfinity/2.0.0/mariadb/bin/mysql -uroot -p${mysql_password} -e \"create database ${name}; grant all privileges on ${name}.* to
			'${user}'@'%' identified by '$password'; flush privileges;\"",
		require => Service["oi-mariadb"],
	}
}
}

class oi3-mariadbgalera::config {
	file {"/etc/my.cnf":
		ensure => present,
		source => "puppet:///modules/oi3-mariadbgalera/my.cnf_client",
		owner => "root",
		group => "root",
		mode => 0755,
		require => Class["oi3-mariadbgalera::install"],
	}

	file {"/root/mysql_system_tables_data.sql":
		ensure => present,
		content => template("oi3-mariadbgalera/mysql_system_tables_data.sql.erb"),
		owner => "root",
		group => "root",
		mode => 0640,
		require => package["oi3-mariadb-galera-3.0.0-2"],
	}

	file {"/root/mysql_install_db":
		ensure => present,
		source => "puppet:///modules/oi3-mariadbgalera/mysql_install_db",
		owner => "root",
		group => "root",
		mode => 0750,
		require => file["/root/mysql_system_tables_data.sql"],
	}

	exec {"create-mysql-database":
		unless => "/usr/bin/test -f /opt/openinfinity/3.0.0/rdbms/data/mysql/user.frm",
		command => "/root/mysql_install_db --user=mysql --defaults-file=/opt/openinfinity/3.0.0/rdbms/etc/my.cnf",
		require => file["/root/mysql_install_db"],
	}
}	

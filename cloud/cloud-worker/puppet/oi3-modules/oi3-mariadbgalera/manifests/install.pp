class oi3-mariadbgalera::install {

	file {"/opt/openinfinity/3.0.0/rdbms":
		ensure => directory,
                owner => "mysql",
                group => "mysql",
                mode => 0775,
		require => file["/opt/openinfinity/3.0.0"],
	}

	file {"/opt/openinfinity/3.0.0/rdbms/etc":
                ensure => directory,
                owner => "mysql",
                group => "mysql",
                mode => 0775,
                require => file["/opt/openinfinity/3.0.0/rdbms"],
        }

	file {"/opt/openinfinity/3.0.0/rdbms/data":
                ensure => directory,
                owner => "mysql",
                group => "mysql",
                mode => 0775,
                require => file["/opt/openinfinity/3.0.0/rdbms"],
        }	


	file {"/opt/openinfinity/3.0.0/rdbms/etc/my.cnf":
		ensure => present,
		content => template("oi3-mariadbgalera/server_my.cnf.erb"),
		owner => "mysql",
		group => "mysql",
		mode => 0644,
		require => file["/opt/openinfinity/3.0.0/rdbms/etc"],
	}

	package { "oi3-mariadb-galera-3.0.0-2":
		ensure => present,
		require => file["/opt/openinfinity/3.0.0/rdbms/etc/my.cnf"],
	}

	file { "/etc/init.d/mysql":
		ensure => present,
		source => "puppet:///modules/oi3-mariadbgalera/mysql",
		owner => "root",
		group => "root",
		mode => 0755,
		require => package["oi3-mariadb-galera-3.0.0-2"],
	}

	user { "mysql":
		ensure => present,
		comment => "MariaDB user",
		gid => "mysql",
		shell => "/bin/false",
		require => Group["mysql"],
	}

	group { "mysql":
		ensure => present,
	}
}

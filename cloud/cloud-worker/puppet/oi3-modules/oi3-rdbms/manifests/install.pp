class oi3-rdbms::install {

	file {"/opt/openinfinity/3.0.0/rdbms":
		ensure => directory,
                owner => "mysql",
                group => "mysql",
                mode => 0775,
		require => file["/opt/openinfinity/3.0.0"],
	}

#	file {"/opt/openinfinity/3.0.0/rdbms/etc":
#                ensure => directory,
#                owner => "mysql",
#                group => "mysql",
#                mode => 0775,
#                require => file["/opt/openinfinity/3.0.0/rdbms"],
#        }

	file {"/opt/openinfinity/3.0.0/rdbms/data":
                ensure => directory,
                owner => "mysql",
                group => "mysql",
                mode => 0775,
                require => file["/opt/openinfinity/3.0.0/rdbms"],
        }	


	file {"/opt/openinfinity/3.0.0/rdbms/data/my.cnf":
		ensure => present,
		source => "puppet:///modules/oi3-rdbms/my.cnf",
		owner => "mysql",
		group => "mysql",
		mode => 0644,
		require => file["/opt/openinfinity/3.0.0/rdbms/data"],
	}

	package { "oi3-rdbms-3.0.0-1":
		ensure => present,
		require => file["/opt/openinfinity/3.0.0/rdbms/data/my.cnf"],
	}

	file { "/etc/init.d/mysql":
		ensure => present,
		source => "puppet:///modules/oi3-rdbms/mysql",
		owner => "root",
		group => "root",
		mode => 0755,
		require => package["oi3-rdbms-3.0.0-1"],
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

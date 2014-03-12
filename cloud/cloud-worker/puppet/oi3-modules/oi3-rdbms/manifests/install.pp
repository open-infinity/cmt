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

	package { "oi3-rdbms":
		ensure => present,
		require => file["/opt/openinfinity/3.0.0/rdbms/data"],
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

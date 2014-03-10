class oi3-rdbms::config {
    file {"/etc/my.cnf.d/openinfinity.cnf":
               ensure => present,
               source => "puppet:///modules/oi3-rdbms/openinfinity.cnf",
               owner => "root",
               group => "root",
               mode => 0644,
               require => Class["oi3-rdbms::install"],
	}

	file {"/opt/openinfinity/3.0.0/rdbms/log":
		ensure => directory,
		owner => "mysql",
		group => "mysql",
		mode => 0755,
		require => Class["oi3-rdbms::install"],
	}

	exec {"create-mysql-database":
               unless => "/usr/bin/test -f /opt/openinfinity/3.0.0/rdbms/data/mysql/user.frm",
               command => "/usr/bin/mysql_install_db --user=mysql --defaults-file=/etc/my.cnf", 
               require => Class["oi3-rdbms::install"],
        }
}	

class oi3-rdbms::service {
        service { "mysql":
                ensure => running,
                hasstatus => true,
                hasrestart => true,
                enable => true,
                require => Class["oi3-rdbms::config"],
        }

        exec { "set-mariadb-password":
                unless => "/usr/bin/mysqladmin -uroot -p${mysql_password} status",
                command => "/usr/bin/mysqladmin -uroot password ${mysql_password}",
                require => Service["mysql"],
        }

        exec { "create-backup-user":
                unless => "/usr/bin/mysql -ubackup -ptoasbackup",
                command => "/usr/bin/mysql -uroot -p${mysql_password} -e \"grant show databases, select, lock tables, reload, create, drop, delete, insert, alter on *.* to backup@localhost identified by 'toasbackup'; flush privileges;\"",
                require => exec["set-mariadb-password"],
        }
}


class oi3-rdbms::config inherits oi3variables {

    file { '/rdbms_conf':
       path => $openInfinityConfPath,
       ensure => present,
       source => "puppet:///modules/oi3-rdbms/openinfinity.cnf",
       owner => "root",
       group => "root",
       mode => 0644,
       require => Class["oi3-rdbms::install"],
    }

    file {"/opt/openinfinity/3.1.0/rdbms/log":
        ensure => directory,
        owner => "mysql",
        group => "mysql",
        mode => 0755,
        require => Class["oi3-rdbms::install"],
    }

    exec {"create-mysql-database":
       path => "/bin",
       unless => "/usr/bin/test -f /opt/openinfinity/3.1.0/rdbms/data/mysql/user.frm",
       command => $createMysqlDatabaseCommand, 
       require => Class["oi3-rdbms::install"],
    }
}   

class oi3-activemq-rdbms::service {

        $nodeids = parsejson($basmachineidlist)

        define amqdbcreate {
                exec { "oi3-create-activemq-db-${name}":
                        unless => "/usr/bin/mysql -uroot -p${mysql_password} toasamq${name}",
                        command => "/usr/bin/mysql -uroot -p${mysql_password} -e \"create database toasamq${name}; grant all privileges on toasamq${name}.* to 'activemq'@'%' identified by '${amq_password}'; flush privileges;\"",
                        require => Class["oi3-rdbms::service"],
               }
        }

        amqdbcreate { $nodeids: }
}

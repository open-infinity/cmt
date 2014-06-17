class mountdata::config {
        file {"/opt/data":
                ensure => directory,
                owner => 'root',
                group => 'root',
                mode => 777,
        }

        file {"/data":
                ensure => link,
                target => '/opt/data',
		force => true,
                require => File["/opt/data"],
        }

}

class mountdata {
        include mountdata::config
}


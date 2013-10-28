class oi3-serviceplatform::service inherits oi3-bas::service {
        service ["oi-tomcat"] {
                ensure => running,
                hasrestart => true,
                enable => true,
                require => Class["oi3-bas::config"],
        }
}
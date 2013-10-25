class oiserviceplatform::service inherits oi3-bas::service {
	service {"oi-tomcat":
		ensure => running,
		hasrestart => true,
		enable => true,
		require => Class["oiserviceplatform::config"],
	}
}

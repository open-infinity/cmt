class oiserviceplatform::install {
	package { ["oi-activemq-5.7-1", "oi-activiti-5.7-1", "oi-mule-3.2.1-1", "oi-springdatahadoop-1.0.1-1" ]:
		ensure => present,
		require => Class["oibas"],
	}

}

class oi3-serviceplatform::install {
	case $operatingsystem {

'RedHat', 'CentOS': {

$javaPackageName = 'java-1.7.0-openjdk'

}

'Ubuntu': {

$javaPackageName = 'openjdk-7-jdk'


}

default: { fail("Unsupported operating system") }

}
	package { ["java-1.7.0-openjdk", "oi3-serviceplatform", "oi3-bas"]:
		ensure => present,
		require => Class["oi3-basic"],
	}

}

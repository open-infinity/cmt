class oi3ldap {
  ensure_resource('package', 'java-1.7.0-openjdk', {
	ensure => present,
  })

  package {"apacheds":
    ensure => present,
    require => Package["java-1.7.0-openjdk"]
  }

  service {"apacheds":
    name => "apacheds-2.0.0_M16-default",
    ensure => "running",
    enable => true,
    require => Package["apacheds"]
  }
}

class oi3ldap {
  package {"java-1.7.0-openjdk":
    ensure => present
  }
  package {"apacheds":
    ensure => present,
    require => Package["java-1.7.0-openjdk"]
  }

  service {"apacheds":
    name => "apacheds-2.0.0_M15-default",
    ensure => "running",
    enable => true,
    require => Package["apacheds"]
  }
}

class oi3ldap inherits oi3variables {
  ensure_resource('package', $javaPackageName, {
    ensure => present,
  })

  package {"apacheds":
    ensure => present,
    require => Package[$javaPackageName]
  }

  service {"apacheds":
    name => $apachedsServiceName,
    ensure => "running",
    enable => true,
    require => Package["apacheds"]
  }
}

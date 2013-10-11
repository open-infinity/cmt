include '::ntp'
class { '::ntp':
  servers  => [ $ntpserver' ],
  restrict => 'restrict 127.0.0.1',
  service_ensure => true,
  service_enable => true
}

class oihealthmonitoring::service {
    service { "collectd":
        ensure => running,
        hasrestart => true,
        enable => true,
        require => Class["oihealthmonitoring::config"],
    }    
    service { "oi-healthmonitoring":
        #25.4.2013 TE note: disbaled due to stalls installation when running at RHEL 
        #ensure => running,
        #hasrestart => true,
        ensure => stopped,
        hasrestart => false,
        enable => true,
        require => Class["oihealthmonitoring::config"],
    }    
}


class oihealthmonitoring::config {
    file { "/etc/profile.d/toas.sh":
        ensure => present,
        owner => 'toas',
        group => 'toas',
        mode => 0755,
        content => template("oihealthmonitoring/toas.sh.template"),
        require => Class["oihealthmonitoring::install"],
    } 

    file { "/etc/init.d/oi-healthmonitoring":
        ensure => present,
        owner => 'toas',
        group => 'toas',
        mode => 0744,
        content => template("oihealthmonitoring/oi-healthmonitoring.template"),
        require => Class["oihealthmonitoring::install"],
    } 
    
    file { "/opt/collectd/etc/collectd.conf":
        ensure => present,
        owner => 'toas',
        group => 'toas',
        mode => 0644,
        content => template("oihealthmonitoring/collectd.conf.template"),
        require => Class["oihealthmonitoring::install"],
    } 
   
    # TODO: What should be inserted in this file??    
    file { "/usr/local/bin/gethostlist":
        ensure => present,
        owner => 'toas',
        group => 'toas',
        mode => 0755,
        content => template("oihealthmonitoring/gethostlist.template"),
        require => Class["oihealthmonitoring::install"],
    } 

    # TODO: email parameters    
    file { "/opt/monitoring/etc/mail.properties":
        ensure => present,
        owner => 'toas',
        group => 'toas',
        mode => 0600,
        content => template("oihealthmonitoring/mail.properties.template"),
        require => Class["oihealthmonitoring::install"],
    } 
    
    # TODO: SNMP config
}


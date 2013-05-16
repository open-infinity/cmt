class oihealthmonitoring::config { 
    file { "/etc/profile.d/toas.sh":
        ensure => file,
        owner => 'toas',
        group => 'toas',
        mode => 0755,
        content => template("oihealthmonitoring/toas.sh.template"),
        require => Class["oihealthmonitoring::install"],
    } 
    file { "/opt/openinfinity/2.0.0/healthmonitoring/collectd/etc/collectd.conf":
        ensure => file,
        owner => 'toas',
        group => 'toas',
        mode => 0644,
        content => template("oihealthmonitoring/collectd.conf.template"),
        require => Class["oihealthmonitoring::install"],
    } 
    file { "/opt/openinfinity/2.0.0/healthmonitoring/nodechecker/etc/nodechecker.conf":
        ensure => file,
        owner => 'toas',
        group => 'toas',
        mode => 0744,
        content => template("oihealthmonitoring/nodechecker.conf.template"),
        require => Class["oihealthmonitoring::install"],
    } 
    file { "/opt/openinfinity/2.0.0/healthmonitoring/nodechecker/etc/nodelist.conf":
        ensure => file,
        owner => 'toas',
        group => 'toas',
        mode => 0744,
        content => template("oihealthmonitoring/nodelist.conf.template"),
        require => Class["oihealthmonitoring::install"],
    }
}

class oi3-healthmonitoring::config { 
    file { "/etc/profile.d/oi.sh":
        ensure => file,
        owner => 'oiuser',
        group => 'oiuser',
        mode => 0755,
        content => template("oi3-healthmonitoring/oi.sh.erb"),
        require => Class["oi3-healthmonitoring::install"],
    } 
    file { "/opt/openinfinity/3.0.0/healthmonitoring/collectd/etc/collectd.conf":
        ensure => file,
        owner => 'oiuser',
        group => 'oiuser',
        mode => 0644,
        content => template("oi3-healthmonitoring/collectd.conf.erb"),
        require => Class["oi3-healthmonitoring::install"],
    } 
    file { "/opt/openinfinity/3.0.0/healthmonitoring/nodechecker/etc/nodechecker.conf":
        ensure => file,
        owner => 'oiuser',
        group => 'oiuser',
        mode => 0744,
        content => template("oi3-healthmonitoring/nodechecker.conf.erb"),
        require => Class["oi3-healthmonitoring::install"],
    } 
    file { "/opt/openinfinity/3.0.0/healthmonitoring/nodechecker/etc/nodelist.conf":
        ensure => file,
        owner => 'oiuser',
        group => 'oiuser',
        mode => 0744,
        content => template("oi3-healthmonitoring/nodelist.conf.erb"),
        require => Class["oi3-healthmonitoring::install"],
    }

    file { "/opt/openinfinity/3.0.0/healthmonitoring/collectd/etc/collectd.d/threshold.conf":
        ensure => file,
        owner => 'oiuser',
        group => 'oiuser',
        mode => 0644,
        content => template("oi3-healthmonitoring/threshold.conf.erb"),
        require => Class["oi3-healthmonitoring::install"],
    }

    file { "/opt/openinfinity/3.0.0/healthmonitoring/nodechecker/var/lib/notifications/inbox":
        mode => 0777,
        require => Class["oi3-healthmonitoring::install"],
    }

    file { "/opt/openinfinity/3.0.0/healthmonitoring/nodechecker/var/lib/notifications/sent":
        mode => 0777,
        require => Class["oi3-healthmonitoring::install"],
    }


}

class oi3-healthmonitoring-rdbms::config { 
    file { "/etc/profile.d/oi.sh":
        ensure => file,
        owner => 'oiuser',
        group => 'oiuser',
        mode => 0755,
        content => template("oi3-healthmonitoring-rdbms/oi.sh.erb"),
        require => Class["oi3-healthmonitoring-rdbms::install"],
    } 
    file { "/opt/openinfinity/3.0.0/healthmonitoring/collectd/etc/collectd.conf":
        ensure => file,
        owner => 'oiuser',
        group => 'oiuser',
        mode => 0644,
        content => template("oi3-healthmonitoring-rdbms/collectd.conf.erb"),
        require => Class["oi3-healthmonitoring-rdbms::install"],
    } 
    file { "/opt/openinfinity/3.0.0/healthmonitoring/nodechecker/etc/nodechecker.conf":
        ensure => file,
        owner => 'oiuser',
        group => 'oiuser',
        mode => 0744,
        content => template("oi3-healthmonitoring-rdbms/nodechecker.conf.erb"),
        require => Class["oi3-healthmonitoring-rdbms::install"],
    } 
    file { "/opt/openinfinity/3.0.0/healthmonitoring/nodechecker/etc/nodelist.conf":
        ensure => file,
        owner => 'oiuser',
        group => 'oiuser',
        mode => 0744,
        content => template("oi3-healthmonitoring-rdbms/nodelist.conf.erb"),
        require => Class["oi3-healthmonitoring-rdbms::install"],
    }

    file { "/opt/openinfinity/3.0.0/healthmonitoring/collectd/etc/collectd.d/threshold.conf":
        ensure => file,
        owner => 'oiuser',
        group => 'oiuser',
        mode => 0644,
        content => template("oi3-healthmonitoring-rdbms/threshold.conf.erb"),
        require => Class["oi3-healthmonitoring-rdbms::install"],
    }

    file { "/opt/openinfinity/3.0.0/healthmonitoring/nodechecker/var/lib/notifications/inbox":
        mode => 0777,
        require => Class["oi3-healthmonitoring-rdbms::install"],
    }

    file { "/opt/openinfinity/3.0.0/healthmonitoring/nodechecker/var/lib/notifications/sent":
        mode => 0777,
        require => Class["oi3-healthmonitoring-rdbms::install"],
    }


}

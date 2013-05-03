class oihealthmonitoring::config {
    # TOAS environment variables
    file { "/etc/profile.d/toas.sh":
        ensure => file,
        owner => 'toas',
        group => 'toas',
        mode => 0755,
        content => template("oihealthmonitoring/toas.sh.template"),
        require => Class["oihealthmonitoring::install"],
    } 

    # Healthmonitoring init script
    #file { "/etc/init.d/oi-healthmonitoring":
    #    ensure => present,
    #    owner => 'toas',
    #    group => 'toas',
    #    mode => 0744,
    #    content => template("oihealthmonitoring/oi-healthmonitoring.template"),
    #    require => Class["oihealthmonitoring::install"],
    #} 
    
    # Collectd configuration file
    file { "/opt/openinfinity/2.0.0/healthmonitoring/collectd/etc/collectd.conf":
        ensure => file,
        owner => 'toas',
        group => 'toas',
        mode => 0644,
        content => template("oihealthmonitoring/collectd.conf.template"),
        require => Class["oihealthmonitoring::install"],
    } 

    # Df configuration file
    #file { "/opt/collectd/etc/collectd.d/df.conf":
    #    ensure => file,
    #    owner => 'toas',
    #    group => 'toas',
    #    mode => 0644,
    #    content => template("oihealthmonitoring/df.conf.template"),
    #    require => Class["oihealthmonitoring::install"],
    #} 

    # Collectd conf directory   
    #file { "/opt/collectd/etc/collectd.d":
    #    ensure => directory, # so make this a directory
    #    recurse => true, # enable recursive directory management
    #    owner => "toas",
    #    group => "toas",
    #    mode => 0774,
    #    source => "puppet:///oihealthmonitoring/empty",
    #    require => Class["oihealthmonitoring::install"],
    #}
    
    # Collectd network configuration file
    #file { "/opt/collectd/etc/collectd.d/network.conf":
    #    ensure => file,
    #    owner => 'toas',
    #    group => 'toas',
    #    mode => 0644,
    #    content => template("oihealthmonitoring/network.conf.template"),
    #    require => Class["oihealthmonitoring::install"],
    #} 

    # RRD data dir
    #file { "/opt/collectd/usr":
    #    ensure => directory, owner => "toas", group => "toas", mode => 0774,
    #    require => Class["oihealthmonitoring::install"],
    #}

    #file { "/opt/collectd/usr/var":
    #    ensure => directory, owner => "toas", group => "toas", mode => 0774,
    #	require => File["/opt/collectd/usr"],
    #}

    #file { "/opt/collectd/usr/var/lib":
    #    ensure => directory, owner => "toas", group => "toas", mode => 0774,
    #	require => File["/opt/collectd/usr/var"],
    #}

    #file { "/opt/collectd/usr/var/lib/collectd":
    #    ensure => directory, owner => "toas", group => "toas", mode => 0774,
    #	require => File["/opt/collectd/usr/var/lib"],
    #}
    
    #file { "/opt/collectd/usr/var/lib/collectd/rrd":
    #    ensure => directory, owner => "toas", group => "toas", mode => 0774,
    #	require => File["/opt/collectd/usr/var/lib/collectd"],
    #}

    # Monitoring common.sh
    #file { "/opt/monitoring/bin/common.sh":
    #    ensure => file,
    #    owner => 'toas',
    #    group => 'toas',
    #    mode => 0755,
    #    content => template("oihealthmonitoring/common.sh.template"),
    #    require => Class["oihealthmonitoring::install"],
    #} 

    # Monitoring start.sh
    #file { "/opt/monitoring/bin/start.sh":
    #    ensure => file,
    #    owner => 'toas',
    #    group => 'toas',
    #    mode => 0744,
    #    content => template("oihealthmonitoring/start.sh.template"),
    #    require => Class["oihealthmonitoring::install"],
    #} 

    # Nodechecker configuration 
    file { "/opt/openinfinity/2.0.0/healthmonitoring/nodechecker/etc/nodechecker.conf":
        ensure => file,
        owner => 'toas',
        group => 'toas',
        mode => 0744,
        content => template("oihealthmonitoring/nodechecker.conf.template"),
        require => Class["oihealthmonitoring::install"],
    } 

    # This file should have all the nodes listed
    file { "/opt/openinfinity/2.0.0/healthmonitoring/nodechecker/etc/nodelist.conf":
        ensure => file,
        owner => 'toas',
        group => 'toas',
        mode => 0744,
        content => template("oihealthmonitoring/nodelist.conf.template"),
        require => Class["oihealthmonitoring::install"],
    } 

    # Cronttab for notifications    
    #file { "/etc/cron.d/notifications_monitoring":
    #    ensure => file,
    #    owner => 'root',
    #    group => 'root',
    #    mode => 0644,
    #    source => "puppet:///oihealthmonitoring/etc_crond_notifications_monitoring",
    #    require => Class["oibasic::install"], # dependency to oibasic
    #} 
    
    # Notificator script
    #file { "/opt/monitoring/bin/notificator.sh":
    #    ensure => file,
    #    owner => 'toas',
    #    group => 'toas',
    #    mode => 0755,
    #    content => template("oihealthmonitoring/notificator.sh.template"),
    #    require => Class["oihealthmonitoring::install"],
    #} 

#    # Notifications directory
#    file { "/opt/monitoring/notifications":
#        ensure => directory, owner => "toas", group => "toas", mode => 0774,
#        require => Class["oihealthmonitoring::install"],
#    }
}


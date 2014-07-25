class jetty($directory = '/opt', $user = 'jetty', $group = 'jetty') {
    ensure_resource('user', $user, {
        home => "${directory}/jetty",
        managehome => false,
        system => true,
        gid => $group,
        before => Package['oi3-jetty'],
    })

    ensure_resource('group', $group, {
        ensure => present
    })

    package { "oi3-jetty":
        ensure => present,
    } ->
    file { "/etc/default/jetty":
        content => template("oi3jetty/default"),
        owner => 'root',
        group => 'root',
        mode => 0644,
    } ->
    file { "start.ini":
        path => "${directory}/jetty/start.ini_openinfinity_example",
        source => "puppet:///modules/oi3jetty/start.ini",
        owner => $user,
        group => $group,
        mode => 0644,
        notify => Service["jetty"]
    } ->
    file { "/etc/init.d/jetty":
        ensure => link,
        target => "${directory}/jetty/bin/jetty.sh",
    } ~>

    service { "jetty":
        enable => true,
        ensure => running,
        hasstatus => false,
        hasrestart => true,
    }

}

class oi3jetty {
    
    class {'jetty':
        directory => "/opt/openinfinity/3.1.0/",
        user => "oiuser",
        group => "oiuser",
    }
}

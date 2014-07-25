class oi3httpd inherits oi3variables  {
    package { $apacheInstallPackageNames:
                ensure => installed
        }

    if ($operatingsystem == 'Ubuntu') {
        exec { '/usr/sbin/a2enmod ssl && /usr/bin/service apache2 restart':
            user => 'root',
            unless => '/usr/sbin/a2query -m ssl',
            require => Package[$apachePackageName],
        }   
    }

    if $::spused {
        file { "${apacheConfPath}shib.conf.toas":
            source => "puppet:///modules/oi3httpd/shib.conf",
            replace => true,
            owner => "root",
            group => "root",
            mode => 0644,
            require => Package[$apachePackageName],
        }
    }

    file { "${apacheConfPath}oi3-proxy.conf.toas":
        source => "puppet:///modules/oi3httpd/oi3-proxy.conf",
        replace => true,
        owner => "root",
        group => "root",
        mode => 0644,
        require => Package[$apachePackageName],
    }

    service {$apacheServiceName:
        ensure => running,
        enable => true,
        require => Package[$apachePackageName]
    }
}

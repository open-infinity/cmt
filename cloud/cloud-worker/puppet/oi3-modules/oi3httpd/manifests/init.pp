class oi3httpd {
	package { ["httpd", "mod_ssl"]:
                ensure => installed
        }

	if $::spused {
		file { "/etc/httpd/conf.d/shib.conf.toas":
                	source => "puppet:///modules/oi3httpd/shib.conf",
                	replace => true,
                	owner => "root",
                	group => "root",
                	mode => 0644,
                	require => Package["httpd"],
        	}
	}

        file { "/etc/httpd/conf.d/oi3-proxy.conf.toas":
                source => "puppet:///modules/oi3httpd/oi3-proxy.conf",
                replace => true,
                owner => "root",
                group => "root",
                mode => 0644,
                require => Package["httpd"],
        }

	service {"httpd":
                ensure => running,
                enable => true,
                require => Package["httpd"]
        }
}

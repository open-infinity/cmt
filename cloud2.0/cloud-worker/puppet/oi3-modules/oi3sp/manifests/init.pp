class oi3shibsp ($ensure = "running", $enable = true, $config = undef) {
	package { "oi3-sp-lb":
		ensure => present
	}

	file { "/etc/shibboleth/shibboleth2.xml.toas":
		content => template($config),
		replace => true,
		owner => "root",
		group => "root",
		mode => 0644,
		require => Package["oi3-sp-lb"]
	}

	service {"shibd":
		ensure => $ensure,
		enable => $enable,
		require => Package["oi3-sp-lb"]
	}
}

class oi3sp {
	if $::spclustered {
		if $::spmachine {
			class {'oi3shibsp':
				ensure => "running",
				enable => true,
				config => "oi3sp/shibboleth2.xml.clustered.erb",
			}
		}
		else {
			class {'oi3shibsp':
				ensure => "stopped",
				enable => false,
				config => "oi3sp/shibboleth2.xml.clustered.erb",
			}
		}
	}
	else {
		class {'oi3shibsp':
			ensure => "running",
			enable => true,
			config => "oi3sp/shibboleth2.xml.single.erb",
		}
	}
}

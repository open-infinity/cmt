# The PuppetForge MongoDB module is buggy and seem not to be updated since
# May 2014. It's better replace it with a self-made script, tailored for our
# needs.

class oi3mongodbmini {
	require oi3-basic
    include oi3mongodbmini::install
    include oi3mongodbmini::config
    include oi3mongodbmini::service
}

class oi3mongodbmini::install {
    package { ["mongodb-org-server", "mongodb-org-shell"]:
        ensure => present,
    }
}

class oi3mongodbmini::config {
    file { '/etc/mongod.conf':
        ensure => present,
        notify => Service["mongod"],
        owner => "mongod",
        group => "mongod",
        source => "puppet:///modules/oi3mongodbmini/mongod.conf",
        require => Class["oi3mongodbmini::install"],
    }

    file {"/var/lib/mongodb":
        ensure => directory,
        mode => 0755,
        owner => "mongod",
        group => "mongod",
    }

}

class oi3mongodbmini::service {
    service { "mongod":
        ensure => running,
        hasrestart => true,
        enable => true,
        require => Class["oi3mongodbmini::config"],
    }
}

# ------------------------------------------------------------------------------
## This is a simple plain stand-alone MongoDB installation running on the 
## default port 27017.
##
## You have to run the following command as root in puppet master machine.
##
##   puppet module install puppetlabs-mongodb
##

#class oi3mongodbmini {
#	require oi3-basic
#	include '::mongodb::server'
#	include '::mongodb::client'
#}

#class {'::mongodb::globals':
#    manage_package_repo => true,
#}

#class {'::mongodb::server':
#    smallfiles => true,
#    bind_ip => ['0.0.0.0'],
#    maxconns => 10,
#}
#class {'::mongodb::client': }


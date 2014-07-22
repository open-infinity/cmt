
# This is a simple plain stand-alone MongoDB installation running on the 
# default port 27017.
#
# You have to run the following command as root in puppet master machine.
#
#   puppet module install puppetlabs-mongodb
#

class oi3mongodbmini {
	require oi3-basic
	include '::mongodb::client'
	include '::mongodb::server'
}

class {'::mongodb::globals':
    manage_package_repo => true,
}

class {'::mongodb::server':
    smallfiles => true,
    bind_ip => '0.0.0.0',
}


define createpart (
	$device = "vdb",
	$fstype = "ext3",
	$owner = "root",
	$group = "root",
	$mode = "755" ) {
		
	file {"${name}": 
		ensure => directory,
		owner => $owner,
		group => $group,
		mode => $mode,
	}
	
	exec { "parted-${device}":
		path => ["/sbin", "/bin", "/usr/sbin", "/usr/bin" ],
		logoutput => true,
		command => "parted /dev/${device} mklabel msdos && parted /dev/${device} mkpart primary ext2 0% 100% && mkfs.ext3 /dev/${device}1 -L optdata",
		unless => "lsblk | grep ${device} | grep part",
		require => File["${name}"],
	}

	mount {"${name}":
		atboot => true,
		device => "/dev/${device}1",
		ensure => mounted,
		fstype => "${fstype}",
		options => "defaults",
		dump => "0",
		pass => "1",
		require => [Exec["parted-${device}"], File["${name}"]],
	}
}

define mountephreal (
	$device = "vda",
	$fstype = "ext2",
	$owner = "root",
	$group = "root",
	$mode = "755" ) {

	file {"${name}":
                ensure => directory,
                owner => $owner,
                group => $group,
                mode => $mode,
        }

	mount {"${name}":
		atboot => true,
		device => "/dev/${device}2",
		ensure => mounted,
		fstype => "${fstype}",
		options => "defaults",
		dump => "0",
		pass => "1",
		require => File["${name}"],
	}
}

class oi3-ebs {
	package { ["parted"]:
		ensure => installed,
	}

	if $ebsVolumeUsed {
		createpart {"/opt":
			device => $ebsDeviceName,
			require => Package["parted"],
		}
	} elsif $ebsImageUsed {
		file {"/opt":
                	ensure => directory,
                	owner => "root",
                	group => "root",
                	mode => 755,
        	}
	} else {
		mountephreal {"/opt":
			device => 'vda',
		}
	}
}



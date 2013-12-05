class oi3-bigdatahost::backup {
	file {"/opt/openinfinity/3.0.0/backup/common/bigdata-common":
		ensure => present,
		source => "puppet:///modules/oi3-bigdatahost/bigdata-common",
		owner => 'root',
		group => 'root',
		mode => 0750,
	}

	file {"/opt/openinfinity/3.0.0/backup/cluster-backup-before.d/before-bigdata-cluster-backup":
		ensure => present,
		source => "puppet:///modules/oi3-bigdatahost/before-bigdata-cluster-backup",
		owner => 'root',
		group => 'root',
		mode => 0750,
	}
	
	file {"/opt/openinfinity/3.0.0/backup/cluster-backup-after.d/after-bigdata-cluster-backup":
		ensure => present,
		source => "puppet:///modules/oi3-bigdatahost/after-bigdata-cluster-backup",
		owner => 'root',
		group => 'root',
		mode => 0750,
	}

	file {"/opt/openinfinity/3.0.0/backup/node-backup-before.d/before-bigdata-node-backup":
		ensure => present,
		source => "puppet:///modules/oi3-bigdatahost/before-bigdata-node-backup",
		owner => 'root',
		group => 'root',
		mode => 0750,
	}


	file {"/opt/openinfinity/3.0.0/backup/node-backup-after.d/after-bigdata-node-backup":
		ensure => present,
		source => "puppet:///modules/oi3-bigdatahost/after-bigdata-node-backup",
		owner => 'root',
		group => 'root',
		mode => 0750,
	}

}


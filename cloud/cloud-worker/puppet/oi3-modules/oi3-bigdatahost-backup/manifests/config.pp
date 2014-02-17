class oi3-bigdatahost-backup::config {
	file {"/opt/openinfinity/3.0.0/backup/common/bigdata-common":
		ensure => present,
		source => "puppet:///modules/oi3-bigdatahost-backup/bigdata-common",
		owner => 'root',
		group => 'root',
		mode => 0750,
		require => Class["oi3-backup::install"],
	}

	file {"/opt/openinfinity/3.0.0/backup/cluster-backup-before.d/before-bigdata-cluster-backup":
		ensure => present,
		source => "puppet:///modules/oi3-bigdatahost-backup/before-bigdata-cluster-backup",
		owner => 'root',
		group => 'root',
		mode => 0750,
		require => Class["oi3-backup::install"],
	}
	
	file {"/opt/openinfinity/3.0.0/backup/cluster-backup-after.d/after-bigdata-cluster-backup":
		ensure => present,
		source => "puppet:///modules/oi3-bigdatahost-backup/after-bigdata-cluster-backup",
		owner => 'root',
		group => 'root',
		mode => 0750,
		require => Class["oi3-backup::install"],
	}

	file {"/opt/openinfinity/3.0.0/backup/node-backup-before.d/before-bigdata-node-backup":
		ensure => present,
		source => "puppet:///modules/oi3-bigdatahost-backup/before-bigdata-node-backup",
		owner => 'root',
		group => 'root',
		mode => 0750,
		require => Class["oi3-backup::install"],
	}


	file {"/opt/openinfinity/3.0.0/backup/node-backup-after.d/after-bigdata-node-backup":
		ensure => present,
		source => "puppet:///modules/oi3-bigdatahost-backup/after-bigdata-node-backup",
		owner => 'root',
		group => 'root',
		mode => 0750,
		require => Class["oi3-backup::install"],
	}

}


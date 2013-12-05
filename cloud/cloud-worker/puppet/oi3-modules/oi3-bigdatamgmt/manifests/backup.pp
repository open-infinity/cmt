class oi3-bigdatamgmt::backup {
	file {"/opt/openinfinity/3.0.0/backup/include-dirs.d/bigdata-dirs":
		ensure => present,
		source => "puppet:///modules/oi3-bigdatamgmt/bigdata-dirs",
		owner => 'root',
		group => 'root',
		mode => 0644,
	}
}


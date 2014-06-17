class toasbigdatamgmt::config {
	file {"/root/.ssh/id_rsa":
                ensure => present,
                owner => 'root',
                group => 'root',
                mode => 0600,
                content => template("toasbigdatamgmt/id_rsa.erb"),
		require => File["/root/.ssh"],
        }

	file {"/root/.ssh":
		ensure => directory,
		owner => 'root',
		group => 'root',
		mode => 700,
	}
}

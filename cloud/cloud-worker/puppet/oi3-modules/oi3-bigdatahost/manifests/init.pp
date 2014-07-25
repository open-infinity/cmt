class oi3-bigdatahost::config {
    if (!defined('$hostname')) {
        fail("hostname variable isn't set")
    }

    case $::operatingsystem {
       'RedHat', 'CentOS': {
            file {"/etc/sysconfig/network":
                ensure => present,
                owner => 'root',
                group => 'root',
                mode => 0644,
                content => template("oi3-bigdatahost/network.erb"),
            }
        }
        'Ubuntu': {
            # Change hostname in Ubuntu
            # 1. Replace all occurences of old hostname in /etc/hosts with the new one
            # 2. Change hostname to the new one in /etc/hostname
            # 3. Restart networking to apply changes
            exec { "sed -i \"s/`cat /etc/hostname`/${hostname}/g\" /etc/hosts && echo \"${hostname}\" > /etc/hostname && echo \"Hostname changed! Networking needs a restart.\"":
                path => [ "/bin/", "/sbin/" , "/usr/bin/", "/usr/sbin/" ],
                user => "root",
                # Unless we already have correct hostname
                unless => "cat /etc/hostname | tr -d \"\\n\" | grep -e \"^${hostname}\$\"",
            }
        }
    }
}

class oi3-bigdatahost {
    include oi3-bigdatahost::config
}


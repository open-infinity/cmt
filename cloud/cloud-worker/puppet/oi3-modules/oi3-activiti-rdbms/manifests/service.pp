class oi3-activiti-rdbms::service {
	
	exec { "oi3-create-activiti-db-and-schema":
                unless => "/opt/openinfinity/2.0.0/mariadb/bin/mysql -uroot -p${mysql_password} activiti",
                command => "/opt/openinfinity/2.0.0/mariadb/bin/mysql -uroot -p${mysql_password} -e \"create database activiti; grant all privileges on activiti.* to
                        'activiti'@'%' identified by 'oiactiviti'; flush privileges; use activiti;  source /opt/openinfinity/3.0.0/activiti/dbschema/activiti.mysql.create.engine.sql; source /opt/openinfinity/3.0.0/activiti/dbschema/activiti.mysql.create.history.sql; source /opt/openinfinity/3.0.0/activiti/dbschema/activiti.mysql.create.identity.sql;\"",
		require => Class["oi3-activiti-rdbms::config"],
      }
	
#	exec { "create-activiti-schema":
#		unless => "/opt/openinfinity/3.0.0/mariadb/bin/mysql -ubackup -ptoasbackup",
#		command => "cat /opt/openinfinity/3.0.0/activiti/dbschema/*.sql | /opt/openinfinity/3.0.0/mariadb/bin/mysql -uroot -p${mysql_password} activiti",
#		require => exec["set-mariadb-password"],
#		require => File["/opt/openinfinity/3.0.0/activiti/dbschema/"], # Created by oibackup module		
#	}
	
}

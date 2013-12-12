
insert into cluster_type_tbl (configuration_id, name, title, dependency, replicated, min_machines, max_machines, min_repl_machines, max_repl_machines ) 
values(1, 'ig', 'Identity Gateway', -1, false, 1, 12, null, null);
	
insert into cluster_type_tbl (configuration_id, name, title, dependency, replicated, min_machines, max_machines, min_repl_machines, max_repl_machines ) 
values(1, 'bas', 'BAS Platform', -1, false, 1, 12, null, null);
	
insert into cluster_type_tbl (configuration_id, name, title, dependency, replicated, min_machines, max_machines, min_repl_machines, max_repl_machines ) 
values(1, 'portal', 'Portal Platform', 5, false, 1, 12, null, null);

insert into cluster_type_tbl (configuration_id, name, title, dependency, replicated, min_machines, max_machines, min_repl_machines, max_repl_machines ) 
values(1, 'mq', 'Service Platform', 5, false, 1, 12, null, null);

insert into cluster_type_tbl (configuration_id, name, title, dependency, replicated, min_machines, max_machines, min_repl_machines, max_repl_machines ) 
values(1, 'rdbms', 'Relational Database Management', -1, false, 1, 1, null, null);

insert into cluster_type_tbl (configuration_id, name, title, dependency, replicated, min_machines, max_machines, min_repl_machines, max_repl_machines ) 
values(1, 'nosql', 'NoSQL Repository', -1, true, 6, 12, 3, 10);

insert into cluster_type_tbl (configuration_id, name, title, dependency, replicated, min_machines, max_machines, min_repl_machines, max_repl_machines ) 
values(1, 'bigdata', 'Big Data Repository', -1, true, 7, 12, 3, 10);

insert into cluster_type_tbl (configuration_id, name, title, dependency, replicated, min_machines, max_machines, min_repl_machines, max_repl_machines ) 
values(1, 'ee', 'EE Platform', -1, false, 1, 12, null, null);

insert into cluster_type_tbl (configuration_id, name, title, dependency, replicated, min_machines, max_machines, min_repl_machines, max_repl_machines ) 
values(1, 'ecm', 'Enterprise Content Management', -1, false, 1, 12, null, null);

insert into cloud_provider_tbl (id, name) values(0, 'Amazon');
insert into cloud_provider_tbl (id, name) values(1, 'Eucalyptus');

insert into availability_zone_tbl (id, cloud_id, name) values(1, 0, 'aws-cluster01');
insert into availability_zone_tbl (id, cloud_id, name) values(2, 0, 'aws-cluster02');
insert into availability_zone_tbl (id, cloud_id, name) values(3, 1, 'dev-cluster01');

insert into machine_type_tbl values (0, 'Small', 'Cores: 1, RAM: 1GB, Disk: 10GB', 1, 512, 6);
insert into machine_type_tbl values (1, 'Medium', 'Cores: 2, RAM: 2GB, Disk: 10GB', 1, 1024, 10);
insert into machine_type_tbl values (2, 'Large', 'Cores: 4, RAM: 4GB, Disk: 10GB', 1, 2048, 10);
insert into machine_type_tbl values (3, 'XLarge', 'Cores: 8, RAM: 8GB, Disk: 10GB', 1, 3072, 10);
insert into machine_type_tbl values (4, 'XXLarge', 'Cores: 16, RAM: 16GB, Disk: 10GB', 1, 4096, 10);

insert into cluster_type_tbl (configuration_id, name, title, dependency, replicated, min_machines, max_machines, min_repl_machines, max_repl_machines )
values(1, 'jbossportal', 'JBoss Portal Platform', -1, false, 1, 12, null, null);

insert into cluster_type_tbl (configuration_id, name, title, dependency, replicated, min_machines, max_machines, min_repl_machines, max_repl_machines )
values(1, 'jbossservice', 'JBoss Service Platform', -1, false, 1, 12, null, null);

-- ACCESS CONTROL RULES
-- allow OPPYATOAS to use Eucalyptus only
insert into acl_cloud_provider_tbl values ('OPPYATOAS', 1);
-- allow TOAS to use all providers
insert into acl_cloud_provider_tbl select 'TOAS', id from cloud_provider_tbl;
-- allow Tieto Finland to use all providers
insert into acl_cloud_provider_tbl select 'Tieto Finland', id from cloud_provider_tbl;

-- allow OPPYATOAS to use all zones from euca
insert into acl_availability_zone_tbl select 'OPPYATOAS', id from availability_zone_tbl where cloud_id = 1;
-- allow TOAS to use all zones
insert into acl_availability_zone_tbl select 'TOAS', id from availability_zone_tbl;
-- allow Tieto Finland to use all zones
insert into acl_availability_zone_tbl select 'Tieto Finland', id from availability_zone_tbl;

-- OPPYATOAS can use only jbossportal and jbossservice
insert into acl_cluster_type_tbl select 'OPPYATOAS', id from cluster_type_tbl where name in ('jbossportal', 'jbossservice');
-- allow all platforms except jbossportal and jbossservice
insert into acl_cluster_type_tbl select 'TOAS', id from cluster_type_tbl where name not in ('jbossportal', 'jbossservice');
-- allow all platforms jbossportal and jbossservice
insert into acl_cluster_type_tbl select 'Tieto Finland', id from cluster_type_tbl where name not in ('jbossportal', 'jbossservice');

insert into acl_machine_type_tbl select 'OPPYATOAS', id from machine_type_tbl;
insert into acl_machine_type_tbl select 'TOAS', id from machine_type_tbl;
insert into acl_machine_type_tbl select 'Tieto Finland', id from machine_type_tbl;

-- Configuration template test data
insert into cfg_template_element_tbl values(1, 2, 'bas', '1.2.2', 'Tomcat with TOAS goodies', NULL, 3, 8, 0, NULL, NULL);
insert into cfg_template_tbl values (1, 'TEMPLATE_WEB_SIMPLE', 'Platforms suitable for simple web services');
insert into cfg_template_tbl values (2, 'TEMPLATE_WEB_IG', 'Platforms suitable for web services with authentication support');

insert into cfg_template_organization_tbl values (1, 1);
insert into cfg_template_organization_tbl values (1, 2);
insert into cfg_template_organization_tbl values (1, 3);
insert into cfg_template_organization_tbl values (1, 4);

-- SSP billing test data
insert into usage_hours_tbl values(1, 10687, 1513, 5, 3532, 1, '2013-10-17 10:30:01');
insert into usage_hours_tbl values(2, 10687, 1513, 5, 3532, 3, '2013-10-17 10:35:01');
insert into machine_tbl (machine_id, machine_instance_id, project_id, machine_name, machine_dns_name, machine_key, active,
machine_username, machine_running, machine_state, machine_cluster_id, machine_private_dns_name, machine_type, machine_configured,
machine_last_update, machine_cloud_type, machine_extra_ebs_volume_id, machine_extra_ebs_volume_device, machine_extra_ebs_volume_size)
values(3532, 'machine1', '0', 'BAS platform', '127.0.0.1', '0', '1', 'root', '1', 'running', '1', 'dns', 'loadbalancer',
'3', '2013-10-01 13:03:47', '1', 'NULL', 'NULL', '0');

insert into cluster_tbl (cluster_id, cluster_type) values(1, '0');






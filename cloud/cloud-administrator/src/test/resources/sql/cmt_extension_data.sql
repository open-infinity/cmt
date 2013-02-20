
insert into cloud_provider_tbl (id, name) values(0, 'Amazon');
insert into cloud_provider_tbl (id, name) values(1, 'Eucalyptus');

insert into availability_zone_tbl (id, cloud_id, name) values(1, 0, 'aws-cluster01');
insert into availability_zone_tbl (id, cloud_id, name) values(2, 0, 'aws-cluster01');
insert into availability_zone_tbl (id, cloud_id, name) values(3, 1, 'dev-cluster01');

insert into machine_type_tbl values (0, 'Small', 'Cores: 1, RAM: 1GB, Disk: 10GB');
insert into machine_type_tbl values (1, 'Medium', 'Cores: 2, RAM: 2GB, Disk: 10GB');
insert into machine_type_tbl values (2, 'Large', 'Cores: 4, RAM: 4GB, Disk: 10GB');
insert into machine_type_tbl values (3, 'XLarge', 'Cores: 8, RAM: 8GB, Disk: 10GB');
insert into machine_type_tbl values (4, 'XXLarge', 'Cores: 16, RAM: 16GB, Disk: 10GB');

insert into cluster_type_tbl (configuration_id, name, title, dependency, replicated, min_machines, max_machines, min_repl_machines, max_repl_machines )
values(1, 'jbossportal', 'JBoss Portal Platform', -1, false, 1, 12, null, null);

insert into cluster_type_tbl (configuration_id, name, title, dependency, replicated, min_machines, max_machines, min_repl_machines, max_repl_machines )
values(1, 'jbossservice', 'JBoss Service Platform', -1, false, 1, 12, null, null);

insert into acl_cloud_provider_tbl values ('OPPYATOAS', 1);
insert into acl_cloud_provider_tbl select 'TOAS', id from cloud_provider_tbl;
insert into acl_cloud_provider_tbl select 'Tieto Finland', id from cloud_provider_tbl;

insert into acl_availability_zone_tbl select 'OPPYATOAS', id from availability_zone_tbl where cloud_id = 1;
insert into acl_availability_zone_tbl select 'TOAS', id from availability_zone_tbl;
insert into acl_availability_zone_tbl select 'Tieto Finland', id from availability_zone_tbl;

insert into acl_cluster_type_tbl select 'OPPYATOAS', id from cluster_type_tbl where name in ('jbossportal', 'jbossservice');
insert into acl_cluster_type_tbl select 'TOAS', id from cluster_type_tbl where name not in ('jbossportal', 'jbossservice');
insert into acl_cluster_type_tbl select 'Tieto Finland', id from cluster_type_tbl where name not in ('jbossportal', 'jbossservice');

insert into acl_machine_type_tbl select 'OPPYATOAS', id from machine_type_tbl;
insert into acl_machine_type_tbl select 'TOAS', id from machine_type_tbl;
insert into acl_machine_type_tbl select 'Tieto Finland', id from machine_type_tbl;

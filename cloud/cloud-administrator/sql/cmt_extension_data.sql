
insert into cloud_provider_tbl (id, name) values(0, 'Amazon');
insert into cloud_provider_tbl (id, name) values(1, 'Eucalyptus');

insert into availability_zone_tbl (id, cloud_id, name) value(1, 0, "aws-cluster01");
insert into availability_zone_tbl (id, cloud_id, name) value(2, 0, "aws-cluster01");
insert into availability_zone_tbl (id, cloud_id, name) value(3, 1, "dev-cluster01");

insert into machine_type_tbl values (0, "Small", "Cores: 1, RAM: 1GB, Disk: 10GB");
insert into machine_type_tbl values (1, "Medium", "Cores: 2, RAM: 2GB, Disk: 10GB");
insert into machine_type_tbl values (2, "Large", "Cores: 4, RAM: 4GB, Disk: 10GB");
insert into machine_type_tbl values (3, "XLarge", "Cores: 8, RAM: 8GB, Disk: 10GB");
insert into machine_type_tbl values (4, "XXLarge", "Cores: 16, RAM: 16GB, Disk: 10GB");

insert into cluster_type_tbl (configuration_id, name, title, dependency, replicated, min_machines, max_machines, min_repl_machines, max_repl_machines )
values(1, 'yaportal', 'YA Portal Platform', -1, false, 1, 12, null, null);

insert into cluster_type_tbl (configuration_id, name, title, dependency, replicated, min_machines, max_machines, min_repl_machines, max_repl_machines )
values(1, 'yaservice', 'YA Service Platform', -1, false, 1, 12, null, null);

insert into acl_machine_type_tbl values ('Project X', 0);
insert into acl_machine_type_tbl values ('Project X', 1);
insert into acl_machine_type_tbl values ('Project X', 2);
insert into acl_machine_type_tbl values ('Project X', 3);
insert into acl_machine_type_tbl values ('Project X', 4);

insert into acl_machine_type_tbl values ('TOAS', 0);
insert into acl_machine_type_tbl values ('TOAS', 1);
insert into acl_machine_type_tbl values ('TOAS', 2);
insert into acl_machine_type_tbl values ('TOAS', 3);
insert into acl_machine_type_tbl values ('TOAS', 4);

insert into acl_cloud_provider_tbl ('Project X', 1);
insert into acl_cloud_provider_tbl ('TOAS', 0);
insert into acl_cloud_provider_tbl ('TOAS', 1);

insert into acl_cluster_type_tbl select 'Project X', id from cluster_type_tbl where name in ('yaportal', 'yaservice');
insert into acl_cluster_type_tbl select 'TOAS', id from cluster_type_tbl where name not in ('yaportal', 'yaservice');

insert into acl_availability_zone_tbl values ('TOAS', 1);
insert into acl_availability_zone_tbl values ('TOAS', 2);
insert into acl_availability_zone_tbl values ('TOAS', 3);
insert into acl_availability_zone_tbl values ('Project X', 3);



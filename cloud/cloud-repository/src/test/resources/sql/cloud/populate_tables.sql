
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

insert into acl_cloud_provider_tbl select 'TOAS', id from cloud_provider_tbl;
insert into acl_cloud_provider_tbl select 'Tieto Finland', id from cloud_provider_tbl;

insert into acl_availability_zone_tbl select 'TOAS', id from availability_zone_tbl;
insert into acl_availability_zone_tbl select 'Tieto Finland', id from availability_zone_tbl;

insert into acl_cluster_type_tbl select 'TOAS', id from cluster_type_tbl;
insert into acl_cluster_type_tbl select 'Tieto Finland', id from cluster_type_tbl;

insert into acl_machine_type_tbl select 'TOAS', id from machine_type_tbl;
insert into acl_machine_type_tbl select 'Tieto Finland', id from machine_type_tbl;

insert into configuration_template_tbl values (1, 'TEMPLATE_uno', 'Blablablabla, wooosh! aksjdhc');
insert into configuration_template_tbl values (2, 'TEMPLATE_zwei', 'blablablabla saoieufölkj');
insert into configuration_template_tbl values (3, 'TEMPLATE_tre', 'oink! asöclkaöslk');
insert into configuration_template_tbl values (4, 'TEMPLATE_four', 'dsföaslkfdölskfölsakdfölas');

insert into configuration_template_organization_tbl values (10687, 1);
insert into configuration_template_organization_tbl values (10687, 2);
insert into configuration_template_organization_tbl values (10687, 3);
insert into configuration_template_organization_tbl values (10687, 4);

insert into configuration_element_tbl values (1, 1, 'ig', '1.2.2', 'Identity Gateway', 0, 1, 20, 0, NULL, NULL);
insert into configuration_element_tbl values (2, 2, 'bas', '1.2.2', 'BAS Platform', 0, 1, 20, 0, NULL, NULL);
insert into configuration_element_tbl values (3, 3, 'portal', '1.2.2', 'Portal Platform', 0, 1, 20, 0, NULL, NULL);
insert into configuration_element_tbl values (4, 4, 'mq', '1.2.2', 'Service Platform', 0, 1, 20, 0, NULL, NULL);
insert into configuration_element_tbl values (5, 5, 'rdbms', '1.2.2', 'Relational Database Management', 0, 1, 1, 0, NULL, NULL);
insert into configuration_element_tbl values (6, 6, 'nosql', '1.2.2', 'NoSQL Repository', 0, 6, 20, 1, 3, 10);
insert into configuration_element_tbl values (7, 7, 'bigdata', '1.2.2', 'Big Data Repository', 0, 7, 20, 1, 3, 10);
insert into configuration_element_tbl values (8, 8, 'ee', '1.2.2', 'EE Platform', 0, 1, 20, 0, NULL, NULL);
insert into configuration_element_tbl values (9, 9, 'ecm', '1.2.2', 'Enterprise Content Management', 0, 1, 20, 0, NULL, NULL);

insert into configuration_template_element_tbl values (1, 1);
insert into configuration_template_element_tbl values (1, 2);
insert into configuration_template_element_tbl values (1, 3);
insert into configuration_template_element_tbl values (1, 4);
insert into configuration_template_element_tbl values (1, 5);
insert into configuration_template_element_tbl values (1, 6);
insert into configuration_template_element_tbl values (1, 7);
insert into configuration_template_element_tbl values (1, 8);
insert into configuration_template_element_tbl values (1, 9);
insert into configuration_template_element_tbl values (2, 1);
insert into configuration_template_element_tbl values (2, 2);















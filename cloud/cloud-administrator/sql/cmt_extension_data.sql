insert into cluster_type_tbl (configuration_id, name, title, dependency, replicated, min_machines, max_machines, min_repl_machines, max_repl_machines ) 
values(1, 'yamq', 'YA Service Platform', -1, false, 1, 12, null, null);

insert into cluster_type_tbl (configuration_id, name, title, dependency, replicated, min_machines, max_machines, min_repl_machines, max_repl_machines ) 
values(1, 'yaportal', 'YA Portal Platform', -1, false, 1, 12, null, null);

insert into cloud_provider_tbl (id, name) values(0, 'Amazon');
insert into cloud_provider_tbl (id, name) values(1, 'Eucalyptus');

insert into availability_zone_tbl (id, cloud_id, name) value(1, 0, "aws-cluster01");
insert into availability_zone_tbl (id, cloud_id, name) value(2, 0, "aws-cluster01");
insert into availability_zone_tbl (id, cloud_id, name) value(3, 1, "dev-cluster01");

insert into acl_cloud_provider_tbl (org_name, cloud_id) value ('OPPYATOAS', 1);

insert into acl_availability_zone_tbl (org_name, zone_id) value ('OPPYATOAS', 3);

insert into acl_cluster_type_tbl (`org_name`,`cluster_id`) select 'Cloud Admin', id from cluster_type_tbl;
insert into acl_cluster_type_tbl (`org_name`,`cluster_id`) select 'OPPYATOAS', id from cluster_type_tbl where name in ('yamq','yaportal');


insert into cloud_provider_tbl (id, name) values(0, 'Amazon');
insert into cloud_provider_tbl (id, name) values(1, 'Eucalyptus');

insert into availability_zone_tbl (id, cloud_id, name) value(1, 0, "aws-cluster01");
insert into availability_zone_tbl (id, cloud_id, name) value(2, 0, "aws-cluster01");
insert into availability_zone_tbl (id, cloud_id, name) value(3, 1, "dev-cluster01");

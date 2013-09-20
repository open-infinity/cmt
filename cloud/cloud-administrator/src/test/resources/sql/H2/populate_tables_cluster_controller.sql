insert into instance_tbl(instance_id, user_id, instance_name, cloud_type, cloud_zone, organization_id, instance_status, instance_active)
values(1, 1, 'TEST_INSTANCE', 1, 'dev-pilvi1', 1, 'Running', 1);


insert into cluster_tbl
(
  cluster_id,
  cluster_name,
  cluster_number_of_machines,
  cluster_lb_name,
  cluster_lb_dns,
  instance_id,
  cluster_type,
  cluster_pub,
  cluster_live,
  cluster_lb_instance_id,
  cluster_security_group_id,
  cluster_security_group_name,
  cluster_multicast_address,
  cluster_machine_type,
  cluster_ebs_image_used,
  cluster_ebs_volumes_used
 )
values(1, 'BAS platform', 10, 'bas-lb', '10.33.208.188', 1, 5, 1, 1, 'lb_instance', 'NULL', 'TOASinstance1', '224.2.1.9', 1, 0, 1);             
		

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

insert into machine_type_tbl values (0, 'Small', 'Cores: 1, RAM: 1GB, Disk: 10GB');
insert into machine_type_tbl values (1, 'Medium', 'Cores: 2, RAM: 2GB, Disk: 10GB');
insert into machine_type_tbl values (2, 'Large', 'Cores: 4, RAM: 4GB, Disk: 10GB');
insert into machine_type_tbl values (3, 'XLarge', 'Cores: 8, RAM: 8GB, Disk: 10GB');
insert into machine_type_tbl values (4, 'XXLarge', 'Cores: 16, RAM: 16GB, Disk: 10GB');

insert into acl_cloud_provider_tbl select 'TOAS', id from cloud_provider_tbl;
insert into acl_cloud_provider_tbl select 'Tieto Finland', id from cloud_provider_tbl;

insert into acl_availability_zone_tbl select 'TOAS', id from availability_zone_tbl;
insert into acl_availability_zone_tbl select 'Tieto Finland', id from availability_zone_tbl;

insert into acl_cluster_type_tbl select 'TOAS', id from cluster_type_tbl;
insert into acl_cluster_type_tbl select 'Tieto Finland', id from cluster_type_tbl;

insert into acl_machine_type_tbl select 'TOAS', id from machine_type_tbl;
insert into acl_machine_type_tbl select 'Tieto Finland', id from machine_type_tbl;

INSERT INTO cluster_tbl (cluster_id,cluster_name,cluster_number_of_machines,cluster_lb_name,cluster_lb_dns,instance_id,cluster_type,cluster_pub,cluster_live,cluster_lb_instance_id,cluster_security_group_id,cluster_security_group_name,cluster_multicast_address,cluster_machine_type,cluster_ebs_image_used,cluster_ebs_volumes_used) VALUES (1106,'Service platform',1,'service-lb','10.33.208.107',720,1,2,1,'i-BB9445E2',null,'C1106','224.2.1.6',1,0,0);
INSERT INTO cluster_tbl (cluster_id,cluster_name,cluster_number_of_machines,cluster_lb_name,cluster_lb_dns,instance_id,cluster_type,cluster_pub,cluster_live,cluster_lb_instance_id,cluster_security_group_id,cluster_security_group_name,cluster_multicast_address,cluster_machine_type,cluster_ebs_image_used,cluster_ebs_volumes_used) VALUES (1107,'Portal platform',3,'service-lb','10.33.208.107',720,1,2,1,'i-BB9445E2',null,'C1106','224.2.1.6',2,1,99);

INSERT INTO machine_tbl (machine_id,machine_instance_id,project_id,machine_name,machine_dns_name,machine_key,active,machine_username,machine_running,machine_state,machine_cluster_id,machine_private_dns_name,machine_type,machine_configured,machine_last_update,machine_cloud_type,machine_extra_ebs_volume_id,machine_extra_ebs_volume_device,machine_extra_ebs_volume_size) VALUES (38,'i-50D03FF4',0,'JBoss Service platform','10.33.208.102',0,1,'root',1,'running',1106,'10.99.186.61','loadbalancer',3,{ts '2013-06-26 11:17:24.'},1,null,null,0);
INSERT INTO machine_tbl (machine_id,machine_instance_id,project_id,machine_name,machine_dns_name,machine_key,active,machine_username,machine_running,machine_state,machine_cluster_id,machine_private_dns_name,machine_type,machine_configured,machine_last_update,machine_cloud_type,machine_extra_ebs_volume_id,machine_extra_ebs_volume_device,machine_extra_ebs_volume_size) VALUES (48,'i-65833FF9',0,'JBoss Service platform','10.33.208.103',0,1,'root',1,'running',1107,'10.99.186.54','clustermember',3,{ts '2013-06-26 11:21:54.'},1,null,null,0);

INSERT INTO usage_hours_tbl (id,organization_id,instance_id,cluster_id,platform_id,machine_id,state,cur_timestamp,cluster_type_title,machine_type_id,machine_type_name,machine_type_spec) VALUES (1,10495,33,2,'10','1',1,{ts '2013-06-04 14:25:41.'},'',0,'','');
INSERT INTO usage_hours_tbl (id,organization_id,instance_id,cluster_id,platform_id,machine_id,state,cur_timestamp,cluster_type_title,machine_type_id,machine_type_name,machine_type_spec) VALUES (2,10495,33,2,'10','2',1,{ts '2013-06-04 14:25:43.'},'',0,'','');
INSERT INTO usage_hours_tbl (id,organization_id,instance_id,cluster_id,platform_id,machine_id,state,cur_timestamp,cluster_type_title,machine_type_id,machine_type_name,machine_type_spec) VALUES (3,10495,33,2,'10','1',3,{ts '2013-06-04 14:44:25.'},'',0,'','');
INSERT INTO usage_hours_tbl (id,organization_id,instance_id,cluster_id,platform_id,machine_id,state,cur_timestamp,cluster_type_title,machine_type_id,machine_type_name,machine_type_spec) VALUES (4,10495,33,2,'10','2',3,{ts '2013-06-04 14:44:25.'},'',0,'','');
INSERT INTO usage_hours_tbl (id,organization_id,instance_id,cluster_id,platform_id,machine_id,state,cur_timestamp,cluster_type_title,machine_type_id,machine_type_name,machine_type_spec) VALUES (5,10495,33,3,'10','3',1,{ts '2013-06-04 14:46:21.'},'',0,'','');
INSERT INTO usage_hours_tbl (id,organization_id,instance_id,cluster_id,platform_id,machine_id,state,cur_timestamp,cluster_type_title,machine_type_id,machine_type_name,machine_type_spec) VALUES (6,10495,33,3,'10','4',1,{ts '2013-06-04 14:46:23.'},'',0,'','');
INSERT INTO usage_hours_tbl (id,organization_id,instance_id,cluster_id,platform_id,machine_id,state,cur_timestamp,cluster_type_title,machine_type_id,machine_type_name,machine_type_spec) VALUES (7,10495,33,3,'10','3',3,{ts '2013-06-04 15:21:47.'},'',0,'','');
INSERT INTO usage_hours_tbl (id,organization_id,instance_id,cluster_id,platform_id,machine_id,state,cur_timestamp,cluster_type_title,machine_type_id,machine_type_name,machine_type_spec) VALUES (8,10495,33,3,'10','4',3,{ts '2013-06-04 15:21:47.'},'',0,'','');
INSERT INTO usage_hours_tbl (id,organization_id,instance_id,cluster_id,platform_id,machine_id,state,cur_timestamp,cluster_type_title,machine_type_id,machine_type_name,machine_type_spec) VALUES (9,10495,33,4,'10','5',1,{ts '2013-06-04 15:25:53.'},'',0,'','');
INSERT INTO usage_hours_tbl (id,organization_id,instance_id,cluster_id,platform_id,machine_id,state,cur_timestamp,cluster_type_title,machine_type_id,machine_type_name,machine_type_spec) VALUES (10,10495,33,4,'10','6',1,{ts '2013-06-04 15:25:55.'},'',0,'','');
INSERT INTO usage_hours_tbl (id,organization_id,instance_id,cluster_id,platform_id,machine_id,state,cur_timestamp,cluster_type_title,machine_type_id,machine_type_name,machine_type_spec) VALUES (11,10495,33,4,'10','5',3,{ts '2013-06-05 10:10:25.'},'',0,'','');
INSERT INTO usage_hours_tbl (id,organization_id,instance_id,cluster_id,platform_id,machine_id,state,cur_timestamp,cluster_type_title,machine_type_id,machine_type_name,machine_type_spec) VALUES (12,10495,33,4,'10','6',3,{ts '2013-06-05 10:10:25.'},'',0,'','');
INSERT INTO usage_hours_tbl (id,organization_id,instance_id,cluster_id,platform_id,machine_id,state,cur_timestamp,cluster_type_title,machine_type_id,machine_type_name,machine_type_spec) VALUES (13,10495,33,5,'10','7',1,{ts '2013-06-05 10:11:01.'},'',0,'','');
INSERT INTO usage_hours_tbl (id,organization_id,instance_id,cluster_id,platform_id,machine_id,state,cur_timestamp,cluster_type_title,machine_type_id,machine_type_name,machine_type_spec) VALUES (14,10495,33,5,'10','8',1,{ts '2013-06-05 10:11:03.'},'',0,'','');
INSERT INTO usage_hours_tbl (id,organization_id,instance_id,cluster_id,platform_id,machine_id,state,cur_timestamp,cluster_type_title,machine_type_id,machine_type_name,machine_type_spec) VALUES (15,10495,33,6,'10','9',1,{ts '2013-06-05 13:58:41.'},'',0,'','');
INSERT INTO usage_hours_tbl (id,organization_id,instance_id,cluster_id,platform_id,machine_id,state,cur_timestamp,cluster_type_title,machine_type_id,machine_type_name,machine_type_spec) VALUES (16,10495,33,6,'10','10',1,{ts '2013-06-05 13:58:43.'},'',0,'','');
INSERT INTO usage_hours_tbl (id,organization_id,instance_id,cluster_id,platform_id,machine_id,state,cur_timestamp,cluster_type_title,machine_type_id,machine_type_name,machine_type_spec) VALUES (17,10495,33,7,'10','11',1,{ts '2013-06-05 15:50:25.'},'',0,'','');
INSERT INTO usage_hours_tbl (id,organization_id,instance_id,cluster_id,platform_id,machine_id,state,cur_timestamp,cluster_type_title,machine_type_id,machine_type_name,machine_type_spec) VALUES (18,10495,33,7,'10','12',1,{ts '2013-06-05 15:50:27.'},'',0,'','');
INSERT INTO usage_hours_tbl (id,organization_id,instance_id,cluster_id,platform_id,machine_id,state,cur_timestamp,cluster_type_title,machine_type_id,machine_type_name,machine_type_spec) VALUES (19,10495,33,7,'10','11',3,{ts '2013-06-05 17:07:32.'},'',0,'','');
INSERT INTO usage_hours_tbl (id,organization_id,instance_id,cluster_id,platform_id,machine_id,state,cur_timestamp,cluster_type_title,machine_type_id,machine_type_name,machine_type_spec) VALUES (20,10495,33,6,'10','9',3,{ts '2013-06-05 17:07:32.'},'',0,'','');
INSERT INTO usage_hours_tbl (id,organization_id,instance_id,cluster_id,platform_id,machine_id,state,cur_timestamp,cluster_type_title,machine_type_id,machine_type_name,machine_type_spec) VALUES (21,10495,33,7,'10','12',3,{ts '2013-06-05 17:07:32.'},'',0,'','');
INSERT INTO usage_hours_tbl (id,organization_id,instance_id,cluster_id,platform_id,machine_id,state,cur_timestamp,cluster_type_title,machine_type_id,machine_type_name,machine_type_spec) VALUES (22,10495,33,6,'10','10',3,{ts '2013-06-05 17:07:32.'},'',0,'','');
INSERT INTO usage_hours_tbl (id,organization_id,instance_id,cluster_id,platform_id,machine_id,state,cur_timestamp,cluster_type_title,machine_type_id,machine_type_name,machine_type_spec) VALUES (23,10495,33,8,'11','13',1,{ts '2013-06-06 12:52:35.'},'',0,'','');
INSERT INTO usage_hours_tbl (id,organization_id,instance_id,cluster_id,platform_id,machine_id,state,cur_timestamp,cluster_type_title,machine_type_id,machine_type_name,machine_type_spec) VALUES (24,10495,33,8,'11','14',1,{ts '2013-06-06 12:52:37.'},'',0,'','');
INSERT INTO usage_hours_tbl (id,organization_id,instance_id,cluster_id,platform_id,machine_id,state,cur_timestamp,cluster_type_title,machine_type_id,machine_type_name,machine_type_spec) VALUES (25,10495,33,9,'10','15',1,{ts '2013-06-06 13:19:45.'},'',0,'','');
INSERT INTO usage_hours_tbl (id,organization_id,instance_id,cluster_id,platform_id,machine_id,state,cur_timestamp,cluster_type_title,machine_type_id,machine_type_name,machine_type_spec) VALUES (26,10495,33,9,'10','16',1,{ts '2013-06-06 13:19:47.'},'',0,'','');
INSERT INTO usage_hours_tbl (id,organization_id,instance_id,cluster_id,platform_id,machine_id,state,cur_timestamp,cluster_type_title,machine_type_id,machine_type_name,machine_type_spec) VALUES (27,10495,33,9,'10','15',3,{ts '2013-06-06 14:12:31.'},'',0,'','');
INSERT INTO usage_hours_tbl (id,organization_id,instance_id,cluster_id,platform_id,machine_id,state,cur_timestamp,cluster_type_title,machine_type_id,machine_type_name,machine_type_spec) VALUES (28,10495,33,9,'10','16',3,{ts '2013-06-06 14:12:31.'},'',0,'','');
INSERT INTO usage_hours_tbl (id,organization_id,instance_id,cluster_id,platform_id,machine_id,state,cur_timestamp,cluster_type_title,machine_type_id,machine_type_name,machine_type_spec) VALUES (29,10495,33,10,'10','17',1,{ts '2013-06-06 14:12:56.'},'',0,'','');
INSERT INTO usage_hours_tbl (id,organization_id,instance_id,cluster_id,platform_id,machine_id,state,cur_timestamp,cluster_type_title,machine_type_id,machine_type_name,machine_type_spec) VALUES (30,10495,33,10,'10','18',1,{ts '2013-06-06 14:12:58.'},'',0,'','');
INSERT INTO usage_hours_tbl (id,organization_id,instance_id,cluster_id,platform_id,machine_id,state,cur_timestamp,cluster_type_title,machine_type_id,machine_type_name,machine_type_spec) VALUES (31,10495,33,10,'10','17',3,{ts '2013-06-06 14:24:51.'},'',0,'','');
INSERT INTO usage_hours_tbl (id,organization_id,instance_id,cluster_id,platform_id,machine_id,state,cur_timestamp,cluster_type_title,machine_type_id,machine_type_name,machine_type_spec) VALUES (32,10495,33,10,'10','18',3,{ts '2013-06-06 14:24:51.'},'',0,'','');
INSERT INTO usage_hours_tbl (id,organization_id,instance_id,cluster_id,platform_id,machine_id,state,cur_timestamp,cluster_type_title,machine_type_id,machine_type_name,machine_type_spec) VALUES (33,10495,33,11,'10','19',1,{ts '2013-06-06 14:25:37.'},'',0,'','');
INSERT INTO usage_hours_tbl (id,organization_id,instance_id,cluster_id,platform_id,machine_id,state,cur_timestamp,cluster_type_title,machine_type_id,machine_type_name,machine_type_spec) VALUES (34,10495,33,11,'10','20',1,{ts '2013-06-06 14:25:39.'},'',0,'','');
INSERT INTO usage_hours_tbl (id,organization_id,instance_id,cluster_id,platform_id,machine_id,state,cur_timestamp,cluster_type_title,machine_type_id,machine_type_name,machine_type_spec) VALUES (35,10495,33,5,'10','7',3,{ts '2013-06-06 14:43:22.'},'',0,'','');
INSERT INTO usage_hours_tbl (id,organization_id,instance_id,cluster_id,platform_id,machine_id,state,cur_timestamp,cluster_type_title,machine_type_id,machine_type_name,machine_type_spec) VALUES (36,10495,33,5,'10','8',3,{ts '2013-06-06 14:43:22.'},'',0,'','');
INSERT INTO usage_hours_tbl (id,organization_id,instance_id,cluster_id,platform_id,machine_id,state,cur_timestamp,cluster_type_title,machine_type_id,machine_type_name,machine_type_spec) VALUES (37,10495,33,8,'11','13',3,{ts '2013-06-06 14:43:32.'},'',0,'','');
INSERT INTO usage_hours_tbl (id,organization_id,instance_id,cluster_id,platform_id,machine_id,state,cur_timestamp,cluster_type_title,machine_type_id,machine_type_name,machine_type_spec) VALUES (38,10495,33,8,'11','14',3,{ts '2013-06-06 14:43:32.'},'',0,'','');
INSERT INTO usage_hours_tbl (id,organization_id,instance_id,cluster_id,platform_id,machine_id,state,cur_timestamp,cluster_type_title,machine_type_id,machine_type_name,machine_type_spec) VALUES (39,10495,33,12,'11','21',1,{ts '2013-06-06 14:44:18.'},'',0,'','');
INSERT INTO usage_hours_tbl (id,organization_id,instance_id,cluster_id,platform_id,machine_id,state,cur_timestamp,cluster_type_title,machine_type_id,machine_type_name,machine_type_spec) VALUES (40,10495,33,12,'11','22',1,{ts '2013-06-06 14:44:20.'},'',0,'','');
INSERT INTO usage_hours_tbl (id,organization_id,instance_id,cluster_id,platform_id,machine_id,state,cur_timestamp,cluster_type_title,machine_type_id,machine_type_name,machine_type_spec) VALUES (41,10495,33,11,'10','19',3,{ts '2013-06-06 14:58:02.'},'',0,'','');
INSERT INTO usage_hours_tbl (id,organization_id,instance_id,cluster_id,platform_id,machine_id,state,cur_timestamp,cluster_type_title,machine_type_id,machine_type_name,machine_type_spec) VALUES (42,10495,33,11,'10','20',3,{ts '2013-06-06 14:58:02.'},'',0,'','');
INSERT INTO usage_hours_tbl (id,organization_id,instance_id,cluster_id,platform_id,machine_id,state,cur_timestamp,cluster_type_title,machine_type_id,machine_type_name,machine_type_spec) VALUES (43,10495,33,13,'10','23',1,{ts '2013-06-06 15:00:29.'},'',0,'','');
INSERT INTO usage_hours_tbl (id,organization_id,instance_id,cluster_id,platform_id,machine_id,state,cur_timestamp,cluster_type_title,machine_type_id,machine_type_name,machine_type_spec) VALUES (44,10495,33,13,'10','24',1,{ts '2013-06-06 15:00:31.'},'',0,'','');
INSERT INTO usage_hours_tbl (id,organization_id,instance_id,cluster_id,platform_id,machine_id,state,cur_timestamp,cluster_type_title,machine_type_id,machine_type_name,machine_type_spec) VALUES (45,10495,33,13,'10','23',3,{ts '2013-06-06 15:22:53.'},'',0,'','');
INSERT INTO usage_hours_tbl (id,organization_id,instance_id,cluster_id,platform_id,machine_id,state,cur_timestamp,cluster_type_title,machine_type_id,machine_type_name,machine_type_spec) VALUES (46,10495,33,13,'10','24',3,{ts '2013-06-06 15:22:53.'},'',0,'','');
INSERT INTO usage_hours_tbl (id,organization_id,instance_id,cluster_id,platform_id,machine_id,state,cur_timestamp,cluster_type_title,machine_type_id,machine_type_name,machine_type_spec) VALUES (47,10495,33,14,'10','25',1,{ts '2013-06-06 15:24:19.'},'',0,'','');
INSERT INTO usage_hours_tbl (id,organization_id,instance_id,cluster_id,platform_id,machine_id,state,cur_timestamp,cluster_type_title,machine_type_id,machine_type_name,machine_type_spec) VALUES (48,10495,33,14,'10','26',1,{ts '2013-06-06 15:24:21.'},'',0,'','');
INSERT INTO usage_hours_tbl (id,organization_id,instance_id,cluster_id,platform_id,machine_id,state,cur_timestamp,cluster_type_title,machine_type_id,machine_type_name,machine_type_spec) VALUES (49,10495,33,14,'10','25',3,{ts '2013-06-06 15:59:34.'},'',0,'','');
INSERT INTO usage_hours_tbl (id,organization_id,instance_id,cluster_id,platform_id,machine_id,state,cur_timestamp,cluster_type_title,machine_type_id,machine_type_name,machine_type_spec) VALUES (50,10495,33,14,'10','26',3,{ts '2013-06-06 15:59:34.'},'',0,'','');
INSERT INTO usage_hours_tbl (id,organization_id,instance_id,cluster_id,platform_id,machine_id,state,cur_timestamp,cluster_type_title,machine_type_id,machine_type_name,machine_type_spec) VALUES (51,10495,33,15,'10','27',1,{ts '2013-06-06 16:00:40.'},'',0,'','');
INSERT INTO usage_hours_tbl (id,organization_id,instance_id,cluster_id,platform_id,machine_id,state,cur_timestamp,cluster_type_title,machine_type_id,machine_type_name,machine_type_spec) VALUES (52,10495,33,15,'10','28',1,{ts '2013-06-06 16:00:42.'},'',0,'','');
INSERT INTO usage_hours_tbl (id,organization_id,instance_id,cluster_id,platform_id,machine_id,state,cur_timestamp,cluster_type_title,machine_type_id,machine_type_name,machine_type_spec) VALUES (53,10495,33,15,'10','27',3,{ts '2013-06-06 16:16:14.'},'',0,'','');
INSERT INTO usage_hours_tbl (id,organization_id,instance_id,cluster_id,platform_id,machine_id,state,cur_timestamp,cluster_type_title,machine_type_id,machine_type_name,machine_type_spec) VALUES (54,10495,33,15,'10','28',3,{ts '2013-06-06 16:16:15.'},'',0,'','');
INSERT INTO usage_hours_tbl (id,organization_id,instance_id,cluster_id,platform_id,machine_id,state,cur_timestamp,cluster_type_title,machine_type_id,machine_type_name,machine_type_spec) VALUES (55,10495,33,16,'10','29',1,{ts '2013-06-07 12:16:17.'},'',0,'','');
INSERT INTO usage_hours_tbl (id,organization_id,instance_id,cluster_id,platform_id,machine_id,state,cur_timestamp,cluster_type_title,machine_type_id,machine_type_name,machine_type_spec) VALUES (56,10495,33,16,'10','30',1,{ts '2013-06-07 12:16:19.'},'',0,'','');
INSERT INTO usage_hours_tbl (id,organization_id,instance_id,cluster_id,platform_id,machine_id,state,cur_timestamp,cluster_type_title,machine_type_id,machine_type_name,machine_type_spec) VALUES (57,10495,33,16,'10','29',3,{ts '2013-06-07 13:04:32.'},'',0,'','');
INSERT INTO usage_hours_tbl (id,organization_id,instance_id,cluster_id,platform_id,machine_id,state,cur_timestamp,cluster_type_title,machine_type_id,machine_type_name,machine_type_spec) VALUES (58,10495,33,16,'10','30',3,{ts '2013-06-07 13:04:32.'},'',0,'','');
INSERT INTO usage_hours_tbl (id,organization_id,instance_id,cluster_id,platform_id,machine_id,state,cur_timestamp,cluster_type_title,machine_type_id,machine_type_name,machine_type_spec) VALUES (59,10495,33,17,'10','31',1,{ts '2013-06-07 13:06:18.'},'',0,'','');
INSERT INTO usage_hours_tbl (id,organization_id,instance_id,cluster_id,platform_id,machine_id,state,cur_timestamp,cluster_type_title,machine_type_id,machine_type_name,machine_type_spec) VALUES (60,10495,33,17,'10','32',1,{ts '2013-06-07 13:06:20.'},'',0,'','');
INSERT INTO usage_hours_tbl (id,organization_id,instance_id,cluster_id,platform_id,machine_id,state,cur_timestamp,cluster_type_title,machine_type_id,machine_type_name,machine_type_spec) VALUES (61,10495,33,12,'11','21',3,{ts '2013-06-07 14:57:35.'},'',0,'','');
INSERT INTO usage_hours_tbl (id,organization_id,instance_id,cluster_id,platform_id,machine_id,state,cur_timestamp,cluster_type_title,machine_type_id,machine_type_name,machine_type_spec) VALUES (62,10495,33,12,'11','22',3,{ts '2013-06-07 14:57:35.'},'',0,'','');
INSERT INTO usage_hours_tbl (id,organization_id,instance_id,cluster_id,platform_id,machine_id,state,cur_timestamp,cluster_type_title,machine_type_id,machine_type_name,machine_type_spec) VALUES (63,10495,33,18,'11','33',1,{ts '2013-06-07 15:05:21.'},'',0,'','');
INSERT INTO usage_hours_tbl (id,organization_id,instance_id,cluster_id,platform_id,machine_id,state,cur_timestamp,cluster_type_title,machine_type_id,machine_type_name,machine_type_spec) VALUES (64,10495,33,18,'11','34',1,{ts '2013-06-07 15:05:23.'},'',0,'','');
INSERT INTO usage_hours_tbl (id,organization_id,instance_id,cluster_id,platform_id,machine_id,state,cur_timestamp,cluster_type_title,machine_type_id,machine_type_name,machine_type_spec) VALUES (65,10495,33,19,'10','35',1,{ts '2013-06-07 15:17:11.'},'',0,'','');
INSERT INTO usage_hours_tbl (id,organization_id,instance_id,cluster_id,platform_id,machine_id,state,cur_timestamp,cluster_type_title,machine_type_id,machine_type_name,machine_type_spec) VALUES (66,10495,33,19,'10','36',1,{ts '2013-06-07 15:17:13.'},'',0,'','');
INSERT INTO usage_hours_tbl (id,organization_id,instance_id,cluster_id,platform_id,machine_id,state,cur_timestamp,cluster_type_title,machine_type_id,machine_type_name,machine_type_spec) VALUES (67,10495,33,17,'10','31',3,{ts '2013-06-07 15:36:06.'},'',0,'','');
INSERT INTO usage_hours_tbl (id,organization_id,instance_id,cluster_id,platform_id,machine_id,state,cur_timestamp,cluster_type_title,machine_type_id,machine_type_name,machine_type_spec) VALUES (68,10495,33,17,'10','32',3,{ts '2013-06-07 15:36:06.'},'',0,'','');
INSERT INTO usage_hours_tbl (id,organization_id,instance_id,cluster_id,platform_id,machine_id,state,cur_timestamp,cluster_type_title,machine_type_id,machine_type_name,machine_type_spec) VALUES (69,10495,33,19,'10','35',3,{ts '2013-06-10 12:33:04.'},'',0,'','');
INSERT INTO usage_hours_tbl (id,organization_id,instance_id,cluster_id,platform_id,machine_id,state,cur_timestamp,cluster_type_title,machine_type_id,machine_type_name,machine_type_spec) VALUES (70,10495,33,19,'10','36',3,{ts '2013-06-10 12:33:04.'},'',0,'','');
INSERT INTO usage_hours_tbl (id,organization_id,instance_id,cluster_id,platform_id,machine_id,state,cur_timestamp,cluster_type_title,machine_type_id,machine_type_name,machine_type_spec) VALUES (71,10495,33,20,'10','37',1,{ts '2013-06-11 13:01:12.'},'',0,'','');
INSERT INTO usage_hours_tbl (id,organization_id,instance_id,cluster_id,platform_id,machine_id,state,cur_timestamp,cluster_type_title,machine_type_id,machine_type_name,machine_type_spec) VALUES (72,10495,33,20,'10','38',1,{ts '2013-06-11 13:01:14.'},'',0,'','');

-- Test data for invoicing
INSERT INTO instance_tbl (instance_id,user_id,instance_name,cloud_type,cloud_zone,organization_id,instance_status,instance_active) VALUES (720,12345,'extranet portal test',1,'dev',10500,'Starting',1);

insert into instance_share_tbl (id, instance_id, period_start, created_by, created, modified_by) 
    values (153, 720, '2013-06-01 00:00:00', 12345, NOW(), 12345);
    
insert into instance_share_invoice_tbl (instance_share_id, period_start, period_end, total_usage, created_by, created, modified_by) 
    values (153, '2013-06-01 00:00:00', '2013-06-30 23:59:59', 12, 12345, NOW(), 12345);
    
insert into instance_share_detail_tbl (instance_share_id, cost_pool, share_percent, description, order_number, created_by, created, modified_by) 
    values (153, '5064578230', 75.00, 'portal server', '12345678', 12345, NOW(), 12345);
insert into instance_share_detail_tbl (instance_share_id, cost_pool, share_percent, description, order_number, created_by, created, modified_by) 
    values (153, '5067816549', 25.00, 'portal server', '20214921', 12345, NOW(), 12345);    

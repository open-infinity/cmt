/*
PATCHED FILE FOR H2 USE
FOLLOWING PATCHES ARE REQUIRED:
- REMOVE "ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8" STUFF
- REMOVE "ON UPDATE CURRENT_TIMESTAMP" FROM LINES LIKE THIS: `cur_timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
 */
DROP TABLE IF EXISTS `DEPLOYMENT`;

CREATE TABLE `DEPLOYMENT` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `state` int(11) NOT NULL,
  `ORGANIZATION_ID` bigint(20) NOT NULL,
  `INSTANCE_ID` bigint(20) NOT NULL,
  `CLUSTER_ID` int(11) NOT NULL,
  `LOCATION` varchar(255) NOT NULL,
  `NAME` varchar(255) NOT NULL,
  `cur_timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `authorized_ip_tbl`;

CREATE TABLE `authorized_ip_tbl` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `instance_id` int(11) DEFAULT NULL,
  `cluster_id` int(11) DEFAULT NULL,
  `cidr_ip` varchar(50) DEFAULT NULL,
  `protocol` varchar(15) DEFAULT NULL,
  `security_group_name` varchar(30) DEFAULT NULL,
  `from_port` int(11) DEFAULT NULL,
  `to_port` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `cluster_tbl`;

CREATE TABLE `cluster_tbl` (
  `cluster_id` int(11) NOT NULL AUTO_INCREMENT,
  `cluster_name` varchar(255) DEFAULT NULL,
  `cluster_number_of_machines` int(11) DEFAULT NULL,
  `cluster_lb_name` varchar(255) DEFAULT NULL,
  `cluster_lb_dns` varchar(255) DEFAULT NULL,
  `instance_id` int(11) DEFAULT NULL,
  `cluster_type` int(11) DEFAULT NULL,
  `cluster_pub` int(11) DEFAULT NULL,
  `cluster_live` int(11) DEFAULT NULL,
  `cluster_lb_instance_id` varchar(100) DEFAULT NULL,
  `cluster_security_group_id` varchar(50) DEFAULT NULL,
  `cluster_security_group_name` varchar(100) DEFAULT NULL,
  `cluster_multicast_address` varchar(50) DEFAULT NULL,
  `cluster_machine_type` int(11) DEFAULT NULL,
  `cluster_ebs_image_used` int(11) DEFAULT NULL,
  `cluster_ebs_volumes_used` int(11) DEFAULT NULL,
  PRIMARY KEY (`cluster_id`)
);

DROP TABLE IF EXISTS `elastic_ip_tbl`;

CREATE TABLE `elastic_ip_tbl` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `instance_id` int(11) DEFAULT NULL,
  `cluster_id` int(11) DEFAULT NULL,
  `machine_id` int(11) DEFAULT NULL,
  `ip_address` varchar(20) DEFAULT NULL,
  `external_ip` varchar(20) DEFAULT NULL,
  `in_use` int(11) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  `organization_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `instance_tbl`;

CREATE TABLE `instance_tbl` (
  `instance_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) DEFAULT NULL,
  `instance_name` varchar(50) DEFAULT NULL,
  `cloud_type` int(11) DEFAULT NULL,
  `cloud_zone` varchar(50) DEFAULT NULL,
  `organization_id` int(11) DEFAULT NULL,
  `instance_status` varchar(20) DEFAULT NULL,
  `instance_active` int(11) DEFAULT NULL,
  PRIMARY KEY (`instance_id`)
);

DROP TABLE IF EXISTS `job_tbl`;

CREATE TABLE `job_tbl` (
  `job_id` int(11) NOT NULL AUTO_INCREMENT,
  `job_type` varchar(50) DEFAULT NULL,
  `job_status` int(11) DEFAULT NULL,
  `job_instance_id` int(11) DEFAULT NULL,
  `job_services` varchar(200) DEFAULT NULL,
  `job_extra_data` varchar(200) DEFAULT NULL,
  `job_cloud` int(11) DEFAULT NULL,
  `job_zone` varchar(30) DEFAULT NULL,
  `job_create_time` datetime DEFAULT NULL,
  `job_start_time` datetime DEFAULT NULL,
  `job_end_time` datetime DEFAULT NULL,
  PRIMARY KEY (`job_id`)
);

DROP TABLE IF EXISTS `key_tbl`;

CREATE TABLE `key_tbl` (
  `key_id` int(11) NOT NULL AUTO_INCREMENT,
  `instance_id` int(11) DEFAULT NULL,
  `secret_key` varchar(2048) DEFAULT NULL,
  `key_fingerprint` varchar(100) DEFAULT NULL,
  `key_name` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`key_id`)
);

DROP TABLE IF EXISTS `machine_tbl`;

CREATE TABLE `machine_tbl` (
  `machine_id` int(11) NOT NULL AUTO_INCREMENT,
  `machine_instance_id` varchar(50) DEFAULT NULL,
  `project_id` int(11) DEFAULT NULL,
  `machine_name` varchar(255) DEFAULT NULL,
  `machine_dns_name` varchar(50) DEFAULT NULL,
  `machine_key` int(11) DEFAULT NULL,
  `active` int(11) DEFAULT NULL,
  `machine_username` varchar(20) DEFAULT NULL,
  `machine_running` int(11) NOT NULL,
  `machine_state` varchar(255) DEFAULT NULL,
  `machine_cluster_id` int(11) DEFAULT NULL,
  `machine_private_dns_name` varchar(255) DEFAULT NULL,
  `machine_type` varchar(50) DEFAULT NULL,
  `machine_configured` int(11) DEFAULT NULL,
  `machine_last_update` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `machine_cloud_type` int(11) DEFAULT NULL,
  `machine_extra_ebs_volume_id` varchar(20) DEFAULT NULL,
  `machine_extra_ebs_volume_device` varchar(20) DEFAULT NULL,
  `machine_extra_ebs_volume_size` int(11) DEFAULT NULL,
  PRIMARY KEY (`machine_id`)
);

DROP TABLE IF EXISTS `user_authorized_ip_tbl`;

CREATE TABLE `user_authorized_ip_tbl` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `instance_id` int(11) DEFAULT NULL,
  `cluster_id` int(11) DEFAULT NULL,
  `cidr_ip` varchar(50) DEFAULT NULL,
  `protocol` varchar(15) DEFAULT NULL,
  `security_group_name` varchar(30) DEFAULT NULL,
  `from_port` int(11) DEFAULT NULL,
  `to_port` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `scaling_rule_tbl`;
	
CREATE TABLE `scaling_rule_tbl` (
  `cluster_id` int(11) NOT NULL,
  `periodic` boolean DEFAULT NULL,
  `scheduled` boolean DEFAULT NULL,
  `scaling_state` int(11) NOT NULL,
  `max_machines` int(11) DEFAULT NULL,
  `min_machines` int(11) DEFAULT NULL,
  `max_load` int(11) DEFAULT NULL,
  `min_load` int(11) DEFAULT NULL,
  `period_from` datetime DEFAULT NULL,
  `period_to` datetime DEFAULT NULL,
  `size_new` int(11) DEFAULT NULL,
  `size_original` int(11) DEFAULT NULL,
  `job_id` int(11) NOT NULL,
  PRIMARY KEY (`cluster_id`)
);

DROP TABLE IF EXISTS `cluster_type_tbl`;
	
CREATE TABLE `cluster_type_tbl` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `configuration_id` int(11) DEFAULT NULL,
  `name` varchar(20) NOT NULL,
  `title` varchar(50) DEFAULT NULL,
  `dependency` int(11) DEFAULT -1,
  `replicated` boolean DEFAULT false,
  `min_machines` int(11) DEFAULT NULL,
  `max_machines` int(11) DEFAULT NULL,
  `min_repl_machines` int(11) DEFAULT NULL,
  `max_repl_machines` int(11) DEFAULT NULL,  PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `cloud_provider_tbl`;

CREATE TABLE `cloud_provider_tbl` (
  `id` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `availability_zone_tbl`;

CREATE TABLE `availability_zone_tbl` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `cloud_id` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT fk_zone_cloud FOREIGN KEY (cloud_id) REFERENCES cloud_provider_tbl(id)
);

DROP TABLE IF EXISTS `machine_type_tbl`;

CREATE TABLE `machine_type_tbl` (
  `id` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  `spec` varchar(255) NOT NULL,
  `cores` int(11) DEFAULT NULL,
  `ram` int(11) DEFAULT NULL,
  `disk` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `acl_cluster_type_tbl`;

CREATE TABLE `acl_cluster_type_tbl` (
  `org_name` varchar(50) NOT NULL,
  `cluster_id` int(11) NOT NULL,
  PRIMARY KEY (`org_name`, `cluster_id`),
  CONSTRAINT fk_acl_cluster_type FOREIGN KEY (cluster_id) REFERENCES cluster_type_tbl(id)
);

DROP TABLE IF EXISTS `acl_cloud_provider_tbl`;

CREATE TABLE `acl_cloud_provider_tbl` (
  `org_name` varchar(50) NOT NULL,
  `cloud_id` int(11) NOT NULL,
  PRIMARY KEY (`org_name`, `cloud_id`),
  CONSTRAINT fk_acl_cloud_provider FOREIGN KEY (cloud_id) REFERENCES cloud_provider_tbl(id)
);

DROP TABLE IF EXISTS `acl_availability_zone_tbl`;

CREATE TABLE `acl_availability_zone_tbl` (
  `org_name` varchar(50) NOT NULL,
  `zone_id` int(11) NOT NULL,
  PRIMARY KEY (`org_name`, `zone_id`),
  CONSTRAINT fk_acl_availability_zone FOREIGN KEY (zone_id) REFERENCES availability_zone_tbl(id)
);

DROP TABLE IF EXISTS `acl_machine_type_tbl`;

CREATE TABLE `acl_machine_type_tbl` (
  `org_name` varchar(50) NOT NULL,
  `machine_type_id` int(11) NOT NULL,
  PRIMARY KEY (`org_name`, `machine_type_id`),
  CONSTRAINT fk_acl_machine_type FOREIGN KEY (machine_type_id) REFERENCES machine_type_tbl(id)
);

DROP TABLE IF EXISTS `instance_parameter_tbl`;

CREATE TABLE `instance_parameter_tbl` (
  `id` int(11) AUTO_INCREMENT,
  `instance_id` int(11),
  `pkey` varchar(255) NOT NULL,
  `pvalue` varchar(1000),
  PRIMARY KEY (`id`),
  CONSTRAINT fk_instance FOREIGN KEY (instance_id) REFERENCES instance_tbl(instance_id)
);

DROP TABLE IF EXISTS `usage_hours_tbl`;

create table `usage_hours_tbl` (
	`id` bigint(20) not null auto_increment, 
	`organization_id` bigint(20) not null, 
	`cluster_id` bigint(20) not null, 
	`platform_id` char(254) not null, 
	`machine_id` char(254) not null, 
	`state` int(11) not null, 
	`cur_timestamp` timestamp not null default current_timestamp, 
	primary key (`id`)
);

DROP TABLE IF EXISTS `deployment_state_tbl`;

CREATE TABLE `deployment_state_tbl` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `deployment_id` int(11) NOT NULL,
  `machine_id` int(11) NOT NULL,
  `state` int(11) DEFAULT '0',
  `cur_timestamp` timestamp NOT NULL,
  PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `configuration_template_tbl`;

CREATE TABLE `configuration_template_tbl` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(30) NOT NULL,
  `description` varchar(256),
  PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `configuration_template_organization_tbl`;

CREATE TABLE `configuration_template_organization_tbl` (
  `organization_id`bigint(20) NOT NULL,
  `template_id` int(11) NOT NULL,
  PRIMARY KEY (`organization_id`),
  CONSTRAINT fk_configuration_template FOREIGN KEY (template_id) REFERENCES configuration_template_tbl(id)
);




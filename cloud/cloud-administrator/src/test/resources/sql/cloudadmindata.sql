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
  `cluster_machine_type` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`cluster_id`)
);

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

CREATE TABLE `key_tbl` (
  `key_id` int(11) NOT NULL AUTO_INCREMENT,
  `instance_id` int(11) DEFAULT NULL,
  `secret_key` varchar(2048) DEFAULT NULL,
  `key_fingerprint` varchar(100) DEFAULT NULL,
  `key_name` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`key_id`)
);

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
  PRIMARY KEY (`machine_id`)
);

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

CREATE TABLE `scaling_rule_tbl` (
  `cluster_id` int(11) NOT NULL,
  `periodic` boolean DEFAULT NULL,
  `scheduled` boolean DEFAULT NULL,
  `scaling_state` int(11) NOT NULL,
  `max_machines` int(11) DEFAULT NULL,
  `min_machines` int(11) DEFAULT NULL,
  `max_cpu_load` int(11) DEFAULT NULL,
  `min_cpu_load` int(11) DEFAULT NULL,
  `period_from` datetime DEFAULT NULL,
  `period_to` datetime DEFAULT NULL,
  `size_new` int(11) DEFAULT NULL,
  `size_original` int(11) DEFAULT NULL,
  `job_id` int(11) NOT NULL,
  PRIMARY KEY (`cluster_id`)
);

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

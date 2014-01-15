CREATE DATABASE openinfinity;
USE openinfinity;

CREATE TABLE `BATCH_JOB_EXECUTION` (
  `JOB_EXECUTION_ID` bigint(20) NOT NULL,
  `VERSION` bigint(20) DEFAULT NULL,
  `JOB_INSTANCE_ID` bigint(20) NOT NULL,
  `CREATE_TIME` datetime NOT NULL,
  `START_TIME` datetime DEFAULT NULL,
  `END_TIME` datetime DEFAULT NULL,
  `STATUS` varchar(10) DEFAULT NULL,
  `EXIT_CODE` varchar(20) DEFAULT NULL,
  `EXIT_MESSAGE` varchar(2500) DEFAULT NULL,
  `LAST_UPDATED` datetime DEFAULT NULL,
  PRIMARY KEY (`JOB_EXECUTION_ID`),
  KEY `JOB_INST_EXEC_FK` (`JOB_INSTANCE_ID`),
  CONSTRAINT `JOB_INST_EXEC_FK` FOREIGN KEY (`JOB_INSTANCE_ID`) REFERENCES `BATCH_JOB_INSTANCE` (`JOB_INSTANCE_ID`)
); 

CREATE TABLE `BATCH_JOB_EXECUTION_CONTEXT` (
  `JOB_EXECUTION_ID` bigint(20) NOT NULL,
  `SHORT_CONTEXT` varchar(2500) NOT NULL,
  `SERIALIZED_CONTEXT` text,
  PRIMARY KEY (`JOB_EXECUTION_ID`),
  CONSTRAINT `JOB_EXEC_CTX_FK` FOREIGN KEY (`JOB_EXECUTION_ID`) REFERENCES `BATCH_JOB_EXECUTION` (`JOB_EXECUTION_ID`)
); 

CREATE TABLE `BATCH_JOB_EXECUTION_SEQ` (
  `ID` bigint(20) NOT NULL
);

CREATE TABLE `BATCH_JOB_INSTANCE` (
  `JOB_INSTANCE_ID` bigint(20) NOT NULL,
  `VERSION` bigint(20) DEFAULT NULL,
  `JOB_NAME` varchar(100) NOT NULL,
  `JOB_KEY` varchar(32) NOT NULL,
  PRIMARY KEY (`JOB_INSTANCE_ID`),
  UNIQUE KEY `JOB_INST_UN` (`JOB_NAME`,`JOB_KEY`)
);

CREATE TABLE `BATCH_JOB_PARAMS` (
  `JOB_INSTANCE_ID` bigint(20) NOT NULL,
  `TYPE_CD` varchar(6) NOT NULL,
  `KEY_NAME` varchar(100) NOT NULL,
  `STRING_VAL` varchar(250) DEFAULT NULL,
  `DATE_VAL` datetime DEFAULT NULL,
  `LONG_VAL` bigint(20) DEFAULT NULL,
  `DOUBLE_VAL` double DEFAULT NULL,
  KEY `JOB_INST_PARAMS_FK` (`JOB_INSTANCE_ID`),
  CONSTRAINT `JOB_INST_PARAMS_FK` FOREIGN KEY (`JOB_INSTANCE_ID`) REFERENCES `BATCH_JOB_INSTANCE` (`JOB_INSTANCE_ID`)
);

CREATE TABLE `BATCH_JOB_SEQ` (
  `ID` bigint(20) NOT NULL
);

CREATE TABLE `BATCH_STEP_EXECUTION` (
  `STEP_EXECUTION_ID` bigint(20) NOT NULL,
  `VERSION` bigint(20) NOT NULL,
  `STEP_NAME` varchar(100) NOT NULL,
  `JOB_EXECUTION_ID` bigint(20) NOT NULL,
  `START_TIME` datetime NOT NULL,
  `END_TIME` datetime DEFAULT NULL,
  `STATUS` varchar(10) DEFAULT NULL,
  `COMMIT_COUNT` bigint(20) DEFAULT NULL,
  `READ_COUNT` bigint(20) DEFAULT NULL,
  `FILTER_COUNT` bigint(20) DEFAULT NULL,
  `WRITE_COUNT` bigint(20) DEFAULT NULL,
  `READ_SKIP_COUNT` bigint(20) DEFAULT NULL,
  `WRITE_SKIP_COUNT` bigint(20) DEFAULT NULL,
  `PROCESS_SKIP_COUNT` bigint(20) DEFAULT NULL,
  `ROLLBACK_COUNT` bigint(20) DEFAULT NULL,
  `EXIT_CODE` varchar(20) DEFAULT NULL,
  `EXIT_MESSAGE` varchar(2500) DEFAULT NULL,
  `LAST_UPDATED` datetime DEFAULT NULL,
  PRIMARY KEY (`STEP_EXECUTION_ID`),
  KEY `JOB_EXEC_STEP_FK` (`JOB_EXECUTION_ID`),
  CONSTRAINT `JOB_EXEC_STEP_FK` FOREIGN KEY (`JOB_EXECUTION_ID`) REFERENCES `BATCH_JOB_EXECUTION` (`JOB_EXECUTION_ID`)
);

CREATE TABLE `BATCH_STEP_EXECUTION_CONTEXT` (
  `STEP_EXECUTION_ID` bigint(20) NOT NULL,
  `SHORT_CONTEXT` varchar(2500) NOT NULL,
  `SERIALIZED_CONTEXT` text,
  PRIMARY KEY (`STEP_EXECUTION_ID`),
  CONSTRAINT `STEP_EXEC_CTX_FK` FOREIGN KEY (`STEP_EXECUTION_ID`) REFERENCES `BATCH_STEP_EXECUTION` (`STEP_EXECUTION_ID`)
);

CREATE TABLE `BATCH_STEP_EXECUTION_SEQ` (
  `ID` bigint(20) NOT NULL
);

CREATE TABLE `DEPLOYMENT` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `state` int(11) NOT NULL,
  `ORGANIZATION_ID` bigint(20) NOT NULL,
  `INSTANCE_ID` bigint(20) NOT NULL,
  `CLUSTER_ID` int(11) NOT NULL,
  `LOCATION` varchar(255) NOT NULL,
  `NAME` varchar(255) NOT NULL,
  `cur_timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
);

CREATE TABLE `acl_availability_zone_tbl` (
  `org_name` varchar(50) NOT NULL,
  `zone_id` int(11) NOT NULL,
  PRIMARY KEY (`org_name`,`zone_id`),
  KEY `fk_acl_availability_zone` (`zone_id`),
  CONSTRAINT `fk_acl_availability_zone` FOREIGN KEY (`zone_id`) REFERENCES `availability_zone_tbl` (`id`)
);

CREATE TABLE `acl_cloud_provider_tbl` (
  `org_name` varchar(50) NOT NULL,
  `cloud_id` int(11) NOT NULL,
  PRIMARY KEY (`org_name`,`cloud_id`),
  KEY `fk_acl_cloud_provider` (`cloud_id`),
  CONSTRAINT `fk_acl_cloud_provider` FOREIGN KEY (`cloud_id`) REFERENCES `cloud_provider_tbl` (`id`)
);

CREATE TABLE `acl_cluster_type_tbl` (
  `org_name` varchar(50) NOT NULL,
  `cluster_id` int(11) NOT NULL,
  PRIMARY KEY (`org_name`,`cluster_id`),
  KEY `fk_acl_cluster_type` (`cluster_id`),
  CONSTRAINT `fk_acl_cluster_type` FOREIGN KEY (`cluster_id`) REFERENCES `cluster_type_tbl` (`id`)
);

CREATE TABLE `acl_machine_type_tbl` (
  `org_name` varchar(50) NOT NULL,
  `machine_type_id` int(11) NOT NULL,
  PRIMARY KEY (`org_name`,`machine_type_id`),
  KEY `fk_acl_machine_type` (`machine_type_id`),
  CONSTRAINT `fk_acl_machine_type` FOREIGN KEY (`machine_type_id`) REFERENCES `machine_type_tbl` (`id`)
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

CREATE TABLE `availability_zone_tbl` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `cloud_id` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_zone_cloud` (`cloud_id`),
  CONSTRAINT `fk_zone_cloud` FOREIGN KEY (`cloud_id`) REFERENCES `cloud_provider_tbl` (`id`)
);

CREATE TABLE `cloud_properties_tbl` (
  `organization_id` bigint(20) NOT NULL,
  `key_column` varchar(64) NOT NULL,
  `value_column` varchar(256) DEFAULT NULL
);

CREATE TABLE `cloud_provider_tbl` (
  `id` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
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
  `cluster_machine_type` int(11) DEFAULT NULL,
  `cluster_ebs_image_used` int(11) DEFAULT NULL,
  `cluster_ebs_volumes_used` int(11) DEFAULT NULL,
  PRIMARY KEY (`cluster_id`)
); 

CREATE TABLE `cluster_type_tbl` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `configuration_id` int(11) DEFAULT NULL,
  `name` varchar(20) NOT NULL,
  `title` varchar(50) DEFAULT NULL,
  `dependency` int(11) DEFAULT '-1',
  `replicated` tinyint(1) DEFAULT '0',
  `min_machines` int(11) DEFAULT NULL,
  `max_machines` int(11) DEFAULT NULL,
  `min_repl_machines` int(11) DEFAULT NULL,
  `max_repl_machines` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
); 

CREATE TABLE `cluster_type_tbl_tmp` (
  `id` int(11) NOT NULL DEFAULT '0',
  `configuration_id` int(11) DEFAULT NULL,
  `name` varchar(10) CHARACTER SET utf8 DEFAULT NULL,
  `title` varchar(50) CHARACTER SET utf8 DEFAULT NULL,
  `dependency` int(11) DEFAULT '-1',
  `replicated` tinyint(1) DEFAULT '0',
  `min_machines` int(11) DEFAULT NULL,
  `max_machines` int(11) DEFAULT NULL,
  `min_repl_machines` int(11) DEFAULT NULL,
  `max_repl_machines` int(11) DEFAULT NULL
);

CREATE TABLE `deployment_state_tbl` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `deployment_id` int(11) NOT NULL,
  `machine_id` int(11) NOT NULL,
  `state` int(11) DEFAULT '0',
  `cur_timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
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

CREATE TABLE `instance_parameter_tbl` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `instance_id` int(11) DEFAULT NULL,
  `pkey` varchar(255) NOT NULL,
  `pvalue` varchar(1000) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_instance` (`instance_id`),
  CONSTRAINT `fk_instance` FOREIGN KEY (`instance_id`) REFERENCES `instance_tbl` (`instance_id`)
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
  `machine_last_update` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `machine_cloud_type` int(11) DEFAULT NULL,
  `machine_extra_ebs_volume_id` varchar(20) DEFAULT NULL,
  `machine_extra_ebs_volume_device` varchar(10) DEFAULT NULL,
  `machine_extra_ebs_volume_size` int(11) DEFAULT NULL,
  PRIMARY KEY (`machine_id`)
); 

CREATE TABLE `machine_type_tbl` (
  `id` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  `spec` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
);

CREATE TABLE `reserved_multicast_ip_tbl` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `instance_id` int(11) DEFAULT NULL,
  `cluster_id` int(11) DEFAULT NULL,
  `address` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
);

CREATE TABLE `scaling_rule_tbl` (
  `cluster_id` int(11) NOT NULL,
  `periodic` tinyint(1) DEFAULT NULL,
  `scheduled` tinyint(1) DEFAULT NULL,
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

CREATE TABLE `backup_rule_tbl` (
`backup_rule_id` int(11) NOT NULL AUTO_INCREMENT,
`cluster_id` int(11) NOT NULL,
`active` ENUM('yes', 'no') NOT NULL,
`cron_minutes` varchar(12) NOT NULL DEFAULT '0',
`cron_hours` varchar(12) NOT NULL DEFAULT '22',
`cron_day_of_month` varchar(12) NOT NULL DEFAULT '*',
`cron_month` varchar(12) NOT NULL DEFAULT '*',
`cron_day_of_week` varchar(12) NOT NULL DEFAULT '?',
`cron_year` varchar(12) NOT NULL DEFAULT '*',
PRIMARY KEY (`backup_rule_id`),
FOREIGN KEY (`cluster_id`) REFERENCES cluster_tbl(cluster_id) ON DELETE CASCADE,
CONSTRAINT `cluster_id` FOREIGN KEY (`cluster_id`) REFERENCES `cluster_tbl` (`cluster_id`)
);

CREATE TABLE `backup_operation_tbl` (
`backup_operation_id` int(11) NOT NULL AUTO_INCREMENT,
`operation` ENUM('backup', 'full-restore', 'partial-restore', 'refresh-schedules') NOT NULL,
`create_time` datetime NULL,
`update_time` datetime NULL,
`target_cluster_id` int(11) NOT NULL,
`source_cluster_id` int(11),
`state` ENUM('requested', 'in-progress', 'succeeded', 'failed') NOT NULL DEFAULT 'requested',
PRIMARY KEY (`backup_operation_id`)
);
CREATE TRIGGER `backup_operation_tbl_INSERT` BEFORE INSERT ON `backup_operation_tbl`
    FOR EACH ROW SET NEW.create_time = IFNULL(NEW.create_time, NOW()), NEW.update_time = IFNULL(NEW.update_time, NOW());
CREATE TRIGGER `backup_operation_tbl_UPDATE` BEFORE UPDATE ON `backup_operation_tbl`
    FOR EACH ROW SET NEW.update_time = IFNULL(NEW.update_time, NOW());


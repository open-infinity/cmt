CREATE TABLE `cloud_provider_tbl` (
  `id` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
);

CREATE TABLE `availability_zone_tbl` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `cloud_id` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`), 
  CONSTRAINT fk_zone_cloud FOREIGN KEY (cloud_id) REFERENCES cloud_provider_tbl(id)
);

CREATE TABLE `machine_type_tbl` (
  `id` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  `spec` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
);

CREATE TABLE `acl_cluster_type_tbl` (
  `org_name` varchar(50) NOT NULL,
  `cluster_id` int(11) NOT NULL,
  PRIMARY KEY (`org_name`, `cluster_id`),
  CONSTRAINT fk_acl_cluster_type FOREIGN KEY (cluster_id) REFERENCES cluster_type_tbl(id)
);

CREATE TABLE `acl_cloud_provider_tbl` (
  `org_name` varchar(50) NOT NULL,
  `cloud_id` int(11) NOT NULL,
  PRIMARY KEY (`org_name`, `cloud_id`),
  CONSTRAINT fk_acl_cloud_provider FOREIGN KEY (cloud_id) REFERENCES cloud_provider_tbl(id)
);

CREATE TABLE `acl_availability_zone_tbl` (
  `org_name` varchar(50) NOT NULL,
  `zone_id` int(11) NOT NULL,
  PRIMARY KEY (`org_name`, `zone_id`),
  CONSTRAINT fk_acl_availability_zone FOREIGN KEY (zone_id) REFERENCES availability_zone_tbl(id)
);

CREATE TABLE `acl_machine_type_tbl` (
  `org_name` varchar(50) NOT NULL,
  `machine_type_id` int(11) NOT NULL,
  PRIMARY KEY (`org_name`, `machine_type_id`),
  CONSTRAINT fk_acl_machine_type FOREIGN KEY (machine_type_id) REFERENCES machine_type_tbl(id)
);

CREATE TABLE `job_platform_parameter_tbl` (
  `id` int(11) AUTO_INCREMENT,
  `job_id` int(11),
  `pkey` varchar(255) NOT NULL,
  `pvalue` varchar(1000),
  PRIMARY KEY (`id`),
  CONSTRAINT fk_job_plaform FOREIGN KEY (job_id) REFERENCES job_tbl(job_id)
);

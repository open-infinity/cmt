DROP TABLE IF EXISTS cluster_tbl;
CREATE TABLE cluster_tbl (
  cluster_id int IDENTITY,
  cluster_name varchar,
  cluster_number_of_machines int,
  cluster_lb_name varchar,
  cluster_lb_dns varchar,
  instance_id int,
  cluster_type int,
  cluster_pub int,
  cluster_live int,
  cluster_lb_instance_id varchar,
  cluster_security_group_id varchar,
  cluster_security_group_name varchar,
  cluster_multicast_address varchar,
  cluster_machine_type tinyint,
  cluster_ebs_image_used int,
  cluster_ebs_volumes_used int,
  PRIMARY KEY (cluster_id)
);


DROP TABLE IF EXISTS instance_tbl;
CREATE TABLE instance_tbl (
  instance_id int IDENTITY ,
  user_id int,
  instance_name varchar,
  cloud_type int,
  cloud_zone varchar,
  organization_id int,
  instance_status varchar,
  instance_active int,
  PRIMARY KEY (instance_id)
) ;

DROP TABLE IF EXISTS instance_parameter_tbl;
CREATE TABLE instance_parameter_tbl (
  id int IDENTITY ,
  instance_id int,
  pkey varchar,
  pvalue int,
  PRIMARY KEY (id)
) ;


DROP TABLE IF EXISTS job_tbl;
CREATE TABLE job_tbl (
  job_id int IDENTITY ,
  job_type varchar,
  job_status int,
  job_instance_id int,
  job_services varchar,
  job_extra_data varchar,
  job_cloud int,
  job_zone varchar,
  job_create_time datetime,
  job_start_time datetime,
  job_end_time datetime,
  PRIMARY KEY (job_id)
) ;


DROP TABLE IF EXISTS machine_tbl;
CREATE TABLE machine_tbl (
  machine_id int IDENTITY ,
  machine_instance_id varchar,
  project_id int,
  machine_name varchar,
  machine_dns_name varchar,
  machine_key int,
  active int,
  machine_username varchar,
  machine_running int,
  machine_state varchar,
  machine_cluster_id int,
  machine_private_dns_name varchar,
  machine_type varchar,
  machine_configured int,
  machine_last_update timestamp,
  machine_cloud_type int,
  machine_extra_ebs_volume_id varchar,
  machine_extra_ebs_volume_device varchar,
  machine_extra_ebs_volume_size int,
  PRIMARY KEY (machine_id)
);

DROP TABLE IF EXISTS scaling_rule_tbl;
CREATE TABLE scaling_rule_tbl (
  cluster_id int IDENTITY,
  periodic boolean,
  scheduled boolean,
  scaling_state int,
  max_machines int,
  min_machines int,
  max_load float,
  min_load float,
  period_from datetime,
  period_to datetime,
  size_new int,
  size_original int,
  job_id int,
  PRIMARY KEY (cluster_id)
); 


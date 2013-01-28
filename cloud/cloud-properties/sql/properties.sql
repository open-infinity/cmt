DROP TABLE IF EXISTS `SHARED_PROPERTIES`;	
DROP TABLE IF EXISTS `cloud_properties_tbl`;	
CREATE TABLE `cloud_properties_tbl` (
	`organization_id` bigint(20) NOT NULL,
    `key_column` varchar(64) NOT NULL,
    `value_column` varchar(256)
);



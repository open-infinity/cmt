
DROP TABLE `properties_tbl`;

CREATE TABLE `properties_tbl` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `organization_id` int(11) DEFAULT NULL,
  `instance_id` int(11) DEFAULT NULL,
  `cluster_id` int(11) DEFAULT NULL,
  `key_column` varchar(256) DEFAULT NULL,
  `value_column` varchar(256) DEFAULT NULL,
  `changed_last_update` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT properties_isntance_id FOREIGN KEY (id) REFERENCES instance_tbl ON DELETE CASCADE,
  CONSTRAINT properties_cluster_id FOREIGN KEY (id) REFERENCES cluster_tbl ON DELETE CASCADE
);


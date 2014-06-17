DROP TABLE IF EXISTS `account`;

CREATE TABLE `account` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `organization_id` int(11),
  `name` varchar(32),
  `state` int(11),
  PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `invoice`;

CREATE TABLE `invoice` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `account_id` int(11),
  `period_from` DATETIME DEFAULT NULL,
  `period_to` DATETIME DEFAULT NULL,
  `sent_time` DATETIME DEFAULT NULL,
  `state` int(11),
  PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `invoice_item`;

CREATE TABLE `invoice_item` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `invoice_id` int(11),
  `machine_id` int(11),
  `cluster_id` int(11),
  `machine_uptime` bigint(19) DEFAULT NULL,
  `machine_type` int(11),
  PRIMARY KEY (`id`)
);

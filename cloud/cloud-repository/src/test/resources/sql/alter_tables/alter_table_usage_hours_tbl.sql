-- cluster_type_title : duplicated from cluster_type_tbl.title
-- machine_type_id : duplicated from cluster_tbl.cluster_machine_type
-- machine_type_name : duplicated from machine_type_tbl.name
-- machine_type_spec : duplicated from machine_type_tbl.spec
ALTER TABLE usage_hours_tbl ADD `cluster_type_title` varchar(50) not null;
ALTER TABLE usage_hours_tbl ADD `machine_type_id` tinyint(4) not null;
ALTER TABLE usage_hours_tbl ADD `machine_type_name` varchar(255) not null;
ALTER TABLE usage_hours_tbl ADD `machine_type_spec` varchar(255) not null;
ALTER TABLE usage_hours_tbl ADD `cluster_ebs_image_used` int(11) default null;
ALTER TABLE usage_hours_tbl ADD `cluster_ebs_volumes_used` int(11) default null;

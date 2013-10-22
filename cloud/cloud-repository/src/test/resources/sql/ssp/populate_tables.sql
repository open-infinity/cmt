
insert into account (organization_id, name, state)
    values(10687, 'test account', 1);

insert into invoice (account_id, period_from, period_to, sent_time, state)
    values(1, '2013-08-01 14:12:12', '2013-09-01 14:12:12', '2013-09-01 14:15:12', 1);

insert into invoice (account_id, period_from, period_to, sent_time, state)
    values(1, '2013-09-01 14:12:12', '2013-10-01 14:12:12', '2013-10-01 14:15:12', 0);

insert into machine_tbl (machine_id, machine_instance_id, project_id, machine_name, machine_dns_name, machine_key, active,
    machine_username, machine_running, machine_state, machine_cluster_id, machine_private_dns_name, machine_type, machine_configured,
    machine_last_update, machine_cloud_type)
    values(3532, 'machine1', '0', 'BAS platform', '127.0.0.1', '0', '1', 'root', '1', 'running', '1', 'dns', 'loadbalancer',
    '3', "2013-10-01 13:03:47", '1');

insert into cluster_tbl (cluster_id, cluster_type) values(1, '0');


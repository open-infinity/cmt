
insert into account (organization_id, name, status)
    values(10687, 'test account', 1);

insert into invoice (account_id, period_from, period_to, sent_time, state)
    values(1, '2013-08-01 14:12:12', '2013-09-01 14:12:12', '2013-09-01 14:15:12', 1);

insert into invoice (account_id, period_from, period_to, sent_time, state)
    values(1, '2013-09-01 14:12:12', '2013-10-01 14:12:12', '2013-10-01 14:15:12', 0);


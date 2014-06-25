#!/bin/sh

# Dump directory
BACKUP_DIR=/opt/openinfinity/3.1.0/backup/dumps
mkdir -p $BACKUP_DIR &>/dev/null

APACHEDS=`cd /etc/init.d ; ls apacheds* | tail -1`
APACHEDS_DATADIR=`ls /var/lib/apacheds* | tail -1`

# ApacheDS doesn't support hot backup, that leaves us with this situation
service $APACHEDS stop
cd / && tar -czf $BACKUP_DIR/apacheds-default-backup.tgz $APACHEDS_DATADIR/default/*
service $APACHEDS start


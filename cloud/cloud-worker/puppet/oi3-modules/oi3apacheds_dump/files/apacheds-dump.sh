#!/bin/sh

# Dump directory
BACKUP_DIR=/opt/openinfinity/3.1.0/backup/dumps/apacheds
mkdir -p $BACKUP_DIR &>/dev/null

# ApacheDS version specific directories
APACHEDS=`cd /etc/init.d ; ls apacheds* | tail -1` || exit 1
APACHEDS_DATADIR=var/lib/`ls -t /var/lib/ | grep apacheds | head -1`/default || exit 1

# Clean old backups before taking a new one
cd $BACKUP_DIR && find . -type f -mtime +7 -name "apacheds-default-backup_*.tgz" -delete

# ApacheDS doesn't support hot backup, that leaves us with this situation
service $APACHEDS stop || exit 1
cd / && tar -czf $BACKUP_DIR/apacheds-default-backup_`date +"%Y-%m-%d"`.tgz $APACHEDS_DATADIR/*
service $APACHEDS start


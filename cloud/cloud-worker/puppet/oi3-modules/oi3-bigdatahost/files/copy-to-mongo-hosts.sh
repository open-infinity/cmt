#!/bin/sh

# 
# This is a test script for development environment and shouldn't be in
# version control.
#

MONGO0=10.33.208.141
MONGO1=10.33.208.146
MONGO2=10.33.208.147
MONGO3=10.33.208.148
MONGO4=10.33.208.15
MONGO5=10.33.208.22
MONGO6=10.33.208.29

KEY=~/Downloads/instance990_mongodb-klusteri.key

cat >/tmp/create-backup-dirs.sh <<EOF
mkdir -p /opt/openinfinity/3.1.0/backup
mkdir -p /opt/openinfinity/3.1.0/backup/tmp
mkdir -p /opt/openinfinity/3.1.0/backup/common

mkdir -p /opt/openinfinity/3.1.0/backup/cluster-backup-before.d
mkdir -p /opt/openinfinity/3.1.0/backup/cluster-backup-after.d

mkdir -p /opt/openinfinity/3.1.0/backup/node-backup-before.d
mkdir -p /opt/openinfinity/3.1.0/backup/node-backup-after.d

mkdir -p /opt/openinfinity/3.1.0/backup/cluster-restore-before.d
mkdir -p /opt/openinfinity/3.1.0/backup/cluster-restore-after.d

mkdir -p /opt/openinfinity/3.1.0/backup/node-restore-before.d
mkdir -p /opt/openinfinity/3.1.0/backup/node-restore-after.d

mkdir -p /opt/openinfinity/3.1.0/backup/exclude-rules.d
mkdir -p /opt/openinfinity/3.1.0/backup/include-dirs.d
EOF
chmod a+x /tmp/create-backup-dirs.sh

scp_to() {
    scp -p -i $KEY $2 root@$1:$3 || exit 1
}

copy_files_to_host() {
    echo "---- $1 -----------------------------------------------------------------------"

    scp_to $1 /tmp/create-backup-dirs.sh /tmp/
    
    ssh -i $KEY root@$1 /tmp/create-backup-dirs.sh || exit 1
    
    scp_to $1 after-bigdata-cluster-backup /opt/openinfinity/3.1.0/backup/cluster-backup-after.d/
    scp_to $1 before-bigdata-cluster-backup /opt/openinfinity/3.1.0/backup/cluster-backup-before.d/
    scp_to $1 before-bigdata-node-backup /opt/openinfinity/3.1.0/backup/node-backup-before.d/
    scp_to $1 after-bigdata-node-backup /opt/openinfinity/3.1.0/backup/node-backup-after.d/
    scp_to $1 bigdata-common /opt/openinfinity/3.1.0/backup/common/
    
    scp_to $1 "../../oi3-backup/files/*" /opt/openinfinity/3.1.0/backup/
    
    ssh -i $KEY root@$1 "chmod -R a+x /opt/openinfinity/3.1.0/backup/*" || exit 1
    
    ssh -i $KEY root@$1 "yum -y --quiet install xz nano tree screen nmap telnet" || exit 1
    
    ssh -i $KEY root@$1 "echo \"type = mongodb\" >> /etc/bigdata" || exit 1
    ssh -i $KEY root@$1 "echo \"mongo-config-servers = mongo1,mongo2,mongo3\" >> /etc/bigdata" || exit 1
}

copy_files_to_host $MONGO4
copy_files_to_host $MONGO5
copy_files_to_host $MONGO6

copy_files_to_host $MONGO1
copy_files_to_host $MONGO2
copy_files_to_host $MONGO3

copy_files_to_host $MONGO0


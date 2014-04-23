#!/usr/bin/perl -w
use strict;
use YAML qw(Dump);
use DBI;

my $hostname = shift || die "No hostname passed";
#$hostname =~ /^(\d+)\_(\d+)\_(\d+)\_(\w+)$/ or die ("Invalid hostname: $hostname");
$hostname =~ /^(\d+)\_(\d+)\_(\d+)\_(\w+)$/;

#print "$hostname\n";

my ($instance, $cluster, $machine, $type) = ($1,$2,$3,$4);

#print $instance;
#print $cluster;
#print $machine;
#print $type;

my $data_source = "dbi:mysql:database=openinfinity;host=localhost";
my $username = "openinfinity";
my $password = "cloudtools";

my $dbh = DBI->connect($data_source, $username, $password) or die $DBI::errstr;
my @class;
my %parameters;
my %postparameters;
my %hm_parameters;
my %ebsparameters;

if($hostname eq "galeratesti1") {
        push(@class, "oi3-basic");
        push(@class, "oi3-mariadbgalera");

        %parameters = (
                rootpass => "xxxx",
                galeraClusterAddress => "xxxx",
                galeraNodeAddress => "xxxx",
                galeraNodeName => "xxxx",
                galeraRootPassword => "xxxx"
        );

        print Dump({
                classes         => \@class,
                parameters      => \%parameters,
        });
        exit 0;
}

# HEALTH MONITORING
#
# Excluded:
# omatrafitestdb1.trafi.fi
# omatrafitest1.trafi.fi
#
# Included:
# instance 93
if ($instance eq "93" && $hostname ne "omatrafitestdb1.trafi.fi" && $hostname ne "omatrafitest1.trafi.fi") {
	push(@class, "oi3-healthmonitoring");
        # Query from machine_tbl, create entris for nodelist.conf from results
        my $sql = "select machine_id,machine_dns_name,machine_private_dns_name,machine_type,machine_name from machine_tbl where machine_cluster_id = $cluster and machine_running > 0";
        my $sth = $dbh->prepare($sql);
        $sth->execute() or die "Error in SQL execute: $DBI::errstr";
        my @datalist;
        my $node_hostname = "NA";
        my $dns_name;

        my ($machine_id, $machine_dns_name, $machine_private_dns_name, $machine_type, $machine_name);
        $sth->bind_col(1, \$machine_id);
        $sth->bind_col(2, \$machine_dns_name);
        $sth->bind_col(3, \$machine_private_dns_name);
        $sth->bind_col(4, \$machine_type);
        $sth->bind_col(5, \$machine_name);
        while ($sth->fetch) { 
                my $hostname;
                if ($machine == $machine_id){
                      $dns_name = $machine_dns_name;
                }
		if ($machine_name =~ m/^(mongo|hbase)\d+$/){
                      $hostname = $machine_name;
                }
                else{
                      $hostname = $machine_private_dns_name;
                      $hostname =~ s/\./-/g;
                      $hostname = "ip-" . $hostname;
                }
                my $hostdata = $machine_private_dns_name . " " . $machine_dns_name . " " . $hostname . " " . 
                               $machine_type . " " .  $machine_id;
                if ($machine eq $machine_id) {
                        $node_hostname = $hostname;
                }
                push(@datalist, $hostdata);
        }

        $sth->finish;
        if ($node_hostname eq "NA"){
                die "Host name assigning failed";
        }

        # Query machine type in cluster
       	$sql = "select cluster_machine_type from cluster_tbl where cluster_id = $cluster";
        $sth = $dbh->prepare($sql);
        $sth->execute() or die "Error in SQL execute: $DBI::errstr";
        my @row = $sth->fetchrow_array();
        my $machineType = $row[0];
        $sth->finish;

        # Query RAM allocated to machine
        $sql = "select ram from machine_type_tbl where id = $machineType";
	$sth = $dbh->prepare($sql);
	$sth->execute() or die "Error in SQL execute: $DBI::errstr";
	@row = $sth->fetchrow_array();
	my $ram = $row[0];
	$sth->finish;
        
        # Query cloud zone
        $sql = "select cloud_zone from instance_tbl where instance_id = $instance";
        $sth = $dbh->prepare($sql);
        $sth->execute() or die "Error in SQL execute: $DBI::errstr";
        @row = $sth->fetchrow_array();
        my $cloud = $row[0];
        $sth->finish; 
	
        %hm_parameters = (
		#FIXME: use cluster_id instead host_group
                host_group => "cluster_" . $cluster,
                cloud_zone => $cloud,
                instance_id => $instance,
                cluster_id => $cluster,
                machine_id => $machine,
                public_ip => $dns_name,
                hostname => $node_hostname,
		java_home => "/etc/alternatives/jre_1.7.0",
		mail_host => "xxxx",
		mail_from => "xxxx",
		default_mail_recepient => "xxxx",
		oi_collectd_root => "/opt/openinfinity/3.0.0/healthmonitoring/collectd",
                oi_rrd_http_server_root => "/opt/openinfinity/3.0.0/healthmonitoring/rrd-http-server",
		oi_monitoring_root => "/opt/openinfinity/3.0.0/healthmonitoring/nodechecker",
                cluster_member_data => \@datalist,
                tomcat_monitor_role_pwd => "xxxx",
                tomcat_jmx_port => "xxxx",
                threshold_warn_max_jvm_committed =>, roundup($ram * .75 * .95 * 1000000 , 10),
	);

}

# PORTAL

if($hostname eq "omatrafitest1.trafi.fi") {
        push(@class, "oi3-basic", "oi3-ebs", "oi3-portal");

        %parameters = (
	    activiti_password => "xxxx",
	    amq_password => "xxxx",
	    backup_dir => "xxxx",
	    backup_host => "xxxx",
	    backup_source_dir => "xxxx",
	    backup_user => "xxxx",
	    dbaddress => "xxxx",
	    ebsDeviceName => "''",
	    ebsVolumeUsed => "false",
	    jvmmem => "1540",
	    jvmperm => "512",
	    liferay_db_password => "xxxx",
	    multicastaddress => "xxxx",
	    oi_dbuser_password => "xxxx",
	    oi_httpuser_pwd => "xxxx",
	    ebsImageUsed => "true",
	    tomcat_monitor_role_pwd => "xxxx"


        );

        print Dump({
                classes         => \@class,
                parameters      => \%parameters,
        });
        exit 0;
}

# RDBMS

if($hostname eq "omatrafitestdb1.trafi.fi") {
        push(@class, "oi3-basic", "oi3-ebs", "oi3-rdbms", "oi3-activiti-rdbms", "oi3-activemq-rdbms", "oi3-portal-rdbms");
#        push(@class, "oi3-basic", "oi3-ebs", "oi3-rdbms", "oi3-activemq-rdbms", "oi3-portal-rdbms");

        %parameters = (
	    
	    activiti_password => "xxxx",
	    amq_password => "xxxx",
	    backup_dir => "backup/55/186",
	    backup_host => "xxxx",
	    backup_source_dir => "/opt/openinfinity/2.0.0/backup/dumps",
	    backup_user => "xxxxx",
	    ebsDeviceName => "''",
	    ebsVolumeUsed => "false",
	    liferay_db_password => "xxxx",
	    mysql_password => "xxxx",
	    oi_httpuser_pwd => "xxxx",
	    ebsImageUsed => "true"
	   
        );

        print Dump({
                classes         => \@class,
                parameters      => \%parameters,
        });
        exit 0;
}

# WMS EE
if($hostname eq "wms.ee.amazon.instance") {
        push(@class, "oi3-basic", "oi3-ebs", "oi3-bas", "oi3-tomee", "oi3-rdbms");

          %parameters = (
	      ebsDeviceName => "",
	      ebsVolumeUsed => "false",
	      jvmmem => "512",
	      jvmperm => "128",
	      multicastaddress => "xxxx",
	      ebsImageUsed => "true",
	      tomcat_monitor_role_pwd => "xxxx",
	      extra_catalina_opts => "",
	  #    extra_jvm_opts => "-DappNode=localhost -Dlog4j.configuration=/opt/openinfinity/3.0.0/tomcat/logs/wms/wms_log4j.xml -Dlog4j.debug=true -Dfocus.log=true -Dnora.log=false -Dapp.server=dynamic -Dwms.core.config.dbvalidation=false -Dapp.server.jndi.remotePre=java:global/WMS10g_ear/WMSApplicationEJB -Dapp.server.jndi.localPre=java:global/WMS10g_ear/WMSApplicationEJB -Dapp.server.jndi.datasourcebase=java: -Dwms.psl.env=localdev -Dpsl.enabled=true -Dpsl.dequeuing=true -Dwms.customer=standalone",
	      extra_jvm_opts => "",
	      mysql_password => "xxxx"
        );

        print Dump({
                classes         => \@class,
                parameters      => \%parameters,
        });
        exit 0;
}



push(@class, "oi3-basic");

my $sql = "select machine_extra_ebs_volume_device from machine_tbl where machine_type = 'clustermember' and machine_id = $machine";
my $sth = $dbh->prepare($sql);
$sth->execute() or die "Error in SQL execute: $DBI::errstr";
my @row = $sth->fetchrow_array;
if(!defined($row[0])) {
        %ebsparameters = (
                ebsVolumeUsed => 'false',
                ebsDeviceName => '',
        );
} else {
        %ebsparameters = (
                ebsVolumeUsed => 'true',
                ebsDeviceName => $row[0],
        );
}
$sth->finish;
$sql = "select cluster_ebs_image_used from cluster_tbl where cluster_id = $cluster";
$sth = $dbh->prepare($sql);
$sth->execute() or die "Error in SQL execute: $DBI::errstr";
@row = $sth->fetchrow_array;
if($row[0] == 1) {
        $ebsparameters{"ebsImageUsed"} = 'true';
} else {
        $ebsparameters{"ebsImageUsed"} ='false';
}
$sth->finish;
push(@class, "oi3-ebs");

if($type eq "portal_lb" || $type eq "service_lb") {
	my $sql = "select machine_private_dns_name from machine_tbl where machine_cluster_id = $cluster and machine_running = 1 and machine_type = 'clustermember'";
	my $sth = $dbh->prepare($sql);
	$sth->execute() or die "Error in SQL execute: $DBI::errstr";
	my @addrlist;
	while(my @row = $sth->fetchrow_array) {
		push(@addrlist,@row);
	}
	$sth->finish;
	%parameters = (
		portal_addresses => \@addrlist
	);
	push(@class, "loadbalancer");
} elsif($type eq "bas_lb" || $type eq "ee_lb") {
	my $sql = "select machine_private_dns_name from machine_tbl where machine_cluster_id = $cluster and machine_type = 'clustermember' and machine_running = 1";
	my $sth = $dbh->prepare($sql);
	$sth->execute() or die "Error in SQL execute: $DBI::errstr";
	my @addrlist;
	while(my @row = $sth->fetchrow_array) {
		push(@addrlist,@row);
	}
	$sth->finish;
	%parameters = (
		portal_addresses => \@addrlist
	);
	push(@class, "oi3-baslb");
} elsif($type eq "portal" || $type eq "service") {
	my $sql = "select cluster_id from cluster_tbl where instance_id = $instance and cluster_type=4";
	my $sth = $dbh->prepare($sql);
	$sth->execute() or die "Error in SQL execute: $DBI::errstr";
	my @row = $sth->fetchrow_array();
	my $db_cluster_id = $row[0];
	$sth->finish;
	$sql = "select machine_private_dns_name from machine_tbl where machine_cluster_id = $db_cluster_id";
	$sth = $dbh->prepare($sql);
	$sth->execute() or die "Error in SQL execute: $DBI::errstr";
	@row = $sth->fetchrow_array();
	my $db_address = $row[0];
	$sth->finish;
	# Get multicast address and machine type
	$sql = "select cluster_multicast_address,cluster_machine_type from cluster_tbl where cluster_id = $cluster";
        $sth = $dbh->prepare($sql);
        $sth->execute() or die "Error in SQL execute: $DBI::errstr";
        @row = $sth->fetchrow_array();
        my $multicastaddress = $row[0];
	my $machineType = $row[1];
	$sth->finish;

	# Query memory size
        $sql = "select ram from machine_type_tbl where id = $machineType";
        $sth = $dbh->prepare($sql);
        $sth->execute() or die "Error in SQL execute: $DBI::errstr";
        @row = $sth->fetchrow_array();
        my $ram = $row[0];
        $sth->finish;
        my $javaMem = jvmMem($ram);
        my $javaPerm = jvmPerm($javaMem);

	$sql = "select cluster_id from cluster_tbl where instance_id = $instance and cluster_type=7";
	$sth = $dbh->prepare($sql);
	$sth->execute() or die "Error in SQL execute: $DBI::errstr";
	if($sth->rows == 0) {
		%parameters = (
			dbaddress => $db_address,
			multicastaddress => $multicastaddress,
			backup_source_dir => "/opt/openinfinity",
			jvmmem => $javaMem,
                        jvmperm => $javaPerm,
			oi_httpuser_pwd => "xxxx",
			amq_password => "xxxx",
			activiti_password => "xxxx",
			oi_dbuser_password => "xxxx",
			liferay_db_password => "xxxx",
		);
		if($type eq "portal") {
			push(@class, "oi3-portal");
		} else {
			push(@class, "oi3-serviceplatform");
		}
	} else {
		@row = $sth->fetchrow_array();
		my $ig_clusterid = $row[0];
		$sth->finish;
		$sql = "select machine_dns_name from machine_tbl where machine_cluster_id = $ig_clusterid";
		$sth = $dbh->prepare($sql);
		$sth->execute() or die "Error in SQL execute: $DBI::errstr";
		@row = $sth->fetchrow_array();
		my $igaddress = $row[0];
		$sth->finish;
		$sql = "select machine_dns_name from machine_tbl where machine_cluster_id = $cluster and machine_type = 'loadbalancer' and machine_running = 1";
		$sth = $dbh->prepare($sql);
		$sth->execute() or die "Error in SQL execute: $DBI::errstr";
		@row = $sth->fetchrow_array();
		my $liferayAddress = $row[0];
		$sth->finish;
		%parameters = (
			dbaddress => $db_address,
			identityGatewayAddress => $igaddress,
			identityGatewayPort => "80",
			liferayAddress => $liferayAddress,
			amAdminUsername => "xxxx",
			amAdminPassword => "xxxx",
			multicastaddress => $multicastaddress,
			backup_source_dir => "/opt/openinfinity",
			liferay_db_password => "xxxx",
			jvmmem => $javaMem,
                        jvmperm => $javaPerm,
		);
		if($type eq "portal") {
			push(@class, "oi3-portal-sso");
		} else {
			push(@class, "oi3-serviceplatform");
		}
	}
	#push(@class, "oibackup");
} elsif($type eq "ig") {
	my $sql = "select cluster_id from cluster_tbl where instance_id = $instance and cluster_type=0";
        my $sth = $dbh->prepare($sql);
        $sth->execute() or die "Error in SQL execute: $DBI::errstr";
        my @row = $sth->fetchrow_array();
	my $portalClusterId = $row[0];
	$sth->finish;
	$sql = "select machine_dns_name from machine_tbl where machine_cluster_id = $portalClusterId and machine_type = 'loadbalancer' and machine_running = 1";
	$sth = $dbh->prepare($sql);
	$sth->execute() or die "Error in SQL execute: $DBI::errstr";
	@row = $sth->fetchrow_array();
	my $portalAddress = $row[0];
	$sth->finish;
	$sql = "select machine_dns_name from machine_tbl where machine_id = $machine";
	$sth = $dbh->prepare($sql);
	$sth->execute() or die "Error in SQL execute: $DBI::errstr";
	@row = $sth->fetchrow_array();
	my $igAddress = $row[0];
	$sth->finish;
	$sql = "select cluster_id from cluster_tbl where instance_id = $instance and cluster_type=4";
	$sth = $dbh->prepare($sql);
        $sth->execute() or die "Error in SQL execute: $DBI::errstr";
        @row = $sth->fetchrow_array();
        my $db_cluster_id = $row[0];
        $sth->finish;
        $sql = "select machine_private_dns_name from machine_tbl where machine_cluster_id = $db_cluster_id";
        $sth = $dbh->prepare($sql);
        $sth->execute() or die "Error in SQL execute: $DBI::errstr";
        @row = $sth->fetchrow_array();
        my $db_address = $row[0];
	%parameters = (
                        dbaddress => $db_address,
                        idpIPAddress => $igAddress,
                        idpPort => "xxxx",
                        spAddress => $portalAddress,
                        spPort => "xxxx",
                        serverAddress => $igAddress,
                        serverPort => "xxxx",
                        dbUser => "xxxx",
                        dbPassword => "xxxx",
                        adminPassword => "xxxx",
                        userAgentPassword => "xxxx",
			backup_source_dir => "/opt/openinfinity/",
        );
	push(@class, "oibackup");
	push(@class, "oi-identity-gateway");
} elsif($type eq "bas") {
	# Query multicast address and machine type
	my $sql = "select cluster_multicast_address,cluster_machine_type from cluster_tbl where cluster_id = $cluster";
        my $sth = $dbh->prepare($sql);
        $sth->execute() or die "Error in SQL execute: $DBI::errstr";
        my @row = $sth->fetchrow_array();
        my $multicastaddress = $row[0];
	my $machineType = $row[1];
	$sth->finish;
	
	# Query cluster id of database cluster
	$sql = "select cluster_id from cluster_tbl where instance_id = $instance and cluster_type=4";
        $sth = $dbh->prepare($sql);
        $sth->execute() or die "Error in SQL execute: $DBI::errstr";
        @row = $sth->fetchrow_array();
        my $db_cluster_id = $row[0];
        $sth->finish;
        
	# Query DB private DNS name
	my $db_address;
	if ($db_cluster_id) {
		$sql = "select machine_private_dns_name from machine_tbl where machine_cluster_id = $db_cluster_id";
		$sth = $dbh->prepare($sql);
		$sth->execute() or die "Error in SQL execute: $DBI::errstr";
		@row = $sth->fetchrow_array();
		$db_address = $row[0];
		$sth->finish;
	}

	# Query memory size
	$sql = "select ram from machine_type_tbl where id = $machineType";
	$sth = $dbh->prepare($sql);
	$sth->execute() or die "Error in SQL execute: $DBI::errstr";
	@row = $sth->fetchrow_array();
	my $ram = $row[0];
	$sth->finish;
	my $javaMem = jvmMem($ram);
	my $javaPerm = jvmPerm($javaMem);
        
        # Puppet modules
        #push(@class, "oibackup");
	push(@class, "oi3-bas");
	
	# Puppet template parameters
	%parameters = (
			multicastaddress => $multicastaddress,
                        dbaddress => $db_address,
			backup_source_dir => "/opt/openinfinity",
			jvmmem => $javaMem,
			jvmperm => $javaPerm,
			tomcat_monitor_role_pwd => "xxxx",
	);
} elsif ($type eq "ee") {
	# Query multicast address and machine type
	my $sql = "select cluster_multicast_address,cluster_machine_type from cluster_tbl where cluster_id = $cluster";
        my $sth = $dbh->prepare($sql);
        $sth->execute() or die "Error in SQL execute: $DBI::errstr";
        my @row = $sth->fetchrow_array();
        my $multicastaddress = $row[0];
	my $machineType = $row[1];
        $sth->finish;
        
	# Query cluster id of database cluster
	$sql = "select cluster_id from cluster_tbl where instance_id = $instance and cluster_type=4";
        $sth = $dbh->prepare($sql);
        $sth->execute() or die "Error in SQL execute: $DBI::errstr";
        @row = $sth->fetchrow_array();
        my $db_cluster_id = $row[0];
        $sth->finish;

	# Query DB private DNS name
	my $db_address;
	if ($db_cluster_id) {
		$sql = "select machine_private_dns_name from machine_tbl where machine_cluster_id = $db_cluster_id";
		$sth = $dbh->prepare($sql);
		$sth->execute() or die "Error in SQL execute: $DBI::errstr";
		@row = $sth->fetchrow_array();
		$db_address = $row[0];
		$sth->finish;
        }

	# Query memory size
        $sql = "select ram from machine_type_tbl where id = $machineType";
        $sth = $dbh->prepare($sql);
        $sth->execute() or die "Error in SQL execute: $DBI::errstr";
        @row = $sth->fetchrow_array();
        my $ram = $row[0];
        $sth->finish;
        my $javaMem = jvmMem($ram);
        my $javaPerm = jvmPerm($javaMem);

	# Add Puppet modules        
	push(@class, "oi3-bas");
	push(@class, "oi3-tomee");
	#push(@class, "oibackup");
	
	# Set parameters
	%parameters = (
                        multicastaddress => $multicastaddress,
			dbaddress => $db_address,
			backup_source_dir => "/opt/openinfinity",
			jvmmem => $javaMem,
                        jvmperm => $javaPerm,
			extra_catalina_opts => "",
			extra_jvm_opts => "",
        );
} elsif($type eq "db") {
	push(@class, "oi3-rdbms");
	#push(@class, "oibackup");
	push(@class, "oi3-activiti-rdbms");
	push(@class, "oi3-activemq-rdbms");
        push(@class, "oi3-portal-rdbms");
        push(@class, "oi3-oauth-rdbms");
	
	my $dbrootpass = "";
        my $sql = "select password from password_tbl where platform = 'mariadb' and user = 'root' and cluster_id = $cluster";
        my $sth = $dbh->prepare($sql);
        $sth->execute() or die "Error in SQL execute: $DBI::errstr";
        my $rows = $sth->rows;
        my $passFound = 0;
        if($rows > 0) {
                my @row = $sth->fetchrow_array();
                if(defined($row[0])) {
                        $passFound = 1;
                        $dbrootpass = $row[0];
                }
        }
        $sth->finish;
        if(!$passFound) {
                $dbrootpass = generatePassword(8);
                $sql = "insert into password_tbl (id, cluster_id, platform, user, password) values (null, $cluster, 'mariadb', 'root', '$dbrootpass')";
                $sth = $dbh->prepare($sql);
                $sth->execute();
                $sth->finish;
        }

	# Set parameters
	%parameters = (
			mysql_password => $dbrootpass,
			# Production parameters
                        backup_source_dir => "/opt/openinfinity/2.0.0/backup/dumps", # TODO: for others: /opt/openinfinity
			amq_password => "toas",
			activiti_password => "toas",
			oi_httpuser_pwd => "toas",
			liferay_db_password => "toas",
                        oi_dbuser_pwd => "toas",
        );
	
	
} elsif($type eq "bigdata" || $type eq "nosql") {
	my $sql = "select secret_key from key_tbl where instance_id = $instance";
	my $sth = $dbh->prepare($sql);
	$sth->execute() or die "Error in SQL execute: $DBI::errstr";
	my @row = $sth->fetchrow_array();
	my $secretKey = $row[0];
	$sth->finish;
	%parameters = (
		rsaprivatekey => $secretKey
	);
	push(@class, "oi3-bigdatamgmt");
} elsif($type eq "bigdata_host" || $type eq "nosql_host") {
	my $sql = "select machine_name from machine_tbl where machine_id = $machine";
	my $sth = $dbh->prepare($sql);
	$sth->execute() or die "Error in SQL execute: $DBI::errstr";
	my @row = $sth->fetchrow_array();
	my $machineName = $row[0];
	$sth->finish;
	%parameters = (
		hostname => $machineName
	);
	push(@class, "oi3-bigdatahost");
} elsif($type eq "jbossportal") {
	push(@class, "oijavasehotspot");
	push(@class, "oibasicjboss");
	push(@class, "oiportaljboss");
	push(@class, "oijbosszufe");
	push(@class, "oijbosscoherence");
	$parameters {'jboss_home'} = "/opt/openinfinity/custom/ya/current/portal/jboss";
    $parameters {'platform_puppet_class'} = "oiportaljboss::install";
	

	#Get multicast address for portal 
	my $sql = "select cluster_multicast_address from cluster_tbl where cluster_id = $cluster";
    my $sth = $dbh->prepare($sql);
    $sth->execute() or die "Error in SQL execute: $DBI::errstr";
    my @row = $sth->fetchrow_array();
    $parameters {'multicastaddress'} = $row[0];

	#Get SOLR LB private ip address
	$sql = "select cluster_id from cluster_tbl where instance_id = $instance and cluster_type=12";
	$sth = $dbh->prepare($sql);
	$sth->execute() or die "Error in SQL execute: $DBI::errstr";
	@row = $sth->fetchrow_array();
	if($sth->rows == 0) {
		$sth->finish;
		$parameters {'solr_master_url'} = "";
	}else{

		my $solr_cluster_id = $row[0];
		$sth->finish;
		$sql = "select machine_private_dns_name from machine_tbl where machine_cluster_id = $solr_cluster_id";
		$sth = $dbh->prepare($sql);
		$sth->execute() or die "Error in SQL execute: $DBI::errstr";
		@row = $sth->fetchrow_array();
		my $solr_address = $row[0];
		$sth->finish;
		$parameters {'solr_master_url'} = join "", "http://", $solr_address,"/solr";
		$parameters {'solr_slave_url'} = join "", "http://",$solr_address,"/solr";
	}
		
    #Get Service Platform LB private ip address
    $sql = "select cluster_id from cluster_tbl where instance_id = $instance and cluster_type=11";
    $sth = $dbh->prepare($sql);
    $sth->execute() or die "Error in SQL execute: $DBI::errstr";
    @row = $sth->fetchrow_array();
    if($sth->rows == 0) {
    	$sth->finish;
    	$parameters {'service_platform_url'} = "";
    }else{
        my $service_cluster_id = $row[0];
        $sth->finish;
        $sql = "select machine_private_dns_name from machine_tbl where machine_cluster_id = $service_cluster_id";
        $sth = $dbh->prepare($sql);
        $sth->execute() or die "Error in SQL execute: $DBI::errstr";
        @row = $sth->fetchrow_array();
        my $service_address = $row[0];
        $sth->finish;
        $parameters {'service_platform_url'} = join "", "http://", $service_address;
	}


	# Query job table by instance id
	my $sql1 = "select job_id from job_tbl where job_instance_id = $instance";
	my $sth1 = $dbh->prepare($sql1);
	$sth1->execute() or die "Error in SQL execute: $DBI::errstr";
	my @row1 = $sth1->fetchrow_array();
	my $job_id = $row1[0];

	# Query job parameters from instance__parameter_tbl
	#my $sql2 = "select id from job_platform_parameter_tbl where job_id = $job_id and pkey = 'portal_live' and pvalue = 'true'";
	my $sql2 = "select id from instance_parameter_tbl where instance_id = $instance and pkey = 'portal_live' and pvalue = 'true'";
	my $sth2 = $dbh->prepare($sql2);
	$sth2->execute() or die "Error in SQL execute: $DBI::errstr";
	my @row2 = $sth2->fetchrow_array();
	my $job_parameter_id = $row2[0];

	if (defined $job_parameter_id) {
		# Query load balancer
		my $sql3 = "select machine_private_dns_name from machine_tbl where machine_type = 'loadbalancer' and machine_cluster_id = $cluster";
		my $sth3 = $dbh->prepare($sql3);
		$sth3->execute() or die "Error in SQL execute: $DBI::errstr";
		my @row3 = $sth3->fetchrow_array();

		# Set parameter for puppet
		$parameters {'lb_private_dns_name'} = $row3[0];
	}

        # Set parameters
	my $sql3 = "select pkey,pvalue from instance_parameter_tbl where instance_id = $instance";
	my $sth3 = $dbh->prepare($sql3);
	$sth3->execute() or die "Error in SQL execute: $DBI::errstr";
	my ($col1, $col2);
	$sth3->bind_col(1, \$col1);
	$sth3->bind_col(2, \$col2);
	while ($sth3->fetch) { # retrieve one row and add key,value pair to hash
    		#print "$col1, $col2\n";
		$parameters{ $col1 } = $col2;
	}

	#Add extra load balancer private dns name 
	#$parameters {'lb_private_dns_name'} = $loadbalancerPrivateDnsName;
        #%parameters = (
                        # FIXME: Testing puppet parameters.
                        #jdbc_connection_url => "jdbc:db2://10.33.208.20:50000/DEMO",
			#lb_private_dns_name => $loadbalancerPrivateDnsName,
			#portal_datasource_url => "url",
			#portal_datasource_user => "user",
			#portal_datasource_password => "password"
        #);


	%postparameters = (
		java_home => "/usr/java/jdk1.7.0_11"
        );

} 

$dbh->disconnect;

# Set backup parameters
my %backup_parameters = (
	# Production parameters
	backup_host => "xxxx",
	backup_user => "xxxx",
	backup_dir => "backup/" . $instance . "/" . $machine,
);

my %parameter_union = (%parameters, %hm_parameters, %postparameters, %ebsparameters);
#my %parameter_union = (%parameters, %postparameters, %ebsparameters);
my %final_parameters = (%parameter_union, %backup_parameters);
#my %parameter_union = %parameters;


print Dump({
	classes		=> \@class,
	parameters	=> \%final_parameters,
});

sub jvmMem {
	my $machineMem = shift;
        return roundup($machineMem * 0.75, 10);
}

sub roundup
{
   my $number = shift;
   my $round = shift;

   if ($number % $round) {
      return (1 + int($number/$round)) * $round;
   }
   else {
      return $number;
   }
}

sub jvmPerm {
	my $javaMem = shift;
	
	if($javaMem < 1000) {
		return 256;
	} else {
		return 512;
	}
}

sub generatePassword {
   my $length = shift;
   my $possible = 'abcdefghijkmnpqrstuvwxyz23456789ABCDEFGHJKLMNPQRSTUVWXYZ';
   my $password = "";
   while (length($password) < $length) {
     $password .= substr($possible, (int(rand(length($possible)))), 1);
   }
   return $password
}

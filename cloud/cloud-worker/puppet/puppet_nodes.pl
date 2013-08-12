#!/usr/bin/perl -w
use strict;
use YAML qw(Dump);
use DBI;

my $hostname = shift || die "No hostname passed";

$hostname =~ /^(\d+)\_(\d+)\_(\d+)\_(\w+)$/ or die ("Invalid hostname: $hostname");
#$hostname =~ /^(\d+)\_(\d+)\_(\d+)\_(\w+)$/;

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

push(@class, "oibasic");

if (1) {
	push(@class, "oihealthmonitoring");
	
        # Query from machine_tbl, create entris for nodelist.conf from results
        my $sql = "select machine_id, machine_private_dns_name, machine_type from machine_tbl where machine_cluster_id = $cluster and machine_running > 0";
	my $sth = $dbh->prepare($sql);
        $sth->execute() or die "Error in SQL execute: $DBI::errstr";
        my @datalist;
        my $node_hostname = "NA";
        my ($machine_id, $machine_private_ip, $machine_type);
        $sth->bind_col(1, \$machine_id);
        $sth->bind_col(2, \$machine_private_ip);
        $sth->bind_col(3, \$machine_type);
        while ($sth->fetch) { 
                my $hostname = $machine_private_ip;
                $hostname =~ s/\./-/g;
                $hostname = "ip-" . $hostname;
                my $hostdata = $machine_private_ip . " " . $hostname . " " . $machine_type;
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
         
	%hm_parameters = (
		host_group => "cluster_" . $cluster,
                hostname => $node_hostname,
		java_home => "/usr/lib/jvm/java-1.6.0-openjdk-1.6.0.0.x86_64/",
		mail_host => "gate33.gw.tietoenator.com",
		mail_from => "vedran.bartonicek\@tieto.com",
		default_mail_recepient => "vedran.bartonicek\@tieto.com",
		toas_collectd_root => "/opt/openinfinity/2.0.0/healthmonitoring/collectd",
                toas_rrd_http_server_root => "/opt/openinfinity/2.0.0/healthmonitoring/rrd-http-server",
		toas_monitoring_root => "/opt/openinfinity/2.0.0/healthmonitoring/nodechecker",
                cluster_member_data => \@datalist,
                tomcat_monitor_role_pwd => "OZidie9a",
                tomcat_jmx_port => "65329",
                threshold_warn_max_jvm_committed =>, roundup($ram * .75 * .95 * 1000000 , 10),
	);
}

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
	push(@class, "baslb");
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
		);
		if($type eq "portal") {
			push(@class, "oiportal");
		} else {
			push(@class, "oiserviceplatform");
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
			amAdminUsername => "amadmin",
			amAdminPassword => "admin1234",
			multicastaddress => $multicastaddress,
			backup_source_dir => "/opt/openinfinity",
			jvmmem => $javaMem,
                        jvmperm => $javaPerm,
		);
		if($type eq "portal") {
			push(@class, "oiportal-sso");
		} else {
			push(@class, "oiserviceplatform");
		}
	}
	push(@class, "oibackup");
	push(@class, "oilttwatch");
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
                        idpPort => "80",
                        spAddress => $portalAddress,
                        spPort => "80",
                        serverAddress => $igAddress,
                        serverPort => "80",
                        dbUser => "liferay",
                        dbPassword => "toasliferay",
                        adminPassword => "admin1234",
                        userAgentPassword => "toas1234",
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
        push(@class, "oibackup");
	push(@class, "oibas");
	
	# Puppet template parameters
	%parameters = (
			multicastaddress => $multicastaddress,
                        dbaddress => $db_address,
			backup_source_dir => "/opt/openinfinity",
			jvmmem => $javaMem,
			jvmperm => $javaPerm,
	);
	push(@class, "oilttwatch");
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
	push(@class, "oibas");
	push(@class, "oitomee");
	push(@class, "oilttwatch");
	push(@class, "oibackup");
	
	# Set parameters
	%parameters = (
                        multicastaddress => $multicastaddress,
			dbaddress => $db_address,
			backup_source_dir => "/opt/openinfinity",
			jvmmem => $javaMem,
                        jvmperm => $javaPerm,
        );
} elsif($type eq "db") {
	push(@class, "oimariadb");
	push(@class, "oibackup");
	
	# Set parameters
	%parameters = (
			# Production parameters
                        backup_source_dir => "/opt/openinfinity/2.0.0/backup/dumps", # TODO: for others: /opt/openinfinity
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
	push(@class, "toasbigdatamgmt");
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
	push(@class, "toasbigdatahost");
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

} elsif($type eq "jbossservice") {
	push(@class, "oijavasehotspot");
	push(@class, "oibasicjboss");
	push(@class, "oiservicejboss");
	push(@class, "oijbosszufe");
	# push(@class, "oijbosscoherence");
	$parameters {'jboss_home'} = "/opt/openinfinity/custom/ya/current/service/jboss";
	$parameters {'platform_puppet_class'} = "oiservicejboss::install";
    

	# Query job table by instance id
	my $sql1 = "select job_id from job_tbl where job_instance_id = $instance";
	my $sth1 = $dbh->prepare($sql1);
	$sth1->execute() or die "Error in SQL execute: $DBI::errstr";
	my @row1 = $sth1->fetchrow_array();
	my $job_id = $row1[0];

        # Set parameters
	#my $sql3 = "select pkey,pvalue from job_platform_parameter_tbl where job_id = $job_id";
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
	%postparameters = (
		java_home => "/usr/java/jdk1.7.0_11"
        );
} elsif ($type eq "jbosssolr") {
    push(@class, "oibasic");
    push(@class, "oijavasehotspot");
    push(@class, "oibasicjboss");
    push(@class, "oisolrjboss");

    # solve machine type for this instance
    my $sql = "select machine_type from machine_tbl where machine_id = $machine";
    my $sth = $dbh->prepare($sql);
    $sth->execute() or die "Error in SQL execute: $DBI::errstr";
    my @rows;
    while(my @row = $sth->fetchrow_array) {
	push(@rows,@row);
    }
    my $mtype = $rows[0];

    %parameters= (

	);

    my $solr_master;
    if ($mtype eq "clustermemberslave") {
	$sql = "select machine_private_dns_name from machine_tbl where machine_cluster_id = $cluster and machine_running = 1 and machine_type = 'clustermembermaster'";
	$sth = $dbh->prepare($sql);
	$sth->execute() or die "Error in SQL execute: $DBI::errstr";
	@rows = ();
	while(my @row = $sth->fetchrow_array) {
	    push(@rows,@row);
	}
	$solr_master = $rows[0];
        $parameters {'solr_master_url'} = "http://$solr_master:8080/solr/replication";
    }


    %postparameters= (
	java_home => "/usr/java/jdk1.7.0_11"
	);

} elsif ($type eq "jbosssolr_lb") {
    my $sql = "select machine_dns_name from machine_tbl where machine_cluster_id = $cluster and machine_running = 1 and machine_type = 'loadbalancer'";

    my $sth = $dbh->prepare($sql);
    $sth->execute() or die "Error in SQL execute: $DBI::errstr";
    my @addrlist;
    while(my @row = $sth->fetchrow_array) {
	push(@addrlist,@row);
    }
    my $solr_lb = $addrlist[0];

    $sql = "select machine_private_dns_name from machine_tbl where machine_cluster_id = $cluster and machine_running = 1 and machine_type = 'clustermembermaster'";
    $sth = $dbh->prepare($sql);
    $sth->execute() or die "Error in SQL execute: $DBI::errstr";
    @addrlist = ();
    while(my @row = $sth->fetchrow_array) {
	push(@addrlist,@row);
    }
    my $solr_master = $addrlist[0];

    $sql = "select machine_private_dns_name from machine_tbl where machine_cluster_id = $cluster and machine_running = 1 and machine_type = 'clustermemberslave'";
    $sth = $dbh->prepare($sql);
    $sth->execute() or die "Error in SQL execute: $DBI::errstr";
    @addrlist = ();
    while(my @row = $sth->fetchrow_array) {
	push(@addrlist,@row);
    }
    
    push(@class, "oibasic");
    push(@class, "solrlb");

    %parameters= ( 
	solr_lb => "$solr_lb",
	solr_master => "$solr_master",
	solr_slave_addresses => \@addrlist,
	);

    %postparameters= (
	java_home => "/usr/java/jdk1.7.0_11"
	);




} elsif ($type eq "jbossportal_lb" || $type eq "jbossservice_lb") {
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
}

$dbh->disconnect;

# Set backup parameters
my %backup_parameters = (
	# Production parameters
	backup_host => "131.207.105.13",
	backup_user => "toas-backup",
	backup_dir => "backup/" . $instance . "/" . $machine,
);

my %parameter_union = (%parameters, %hm_parameters, %postparameters, %ebsparameters);
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

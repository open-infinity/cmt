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

push(@class, "oibasic");

if($hostname eq "localhost.localdomain") {
	push(@class, "oibas");
#	%parameters = (
#		pkgname => "mytomcat-helloworld.war"
#	);
}

if($hostname eq "tomee-test") {
	push(@class, "oibas");
	push(@class, "oitomee");
}

if($hostname eq "healthmonitoring-test-5") {
	push(@class, "oibas");
	push(@class, "oihealthmonitoring");
	%parameters = (
	    communication_iface => "eth0",
	    host_group => "toas",
		java_home => "/usr/lib/jvm/java-1.6.0-openjdk-1.6.0.0.x86_64/",
        mail_host => "gate33.gw.tietoenator.com",
        mail_from => "openinfinity-admin\@tieto.com",
        mail_sender_cron_expression => "0 * * * * ? *",
        default_mail_recepient => "vitali.kukresh\@tieto.com",
        toas_collectd_root => "/opt/collectd",
        toas_monitoring_root => "/opt/monitoring",
	);
}

if($hostname eq "sso-test3") {
	push(@class, "oi-identity-gateway");
	%parameters = (
		serverAddress => "10.33.208.105",
		serverPort => "8080",
		adminPassword => "admin1234",
		userAgentPassword => "toas1234",
		idpIPAddress => "10.33.208.105",
		idpPort => "8080",
		dbAddress => "10.33.208.100",
		dbUser => "liferay",
		dbPassword => "toasliferay",
		spAddress => "10.33.208.102",
		spPort => "80",
	);
}

if($type eq "portal_lb" || $type eq "service_lb") {
	my $sql = "select machine_private_dns_name from machine_tbl where machine_cluster_id = $cluster and machine_type = 'clustermember' and machine_running = 1";
	my $sth = $dbh->prepare($sql);
	$sth->execute() or die "Error in SQL execute: $DBI::errstr";
	my @addrlist;
	while(my @row = $sth->fetchrow_array) {
		push(@addrlist,@row);
	}
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
	$sql = "select machine_private_dns_name from machine_tbl where machine_cluster_id = $db_cluster_id";
	my $sth2 = $dbh->prepare($sql);
	$sth2->execute() or die "Error in SQL execute: $DBI::errstr";
	@row = $sth2->fetchrow_array();
	my $db_address = $row[0];
	%parameters = (
		dbaddress => $db_address
	);
	if($type eq "portal") {
		push(@class, "oiportal");
	} else {
		push(@class, "oiserviceplatform");
	}
} elsif($type eq "bas") {
	push(@class, "oibas");
} elsif ($type eq "ee") {
	push(@class, "oibas");
	push(@class, "oitomee");
} elsif($type eq "db") {
	push(@class, "oimariadb");
} elsif($type eq "bigdata" || $type eq "nosql") {
	my $sql = "select secret_key from key_tbl where instance_id = $instance";
	my $sth = $dbh->prepare($sql);
	$sth->execute() or die "Error in SQL execute: $DBI::errstr";
	my @row = $sth->fetchrow_array();
	my $secretKey = $row[0];
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
	%parameters = (
		hostname => $machineName
	);
	push(@class, "toasbigdatahost");
}
$dbh->disconnect;

print Dump({
	classes		=> \@class,
	parameters	=> \%parameters,
});


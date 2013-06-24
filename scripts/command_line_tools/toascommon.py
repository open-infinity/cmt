import MySQLdb as mdb
import boto
from boto.regioninfo import RegionInfo
from boto import ec2
from toasconfig import *
from toasdomain import *

con = mdb.connect(dbHost, dbUser, dbPassword, dbName)
region = RegionInfo(None, "eucalyptus", eucaHost)
ec2 = boto.connect_ec2(aws_access_key_id=eucaAccessKey, aws_secret_access_key=eucaSecretKey, is_secure=False, debug=0, region=region, port=eucaPort, path=eucaPath, api_version=eucaApiVersion)


def getInstance(id):
	cur = con.cursor()
	sql = "select instance_id,user_id,instance_name,cloud_type,cloud_zone,organization_id,instance_status,instance_active from instance_tbl where instance_active = 1 and instance_id = "+str(id)
	cur.execute(sql)
	rows = cur.fetchall()
	if(len(rows) < 1):
		cur.close()
		return None
	row = rows[0]
	temp = Instance()
	temp.id = row[0]
        temp.userId = row[1]
        temp.name = row[2]
        temp.cloudType = row[3]
        temp.cloudZone = row[4]
        temp.organizationId = row[5]
        temp.instanceStatus = row[6]
        temp.instanceActive = row[7]

	cur.close()
	return temp

def getCluster(id):
	cur = con.cursor()
	sql = "select cluster_id, cluster_name, cluster_number_of_machines, cluster_lb_name, cluster_lb_dns, instance_id, cluster_type, cluster_pub, cluster_live, cluster_lb_instance_id, cluster_security_group_id, cluster_security_group_name, cluster_multicast_address, cluster_machine_type, cluster_ebs_image_used, cluster_ebs_volumes_used from cluster_tbl where cluster_id = "+str(id)
	cur.execute(sql)
	rows = cur.fetchall()
	if(len(rows) < 1):
		cur.close()
		return None
	temp = Cluster()
	row = rows[0]
	temp.id = row[0]
        temp.name = row[1]
        temp.machines = row[2]
        temp.lbName = row[3]
        temp.lbDns = row[4]
        temp.instanceId = row[5]
        temp.type = row[6]
        temp.pub = row[7]
        temp.live = row[8]
        temp.lbInstanceId = row[9]
        temp.groupId = row[10]
        temp.groupName = row[11]
        temp.multicastAddress = row[12]
        temp.machineType = row[13]
        temp.ebsImageUsed = row[14]
        temp.ebsVolumeUsed = row[15]
	
	cur.close()
	return temp

def getMachine(id):
	cur = con.cursor()
	sql = "select machine_id, machine_instance_id, machine_name, machine_dns_name, active, machine_username, machine_running,  machine_state, machine_cluster_id, machine_private_dns_name, machine_type, machine_configured, machine_cloud_type, machine_extra_ebs_volume_id, machine_extra_ebs_volume_device, machine_extra_ebs_volume_size from machine_tbl where machine_id = "+str(id)
	cur.execute(sql)
	rows = cur.fetchall()
	if(len(rows) < 1):
		cur.close()
		return None
	temp = Machine()
	row = rows[0]
	temp.id = row[0]
        temp.instanceId = row[1]
        temp.name = row[2]
        temp.dnsName = row[3]
        temp.active = row[4]
        temp.username = row[5]
        temp.running = row[6]
        temp.state = row[7]
        temp.clusterId = row[8]
        temp.privateDnsName = row[9]
        temp.type = row[10]
        temp.configured = row[11]
        temp.cloudType = row[12]
        temp.extraEbsVolumeId = row[13]
        temp.extraEbsVolumeDevice = row[14]
        temp.extraEbsVolumeSize = row[15]
	
	cur.close()
	return temp

def insertMachine(instanceId, name, dnsName, userName, state, clusterId, privateDnsName, machineType, machineConfigured, cloudType, volumeId, volumeDevice, volumeSize):
	cur = con.cursor()
	sql = "insert into machine_tbl values (null, '%s', 0, '%s', '%s', 0, 1, '%s', 1, '%s', %d, '%s', '%s', %d, NOW(), %d, '%s', '%s', %d)" % (instanceId, name, dnsName, userName, state, clusterId, privateDnsName, machineType, machineConfigured, cloudType, volumeId, volumeDevice, volumeSize)
	res = cur.execute(sql)
	if(res < 1):
		cur.close()
		return False
	else:
		con.commit()
		cur.close()
		return True

def insertAuthorizedIP(instanceId, clusterId, cidrIP, protocol, securityGroupName, fromPort, toPort):
	cur = con.cursor()
	sql = "insert into user_authorized_ip_tbl values(null, %d, %d, '%s', '%s', '%s', %d, %d)" % (instanceId, clusterId, cidrIP, protocol, securityGroupName, fromPort, toPort)
	res = cur.execute(sql)
	if(res < 1):
		cur.close()
		return False
	else:
		con.commit()
		cur.close()
		return True


def updateClusterLbInfo(id, lbDns, lbInstanceId):
	cur = con.cursor()
	sql = "update cluster_tbl set cluster_lb_dns = '%s', cluster_lb_instance_id = '%s' where cluster_id = %d" % (lbDns, lbInstanceId, id)
	res = cur.execute(sql)
	if(res < 1):
		cur.close()
		return False
	else:
		con.commit()
		cur.close()
		return True

def getLoadbalancer(clusterId):
	cluster = getCluster(clusterId)
	cur = con.cursor()
	sql = "select machine_id, machine_instance_id, machine_name, machine_dns_name, active, machine_username, machine_running,  machine_state, machine_cluster_id, machine_private_dns_name, machine_type, machine_configured, machine_cloud_type, machine_extra_ebs_volume_id, machine_extra_ebs_volume_device, machine_extra_ebs_volume_size from machine_tbl where machine_instance_id = '"+cluster.lbInstanceId+"'"
	cur.execute(sql)
	rows = cur.fetchall()
	if(len(rows) < 1):
		cur.close()
		return None
	temp = Machine()
	row = rows[0]
	temp.id = row[0]
        temp.instanceId = row[1]
        temp.name = row[2]
        temp.dnsName = row[3]
        temp.active = row[4]
        temp.username = row[5]
        temp.running = row[6]
        temp.state = row[7]
        temp.clusterId = row[8]
        temp.privateDnsName = row[9]
        temp.type = row[10]
        temp.configured = row[11]
        temp.cloudType = row[12]
        temp.extraEbsVolumeId = row[13]
        temp.extraEbsVolumeDevice = row[14]
        temp.extraEbsVolumeSize = row[15]

	cur.close()
	return temp


def deleteMachineFromDatabase(id):
	cur = con.cursor()
	sql = "delete from machine_tbl where machine_id = "+str(id)
	res = cur.execute(sql)
	con.commit()
	cur.close()
	return res

def getInstances(con):
        cur = con.cursor()
        cur.execute("select instance_id,user_id,instance_name,cloud_type,cloud_zone,organization_id,instance_status,instance_active from instance_tbl where instance_active = 1")
        rows = cur.fetchall()
        instances = []
        for row in rows:
                temp = Instance()
                temp.id = row[0]
                temp.userId = row[1]
                temp.name = row[2]
                temp.cloudType = row[3]
                temp.cloudZone = row[4]
                temp.organizationId = row[5]
                temp.instanceStatus = row[6]
                temp.instanceActive = row[7]
                instances.append(temp)

	cur.close()
        return instances

def getInstanceClusters(id):
        cur = con.cursor()
        sql = "select cluster_id, cluster_name, cluster_number_of_machines, cluster_lb_name, cluster_lb_dns, instance_id, cluster_type, cluster_pub, cluster_live, cluster_lb_instance_id, cluster_security_group_id, cluster_security_group_name, cluster_multicast_address, cluster_machine_type, cluster_ebs_image_used, cluster_ebs_volumes_used from cluster_tbl where instance_id = " + str(id)
        cur.execute(sql)
        rows = cur.fetchall()
        clusters = []
        for row in rows:
                temp = Cluster()
                temp.id = row[0]
                temp.name = row[1]
                temp.machines = row[2]
                temp.lbName = row[3]
                temp.lbDns = row[4]
                temp.instanceId = row[5]
                temp.type = row[6]
                temp.pub = row[7]
                temp.live = row[8]
                temp.lbInstanceId = row[9]
                temp.groupId = row[10]
                temp.groupName = row[11]
                temp.multicastAddress = row[12]
                temp.machineType = row[13]
                temp.ebsImageUsed = row[14]
                temp.ebsVolumeUsed = row[15]
                clusters.append(temp)

	cur.close()
        return clusters

def getClusterMachines(con, id):
        cur = con.cursor()
        sql = "select machine_id, machine_instance_id, machine_name, machine_dns_name, active, machine_username, machine_running,  machine_state, machine_cluster_id, machine_private_dns_name, machine_type, machine_configured, machine_cloud_type, machine_extra_ebs_volume_id, machine_extra_ebs_volume_device, machine_extra_ebs_volume_size from machine_tbl where machine_cluster_id = " + str(id)
        cur.execute(sql)
        rows = cur.fetchall()
        machines = []
        for row in rows:
                temp = Machine()
                temp.id = row[0]
                temp.instanceId = row[1]
                temp.name = row[2]
                temp.dnsName = row[3]
                temp.active = row[4]
                temp.username = row[5]
                temp.running = row[6]
                temp.state = row[7]
                temp.clusterId = row[8]
                temp.privateDnsName = row[9]
                temp.type = row[10]
                temp.configured = row[11]
                temp.cloudType = row[12]
                temp.extraEbsVolumeId = row[13]
                temp.extraEbsVolumeDevice = row[14]
                temp.extraEbsVolumeSize = row[15]
                machines.append(temp)

	cur.close()
        return machines

def getEucaInstance(instanceId):
        instances = [instanceId]
        res = ec2.get_all_instances(instance_ids=instances)
        if(len(res) > 0):
                resInstances = res[0].instances
                if(len(resInstances) > 0):
                        i = resInstances[0]
                        return i
                else:
                        return None
        else:
                return None

def getInstanceKey(instanceId):
	cur = con.cursor()
	sql = "select key_id, instance_id, secret_key, key_fingerprint, key_name from key_tbl where instance_id = "+str(instanceId)
	cur.execute(sql)
	rows = cur.fetchall()
	if(len(rows) < 1):
		cur.close()
		return None
	row = rows[0]
	temp = Key()
	temp.id = row[0]
	temp.instanceId = row[1]
	temp.secretKey = row[2]
	temp.keyFingerprint = row[3]
	temp.name = row[4]

	cur.close()
	return temp

def checkLoadBalancer(machine):
        if(machine.type == 'loadbalancer'):
                instance = getEucaInstance(machine.instanceId)
                if(instance != None):
                        if(instance.state == 'running'):
                                return "OK"
                        else:
                                return "LB instance found ("+machine.instanceId+"), but it's not running. State: " + instance.state
                else:
                        return "LB instance not found from cloud (machineId: "+str(machine.id)+", instanceId: "+machine.instanceId+")"
        else:
                return "Not a loadbalancer machine"

def runInstance(keyName, imageId, securityGroup, instanceType):
	reservation = ec2.run_instances(imageId, key_name=keyName, instance_type=instanceType, security_groups=[securityGroup])
	instances = reservation.instances
	if(len(instances) < 1):
		return None
	instance = instances[0]
	return instance

def authorizeIP(groupName, protocol, fromPort, toPort, cidr):
	#retVal = ec2.authorize_security_group(group_name=groupName, ip_protocol=protocol, from_port=fromPort, to_port=toPort, cidr_ip=cidr)
	retVal = ec2.authorize_security_group_deprecated(group_name=groupName, ip_protocol=protocol, from_port=fromPort, to_port=toPort, cidr_ip=cidr)
	return retVal

def getMachineType(size):
	if(size == 0):
		return "m1.small"
	if(size == 1):
		return "c1.medium"
	if(size == 2):
		return "m1.large"
	if(size == 3):
		return "m1.xlarge"
	if(size == 4):
		return "c1.xlarge"

def createVolume(size):
	volumeId = ec2.create_volume(size=size, zone=eucaZone, snapshot=None, volume_type=None, iops=None)
	return volumeId


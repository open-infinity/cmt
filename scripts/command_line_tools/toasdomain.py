class Instance:
        id = 0
	userId = 0
        name = ""
	cloudType = 0
	cloudZone = ""
	organizationId = 0
	instanceStatus = 0
	instanceActive = 0

class Key:
	id = 0
	instanceId = 0
	secretKey = ""
	keyFingerprint = ""
	name = ""

class Cluster:
        id = 0
        name = ""
        machines = 0
	lbName = ""
	lbDns = ""
	instanceId = 0
        type = 0
	pub = 0
	live = 0
	lbInstanceId = ""
	groupId = ""
	groupName = ""
	multicastAddress = ""
	machineType = 0
	ebsImageUsed = 0
	ebsVolumeUsed = 0


class Machine:
        id = 0
        instanceId = ""
	name = ""
	dnsName = ""
	active = 0
	username = ""
	running = 0
        state = ""
	clusterId = 0
	privateDnsName = ""
        type = ""
	configured = 0
	cloudType = 0
	extraEbsVolumeId = ""
	extraEbsVolumeDevice = ""
	extraEbsVolumeSize = 0

class AuthorizedIP:
	id = 0
	instanceId = 0
	clusterId = 0
	cidrIP = ""
	protocol = ""
	securityGroupName = ""
	fromPort = 0
	toPort = 0

class ClusterCheck:
	cluster = None
	lbStatus = 0
	realMachineCount = 0
	errorMessages = []
	clusterStatus = 0

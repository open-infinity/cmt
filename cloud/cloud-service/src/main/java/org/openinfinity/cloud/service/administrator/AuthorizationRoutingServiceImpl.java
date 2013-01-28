package org.openinfinity.cloud.service.administrator;

import static org.openinfinity.cloud.service.administrator.ClusterService.CLUSTER_STATUS_PRIVATE;
import static org.openinfinity.cloud.service.administrator.ClusterService.CLUSTER_STATUS_PUBLIC;
import static org.openinfinity.cloud.service.administrator.ClusterService.CLUSTER_STATUS_UNPUBLISHED;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.openinfinity.cloud.domain.AuthorizationRoute;
import org.openinfinity.cloud.domain.Cluster;
import org.openinfinity.cloud.domain.ElasticIP;
import org.openinfinity.cloud.domain.repository.administrator.AuthorizedRoutingRepository;
import org.openinfinity.core.annotation.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service interface implementation for building security groups between machines.
 * 
 * @author Ossi Hämäläinen
 * @author Ilkka Leinonen
 * @author Juha-Matti Sironen
 * @author Vedran Bartonicek 
 * @version 1.0.0 Initial version
 * @since 1.0.0
 */
@Service("authorizationRoutingService")
public class AuthorizationRoutingServiceImpl implements AuthorizationRoutingService {

	@Autowired
	private AuthorizedRoutingRepository authorizedRoutingRepository;
	
	@Log
	public Collection<AuthorizationRoute> getIPs(int clusterId) {
		return Collections.unmodifiableCollection(authorizedRoutingRepository.getIPs(clusterId));
	}

	@Log
	public Collection<AuthorizationRoute> getInstanceIPs(int instanceId) {
		return Collections.unmodifiableCollection(authorizedRoutingRepository.getInstanceIPs(instanceId));
	}

	@Log
	public void addIP(AuthorizationRoute ip) {
		authorizedRoutingRepository.addIP(ip);
	}

	@Log
	public void deleteIP(AuthorizationRoute ip) {
		authorizedRoutingRepository.deleteIP(ip);
	}
	
	@Log
	public void deleteUserAuthorizedIP(int ipId) {
		authorizedRoutingRepository.deleteUserAuthorizedIP(ipId);
	}
	
	@Log
	public int addUserAuthorizedIP(AuthorizationRoute ip){
		return authorizedRoutingRepository.addUserAuthorizedIP(ip);
	}
	
	@Log
	public void deleteInstanceIPs(int instanceId) {
		authorizedRoutingRepository.deleteInstanceIPs(instanceId);
	}
	
	@Log
	public void updateAuthorizedIPs(Cluster cluster, int newPubStatus, EC2Wrapper ec2, String securityGroupOwner) {
		if (cluster.getPublished() == CLUSTER_STATUS_UNPUBLISHED) {
			if (newPubStatus == CLUSTER_STATUS_PUBLIC) {
				AuthorizationRoute ip = new AuthorizationRoute();
				ip.setCidrIp("0.0.0.0/0");
				ip.setClusterId(cluster.getId());
				ip.setInstanceId(cluster.getInstanceId());
				ip.setFromPort(80);
				ip.setToPort(80);
				ip.setProtocol("tcp");
				ip.setSecurityGroupName(cluster.getSecurityGroupName());
				ec2.authorizeIPs(ip.getSecurityGroupName(), ip.getCidrIp(), ip.getFromPort(), ip.getToPort(), ip.getProtocol());
			} else if(newPubStatus == CLUSTER_STATUS_PRIVATE) {
			/*	Collection<AuthorizationRoute> ipList = authorizedRoutingRepository.getInstanceIPs(cluster.getInstanceId());
				Iterator<AuthorizationRoute> i = ipList.iterator();
				while(i.hasNext()) {
					AuthorizationRoute ip = i.next();
					ec2.authorizeIPs(cluster.getSecurityGroupName(), ip.getCidrIp(), ip.getFromPort(), ip.getToPort(), ip.getProtocol());
				} */
				Collection<String> groupList = authorizedRoutingRepository.getAllSecurityGroupsInInstance(cluster.getInstanceId());
				Iterator<String> i = groupList.iterator();
				while(i.hasNext()) {
					String group = i.next();
					if(!group.equals(cluster.getSecurityGroupName())) {
						ec2.authorizeGroup(cluster.getSecurityGroupName(), group, securityGroupOwner, 0, 65535, "tcp");
					}
				}
			}
		} else if(cluster.getPublished() == CLUSTER_STATUS_PRIVATE) {
			if(newPubStatus == CLUSTER_STATUS_PUBLIC) {
			/*	List<AuthorizedIP> ipList = cloudAdminDao.getInstanceIPs(cluster.getInstanceId());
				Iterator<AuthorizedIP> i = ipList.iterator();
				while(i.hasNext()) {
					AuthorizedIP ip = i.next();
					ec2.revokeIPs(cluster.getSecurityGroupName(), ip.getCidrIp(), ip.getFromPort(), ip.getToPort(), ip.getProtocol());
				} */
				ec2.authorizeIPs(cluster.getSecurityGroupName(), "0.0.0.0/0", 80, 80, "tcp");
			} 
		} else if(cluster.getPublished() == CLUSTER_STATUS_PUBLIC) {
			if(newPubStatus == CLUSTER_STATUS_PRIVATE) {
				ec2.revokeIPs(cluster.getSecurityGroupName(), "0.0.0.0/0", 80, 80, "tcp");
			/*	Collection<AuthorizationRoute> ipList = authorizedRoutingRepository.getInstanceIPs(cluster.getInstanceId());
				Iterator<AuthorizationRoute> i = ipList.iterator();
				while(i.hasNext()) {
					AuthorizationRoute ip = i.next();
					ec2.authorizeIPs(cluster.getSecurityGroupName(), ip.getCidrIp(), ip.getFromPort(), ip.getToPort(), ip.getProtocol());
				} */
				Collection<String> groupList = authorizedRoutingRepository.getAllSecurityGroupsInInstance(cluster.getInstanceId());
				Iterator<String> i = groupList.iterator();
				while(i.hasNext()) {
					String group = i.next();
					if(!group.equals(cluster.getSecurityGroupName())) {
						ec2.authorizeGroup(cluster.getSecurityGroupName(), group, securityGroupOwner, 0, 65535, "tcp");
					}
				}
			}
		}
		
	}
	
	@Log
	public void deleteClusterIPs(int clusterId) {
		authorizedRoutingRepository.deleteClusterIPs(clusterId);
	}

	@Log
	public Collection<AuthorizationRoute> getUserAuthorizedIPsForCluster(int clusterId) {
		return authorizedRoutingRepository.getUserAuthorizedIPsForCluster(clusterId);
	}
	
	@Log
	public void deleteAllUserAuthorizedIPsFromCluster(int clusterId) {
		authorizedRoutingRepository.deleteAllUserAuthorizedIPsFromCluster(clusterId);
	}

	@Log
	public Collection<ElasticIP> getElasticIPList() {
		return authorizedRoutingRepository.getElasticIPs();
	}

	@Log
	public ElasticIP getElasticIP(int ipId) {
		return authorizedRoutingRepository.getElasticIP(ipId);
	}

	@Log
	public void updateElasticIP(ElasticIP ip) {
		authorizedRoutingRepository.updateElasticIP(ip);
	}

	@Log
	public ElasticIP getClustersElasticIP(int clusterId) {
		return authorizedRoutingRepository.getClustersElasticIP(clusterId);
	}

	@Log
	public void freeElasticIP(ElasticIP ip) {
		ip.setClusterId(0);
		ip.setInstanceId(0);
		ip.setMachineId(0);
		ip.setInUse(0);
		ip.setOrganizationId(0);
		ip.setUserId(0);
		authorizedRoutingRepository.updateElasticIP(ip);
	}

	@Override
	public List<String> getAllSecurityGroupsInInstance(int instanceId) {
		return authorizedRoutingRepository.getAllSecurityGroupsInInstance(instanceId);
	}

}

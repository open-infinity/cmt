package org.openinfinity.cloud.service.administrator;

import java.util.Collection;
import java.util.List;

import org.openinfinity.cloud.domain.AuthorizationRoute;
import org.openinfinity.cloud.domain.Cluster;
import org.openinfinity.cloud.domain.ElasticIP;

/**
 * Service interface for building security groups between machines.
 * 
 * @author Ilkka Leinonen
 * @author Ossi Hämäläinen
 * @author Juha-Matti Sironen
 * @author Vedran Bartonicek 
 * @version 1.0.0 Initial version
 */
public interface AuthorizationRoutingService {
	Collection<AuthorizationRoute> getIPs(int clusterId);
	
	Collection<AuthorizationRoute> getInstanceIPs(int instanceId);

	Collection<AuthorizationRoute> getUserAuthorizedIPsForCluster(int clusterId);
	
	void addIP(AuthorizationRoute ip);
	
	void deleteIP(AuthorizationRoute ip);
	
	void deleteUserAuthorizedIP(int ipId);
	
	int addUserAuthorizedIP(AuthorizationRoute ip);
	
	void deleteAllUserAuthorizedIPsFromCluster(int clusterId);
	
	void deleteInstanceIPs(int instanceId);
	
	void deleteClusterIPs(int clusterId);
	
	Collection<ElasticIP> getElasticIPList();
	
	ElasticIP getElasticIP(int ipId);
	
	void updateElasticIP(ElasticIP ip);

	ElasticIP getClustersElasticIP(int clusterId);
	
	void freeElasticIP(ElasticIP ip);
	
	void updateAuthorizedIPs(Cluster cluster, int newPubStatus, EC2Wrapper ec2, String securityGroupOwner);	
	public List<String> getAllSecurityGroupsInInstance(int instanceId);
	
}
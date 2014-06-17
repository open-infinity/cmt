/*
 * Copyright (c) 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openinfinity.cloud.domain.repository.administrator;

import java.util.Collection;
import java.util.List;

import org.openinfinity.cloud.domain.AuthorizationRoute;
import org.openinfinity.cloud.domain.ElasticIP;

/**
 * AuthorizedRouting repoasitory interface
 * 
 * @author Ossi Hämäläinen
 * @author Ilkka Leinonen
 * @author Juha-Matti Sironen
 * @version 1.0.0 Initial version
 * @since 1.0.0
 */

public interface AuthorizedRoutingRepository {

	void addIP(AuthorizationRoute ip);
	
	void deleteInstanceIPs(int instanceId);
	
	Collection<AuthorizationRoute> getInstanceIPs(int instanceId);
	
	Collection<AuthorizationRoute> getIPs(int clusterId);
	
	void deleteIP(AuthorizationRoute ip);
	
	void deleteClusterIPs(int clusterId);
	
	int addUserAuthorizedIP(AuthorizationRoute ip);
	
	void deleteUserAuthorizedIP(int id);
	
	Collection<AuthorizationRoute> getUserAuthorizedIPsForCluster(int clusterId);
		
	void deleteAllUserAuthorizedIPsFromCluster(int clusterId);
	
	void addElasticIP(ElasticIP ip);
	
	List<ElasticIP> getElasticIPs();
	
	void updateElasticIP(ElasticIP ip);
	
	ElasticIP getElasticIP(int ipId);
	
	ElasticIP getClustersElasticIP(int clusterId);
	
	List<String> getAllSecurityGroupsInInstance(int instanceId);
	
}
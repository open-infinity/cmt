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

package org.openinfinity.cloud.service.administrator;

import java.util.Collection;

import org.openinfinity.cloud.domain.Cluster;
import org.openinfinity.cloud.domain.Machine;

/**
 * Cluster service interface for building clusters inside a cloud environment.
 * 
 * @author Ilkka Leinonen
 * @author Ossi Hämäläinen
 * @author Juha-Matti Sironen
 * @version 1.0.0 Initial version
 * @since 1.0.0
 */
public interface ClusterService {
	
	static final int CLUSTER_TYPE_PORTAL = 0;
	
	static final int CLUSTER_TYPE_MULE_MQ = 1;
	
	static final int CLUSTER_TYPE_PENTAHO = 2;
	
	static final int CLUSTER_TYPE_BIGDATA = 3;
	
	static final int CLUSTER_TYPE_DATABASE = 4;
	
	static final int CLUSTER_TYPE_BAS = 5;
	
	static final int CLUSTER_TYPE_NOSQL = 6;
	
	static final int CLUSTER_TYPE_IDENTITY_GATEWAY = 7;
	
	static final int CLUSTER_TYPE_EE = 8;
	
	static final int CLUSTER_TYPE_ECM = 9;
	
	static final int CLUSTER_STATUS_PUBLIC = 1;
	
	static final int CLUSTER_STATUS_PRIVATE = 2;
	
	static final int CLUSTER_STATUS_UNPUBLISHED = 3;
	

	static final String[] CLUSTER_TYPE_NAME = {"Portal Platform", "Service Platform", "Analytics Platform",
	   										   "Big Data Repository", "Database Platform", "BAS Platform",
											   "NoSQL Platform", "Identity Gateway Platform", "EE Platform", "Enterprise Content Management Platform"};
											   
	static final String[] SERVICE_NAME = {"portal_platform", "service_platform", "analytics_platform",
										  "bigdata_platform", "database_platform", "bas_platform", 
										  "nosql_platform", "ig_platform", "ee_platform", "ecm_platform"};
	
	static final String[] CLUSTER_PUBLISH_STATUS_NAME = {"", "Private", "Unpublished"};
	
	static final String[] CLUSTER_TYPE_MACHINE_NAME = {"portal", "service", "analytics", "bigdata", "db", "bas", "nosql", "ig", "ee", "ecm"};
	
	Collection<Cluster> getClusters();
	
	Cluster getCluster(int id);
	
	void addCluster(Cluster cluster);

	Collection<Cluster> getClusters(int instanceId);
	
	void updatePublished(int id, int published);
	
	void updateCluster(Cluster cluster);
	
	void deleteCluster(Cluster cluster);
	
	Machine getClustersLoadBalancer(int clusterId);

	Cluster getClusterByLoadBalancerId(int id);
	
	Cluster getClusterByClusterId(int id);
	
}
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
package org.openinfinity.cloud.service.deployer;

import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import org.openinfinity.cloud.domain.Cluster;
import org.openinfinity.cloud.domain.Deployment;
import org.openinfinity.cloud.domain.DeploymentStatus;
import org.openinfinity.cloud.domain.Instance;
import org.openinfinity.cloud.domain.repository.administrator.ClusterRepository;
import org.openinfinity.cloud.domain.repository.administrator.InstanceRepository;
import org.openinfinity.cloud.domain.repository.deployer.BucketRepository;
import org.openinfinity.cloud.domain.repository.deployer.DeploymentRepository;
import org.openinfinity.core.annotation.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Deployer interface implementation for deploying new applications inside a cloud environment.
 * 
 * @author Ilkka Leinonen
 * @author Tommi Siitonen
 * @version 1.0.0
 * @since 1.0.0
 */
@Service("deployerService")
public class DeployerServiceImpl implements DeployerService {

	@Autowired
	@Qualifier("jetS3Repository")
	private BucketRepository bucketRepository;

	@Autowired
	private DeploymentRepository deploymentRepository;
	
	@Autowired
	private InstanceRepository instanceRepository;
	
	@Autowired
	private ClusterRepository clusterRepository;

	@Log
	public Deployment deploy(Deployment deployment) {
		// For supporting rollback bucket objects need to be named to be uniquely and old versions stored in db with NOT_DEPLOYED state
		
		// we need new deployment id for location? location need to be stored also
		
		deployment.setLocation("not_in_walrus"); 
		//  if previous DEPLOYED deployment (target and name) exists it will be updated to UNDEPLOYED
		// this will update deploymenstatuses and actually remove old application from target cluster machines
		deploymentRepository.storeAndUpdate(deployment);
		String location = bucketRepository.createBucket(deployment.getInputStream(), ""+deployment.getClusterId(), deployment.getName()+"-"+deployment.getId(), new HashMap<String, String>());
		deployment.setLocation(location);
		deployment.setState(DEPLOYMENT_STATE_DEPLOYED);
		deploymentRepository.updateLocationAndState(deployment);
		return deployment;
				
		//String location = bucketRepository.createBucket(deployment.getInputStream(), ""+deployment.getClusterId(), deployment.getName(), new HashMap<String, String>());
		//deployment.setLocation(location);
		//deploymentRepository.store(deployment);
		//return deployment;
	} 
	
	@Log
	public Deployment redeploy(Deployment deployment) {
		// For supporting rollback bucket objects need to be named to be uniquely and old versions stored in db with NOT_DEPLOYED state

		// update current deployed deployment to undeploy and redeployed to deploy
		// TODO: verify deploymentstatuses handling in reader
		
		//  if previous DEPLOYED deployment (target and name) exists it will be updated to UNDEPLOY
		deploymentRepository.updateExistingDeployedDeploymentState(deployment, DEPLOYMENT_STATE_UNDEPLOY);
		// we could update state to UNDEPLOYED and deploymentStatuses to undeployed as well since current
		// deployment will be replaced
		
		deployment.setState(DEPLOYMENT_STATE_DEPLOYED);
		deploymentRepository.updateLocationAndState(deployment);
		return deployment;
				
		//String location = bucketRepository.createBucket(deployment.getInputStream(), ""+deployment.getClusterId(), deployment.getName(), new HashMap<String, String>());
		//deployment.setLocation(location);
		//deploymentRepository.store(deployment);
		//return deployment;
	}	
	
	

	@Log
	public void deleteObject(Deployment deployment) {
		bucketRepository.deleteObject(""+deployment.getClusterId(), deployment.getName());
		// no mid-state used. if DeploymentStatuses need to be set to DELETED use mid state and implement batch processing
		// deployment.setState(DeployerService.DEPLOYMENT_STATE_DELETED);
		// deploymentRepository.store(deployment);
		deploymentRepository.updateDeploymentStateById(deployment.getId(), DeployerService.DEPLOYMENT_STATE_DELETED);
	}
	
	
	@Log
	public Deployment loadDeploymentById(int deploymentId) {
		return deploymentRepository.loadById(deploymentId);
	}
		
	
	@Log
	public Collection<Deployment> loadDeployments() {
		return deploymentRepository.loadAll();
	}
	
	@Log
	public Collection<Deployment> loadDeploymentsForOrganization(long organizationId) {
		return deploymentRepository.loadAllForOrganization(organizationId);
	}
	
	@Log
	public Collection<Deployment> loadDeploymentsForClusterWithName(Deployment deployment) {
		return deploymentRepository.loadByOrgInstClusName(deployment.getOrganizationId(), deployment.getInstanceId(), deployment.getClusterId(), deployment.getName());
	}
	
	
	@Log
	public Collection<Deployment> loadDeployments(int page, int rows){
		return deploymentRepository.loadDeployments(page, rows);
	}
	
	@Log
	public Collection<Instance> loadInstances(long organizationId) {
		Collection<Instance> instances = instanceRepository.getOrganizationInstances(organizationId);
		return Collections.unmodifiableCollection(instances);
	}

	@Log
	public Collection<Cluster> loadClusters(int instanceId) {
		Collection<Cluster> clusters = clusterRepository.getClusters(instanceId);
		return Collections.unmodifiableCollection(clusters);
	}
	
	@Log
	public void updateDeploymentState(Deployment deployment) {
		deploymentRepository.updateDeploymentStateById(deployment.getId(), deployment.getState());
	}
	
	@Log
	public void updateDeployment(Deployment deployment) {
		// store should both handle add and update ?
		//deploymentRepository.store(deployment);
	}
	
	@Log
	public void updateDeploymentStatusStatesFromToByDeploymentId(DeploymentStatus.DeploymentState from, DeploymentStatus.DeploymentState to, int deploymentId) {		
		deploymentRepository.updateDeploymentStatusStatesFromToByDeploymentId(from.getValue(), to.getValue(), deploymentId);
	}
		
	
	@Log
	public void rollback(Deployment deployment) {
		// TODO Auto-generated method stub	
		// fetch deployment with id in object name and update deployment statuses. also deploy need changes
	}

	@Log
	@Override
	public InputStream load(String bucketName, String key) {
		return bucketRepository.load(bucketName, key);
	}

	@Override
	public void storeDeploymentStatus(DeploymentStatus deploymentStatus) {
		deploymentRepository.storeDeploymentStatus(deploymentStatus);
	}

	@Override
	public Collection<DeploymentStatus> loadDeploymentStatusesForCluster(int clusterId) {
		return deploymentRepository.loadDeploymentStatusesByClusterId(clusterId);
	}
	
	@Override
	public Collection<DeploymentStatus> loadDeploymentStatusesForDeployment(int deploymentId) {
		return deploymentRepository.loadDeploymentStatusesByDeploymentId(deploymentId);
	}	
}
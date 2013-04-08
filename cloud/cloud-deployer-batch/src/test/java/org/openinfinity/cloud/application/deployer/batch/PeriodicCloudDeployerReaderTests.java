/*
 * Copyright (c) 2011-2013 the original author or authors.
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
package org.openinfinity.cloud.application.deployer.batch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.openinfinity.cloud.domain.Deployment;
import org.openinfinity.cloud.domain.DeploymentStatus;
import org.openinfinity.cloud.domain.DeploymentStatus.DeploymentState;
import org.openinfinity.cloud.domain.Instance;
import org.openinfinity.cloud.service.administrator.InstanceService;
import org.openinfinity.cloud.service.deployer.DeployerService;

/**
 * Unit test for <code>PeriodicCloudDeployerReader</code> class.
 * 
 * @author Ilkka Leinonen
 * @version 1.0.0
 * @since 1.2.0
 */
public class PeriodicCloudDeployerReaderTests {
	
	private static final int DEPLOYMENT_ID = 1000;

	private static final int CLUSTER_ID = 100;

	private static final int INSTANCE_ID = 1234;

	private static final Long ORGANIZATION_ID = new Long(12345);

	PeriodicCloudDeployerReader periodicCloudDeployerReader;

	DeploymentStatus expectedDeploymentStatus1;
	DeploymentStatus expectedDeploymentStatus2;
	DeploymentStatus expectedDeploymentStatus3; 
	DeploymentStatus allreadyDeployedStatus;
	DeploymentStatus allreadyDeployedStatusButOld;
	
	@Before
	public void setUp() {
		Collection<DeploymentStatus> deploymentStatuses = new ArrayList<DeploymentStatus>();
		Collection<Deployment> deployments = new ArrayList<Deployment>();
		
		Deployment deployment = new Deployment();
		deployment.setId(DEPLOYMENT_ID);
		deployment.setClusterId(CLUSTER_ID);
		deployment.setOrganizationId(ORGANIZATION_ID);
		populateDeploymentStatuses(deployment, deploymentStatuses);
		deployments.add(deployment);
		DateTime deploymentTimestamp = new DateTime(1980, 4, 3, 12, 0, 0, 0);
		Timestamp timestamp = new Timestamp(deploymentTimestamp.toDate().getTime());
		deployment.setDeploymentTimestamp(timestamp);
		
		Collection<Instance> instances = new ArrayList<Instance>();
		Instance instance = new Instance();
		instance.setInstanceId(INSTANCE_ID);
		instance.setOrganizationid(ORGANIZATION_ID);
		instances.add(instance);
		
		DeployerService deployerService = mock(DeployerService.class);
		InstanceService instanceService = mock(InstanceService.class);
		
		when(deployerService.loadDeploymentStatusesForCluster(new Long(1))).thenReturn(deploymentStatuses);
		when(instanceService.getAllActiveInstances()).thenReturn(instances);
		when(deployerService.loadDeploymentsForOrganization(ORGANIZATION_ID)).thenReturn(deployments);
		when(deployerService.loadDeploymentStatusesForCluster(CLUSTER_ID)).thenReturn(deploymentStatuses);
		
		periodicCloudDeployerReader = new PeriodicCloudDeployerReader();
		periodicCloudDeployerReader.deployerService = deployerService;
		periodicCloudDeployerReader.instanceService = instanceService;		
	}

	private void populateDeploymentStatuses(Deployment deployment, Collection<DeploymentStatus> deploymentStatuses) {
		DateTime createTime = new DateTime(1979, 4, 3, 12, 0, 0, 0);
		DateTime newerDeploymentPackageOnMachine = new DateTime(1981, 4, 3, 12, 0, 0, 0);
		expectedDeploymentStatus1 = createDeploymentStatus(deployment, DeploymentState.NOT_DEPLOYED, 1, createTime.toDate());
		expectedDeploymentStatus2 = createDeploymentStatus(deployment, DeploymentState.NOT_DEPLOYED, 2, createTime.toDate());
		expectedDeploymentStatus3 = createDeploymentStatus(deployment, DeploymentState.NOT_DEPLOYED, 3, createTime.toDate());
		allreadyDeployedStatus = createDeploymentStatus(deployment, DeploymentState.DEPLOYED, 4, createTime.toDate());
		allreadyDeployedStatusButOld = createDeploymentStatus(deployment, DeploymentState.DEPLOYED, 5, newerDeploymentPackageOnMachine.toDate());
		deploymentStatuses.add(expectedDeploymentStatus1);
		deploymentStatuses.add(expectedDeploymentStatus2);
		deploymentStatuses.add(expectedDeploymentStatus3);
		deploymentStatuses.add(allreadyDeployedStatus);
		deploymentStatuses.add(allreadyDeployedStatusButOld);
	}

	private DeploymentStatus createDeploymentStatus(Deployment deployment, DeploymentState deploymentState, int id, Date createTime) {
		DeploymentStatus deploymentStatus = new DeploymentStatus();
		deploymentStatus.setDeployment(deployment);
		deploymentStatus.setDeploymentState(deploymentState);
		deploymentStatus.setId(id);
		deploymentStatus.setMachineId(id);
		deploymentStatus.setTimestamp(createTime);
		return deploymentStatus;
	}
	
	@Test
	public void givenKnownUniqueInstanceAndKnownDeploymentWhenProcessingKnownDeploymentStatusesThenReaderMustProvideAllExpectedResultsWithNotDeploymentState() {
		try {
			DeploymentStatus actualDeploymentStatus1 = periodicCloudDeployerReader.read();
			DeploymentStatus actualDeploymentStatus2 = periodicCloudDeployerReader.read();
			DeploymentStatus actualDeploymentStatus3 = periodicCloudDeployerReader.read();
			DeploymentStatus actualDeploymentStatus4 = periodicCloudDeployerReader.read();
			DeploymentStatus expectedEmpty = periodicCloudDeployerReader.read();
			assertNotNull(actualDeploymentStatus1);
			assertEquals(expectedDeploymentStatus1.getId(), actualDeploymentStatus1.getId());
			assertNotNull(actualDeploymentStatus2);
			assertEquals(expectedDeploymentStatus2.getId(), actualDeploymentStatus2.getId());
			assertNotNull(actualDeploymentStatus3);
			assertEquals(expectedDeploymentStatus3.getId(), actualDeploymentStatus3.getId());
			assertNotNull(actualDeploymentStatus4);
			assertEquals(actualDeploymentStatus4.getId(), allreadyDeployedStatus.getId());
			assertNull(expectedEmpty);
		} catch (Throwable throwable) {
			throwable.printStackTrace();
			fail(throwable.toString());
		}		
		
	}
	
}
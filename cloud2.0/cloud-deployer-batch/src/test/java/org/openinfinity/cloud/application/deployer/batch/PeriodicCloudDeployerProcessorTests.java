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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.openinfinity.cloud.domain.Deployment;
import org.openinfinity.cloud.domain.DeploymentStatus;
import org.openinfinity.cloud.domain.DeploymentStatus.DeploymentState;
import org.openinfinity.cloud.service.deployer.DeployerService;

/**
 * Unit test for <code>PeriodicCloudDeployerReader</code> class.
 * 
 * @author Ilkka Leinonen
 * @version 1.0.0
 * @since 1.2.0
 */
public class PeriodicCloudDeployerProcessorTests {
	
	private static final int DEPLOYMENT_ID = 1000;

	private static final int CLUSTER_ID = 100;

	private static final Long ORGANIZATION_ID = new Long(12345);
	
	private static final String EXPECTED_KEY = "testKey";
	private static final String EXPECTED_DEPLOYMENT_NAME = "Open Infinity Deployment";
	private static final String EXPECTED_BUCKET_NAME = "testName";
	
	Collection<DeploymentStatus> deploymentStatuses;

	PeriodicCloudDeployerProcessor periodicCloudDeployerProcessor;

	Deployment deployment;
	
	DeploymentStatus expectedDeploymentStatus1;
	DeploymentStatus expectedDeploymentStatus2;
	DeploymentStatus expectedDeploymentStatus3; 
	DeploymentStatus allreadyDeployedStatus;
	DeploymentStatus allreadyDeployedStatusButOld;
	
	@Before
	public void setUp() throws IOException {
	    deploymentStatuses = new ArrayList<DeploymentStatus>();
		
	    deployment = new Deployment();
		deployment.setId(DEPLOYMENT_ID);
		deployment.setClusterId(CLUSTER_ID);
		deployment.setOrganizationId(ORGANIZATION_ID);
		deployment.setName(EXPECTED_DEPLOYMENT_NAME);
		populateDeploymentStatuses(deployment, deploymentStatuses);
		
		DeployerService deployerService = mock(DeployerService.class);

		//InputStream inputStream = mock(InputStream.class);
		//when(inputStream.read()).thenReturn(-1);
		//InputStream inputStream = IOUtils.toInputStream("Open Infinity multi channel stream.");
		
	    ByteArrayInputStream bais = new ByteArrayInputStream("a=b+c".getBytes());
	  // FIXME: Yhdistetäänkö inputstreamia ja deploymenttia?
		
		when(deployerService.load(EXPECTED_BUCKET_NAME, EXPECTED_KEY)).thenReturn(bais);
		
		
		
		periodicCloudDeployerProcessor = new PeriodicCloudDeployerProcessor();
		periodicCloudDeployerProcessor.deployerService = deployerService;
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
			for (DeploymentStatus deploymentStatus : deploymentStatuses) {
				DeploymentStatus actual = periodicCloudDeployerProcessor.process(deploymentStatus);
				assertNotNull(actual);
				assertNotNull(actual.getDeployment().getName());
				assertNotNull(actual.getDeployment().getOrganizationId());
				//assertNotNull(actual.getDeployment().getInputStream());
			}
		} catch (Throwable throwable) {
			throwable.printStackTrace();
			fail(throwable.toString());
		}				
	}
	
}
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

package org.openinfinity.cloud.autoscaler.test.periodicscaler; 

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openinfinity.cloud.autoscaler.periodicscaler.PeriodicScalerItemReader;
import org.openinfinity.cloud.domain.Cluster;
import org.openinfinity.cloud.domain.Machine;
import org.openinfinity.cloud.service.administrator.ClusterService;
import org.openinfinity.cloud.service.administrator.MachineService;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Batch reader unit tests.
 * 
 * @author Ilkka Leinonen
 * @version 1.0.0
 * @since 1.0.0
 */
//@ContextConfiguration(locations={"/META-INF/spring/periodic-scaler-context.xml"})
//@RunWith(SpringJUnit4ClassRunner.class)
public class CapacityUsageItemReaderTests {

/*	private CapacityUsageItemReader reader;
	private ClusterService clusterService;
	private MachineService machineService;
	
	@Before
	public void setUp() {
		 reader = new CapacityUsageItemReader();
		 clusterService = mock(ClusterService.class);
		 machineService = mock(MachineService.class);
		 //reader.machineService = machineService;
		 //reader.clusterService = clusterService;
		 mockMachineService();
		 mockClusterService();
	}

	private void mockMachineService() {
		Machine machine = new Machine();
		machine.setId(123);
		machine.setType("basplatform");
		when(machineService.getMachine(123)).thenReturn(machine);
	}
	
	private void mockClusterService() {
		Collection<Cluster> clusters = new ArrayList<Cluster>();
		Cluster cluster = new Cluster();
		cluster.setId(123);
		cluster.setInstanceId(123);
		when(clusterService.getClusters()).thenReturn(clusters);
	}
	
	@Test
	public void testReadOnce() throws Exception {
		Machine machine = reader.read();
		assertEquals(null, machine);
	}
*/
}

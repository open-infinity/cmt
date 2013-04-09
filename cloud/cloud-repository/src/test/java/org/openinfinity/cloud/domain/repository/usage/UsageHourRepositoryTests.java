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
package org.openinfinity.cloud.domain.repository.usage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;

import java.util.Collection;

import javax.sql.DataSource;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openinfinity.cloud.domain.UsageHour;
import org.openinfinity.cloud.domain.UsageHour.VirtualMachineState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Integration test for Usage hour repository interface.
 * 
 * @author Ilkka Leinonen
 * @version 1.0.0
 * @since 1.2.0
 */
@ContextConfiguration(locations={"classpath*:META-INF/spring/repository-context.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class UsageHourRepositoryTests {

	@Autowired
	@Qualifier("cloudDataSource")
	DataSource ds;
	
	@Autowired
	UsageHourRepository usageHourRepository;

	@Test
	public void givenKnownUsageHourOfVirtualMachineWhenModifyingStateOfVirtualMachineThenModificationMustBeAccessibleFromRepositoryInterface() {
		long organizationId = System.currentTimeMillis();
		UsageHour expected = new UsageHour();
		expected.setClusterId(System.currentTimeMillis());
		expected.setMachineId("MACHINE_ID_12345");
		expected.setOrganizationId(organizationId);
		expected.setPlatformId("1");
		expected.setVirtualMachineState(VirtualMachineState.STARTED);
		usageHourRepository.store(expected);
		Collection<UsageHour> usageHours = usageHourRepository.loadUsageHoursByOrganizationId(organizationId);
		assertEquals(1, usageHours.size());
		UsageHour actual = null;
		for (UsageHour usage : usageHours) {
			actual = usage;
		}
		assertEquals(expected.getClusterId(), actual.getClusterId());
		assertEquals(expected.getMachineId(), actual.getMachineId());
		assertEquals(expected.getOrganizationId(), actual.getOrganizationId());
		assertEquals(expected.getPlatformId(), actual.getPlatformId());
		assertEquals(expected.getVirtualMachineState().getValue(), actual.getVirtualMachineState().getValue());
		assertNotNull(actual.getTimeStamp());
		assertNotSame(expected.getId(), actual.getId());	
	}
	
	@Test
	public void givenKnownOrganizationIdAndTimePeriodWhenQueringStateModificationOfVirtualMachinesThenModificationMustBeAccessibleFromRepositoryInterfaceDuringDefinedTimePeriod() {
		long organizationId = System.currentTimeMillis();
		UsageHour expected = new UsageHour();
		expected.setClusterId(System.currentTimeMillis());
		expected.setMachineId("MACHINE_ID_12345");
		expected.setOrganizationId(organizationId);
		expected.setPlatformId("1");
		expected.setVirtualMachineState(VirtualMachineState.STARTED);
		usageHourRepository.store(expected);
		DateTime startTime = new DateTime(1979, 4, 3, 12, 0, 0, 0);
		DateTime endTime = new DateTime(2079, 4, 3, 12, 0, 0, 0);
		Collection<UsageHour> usageHours = usageHourRepository.loadUsageHoursByOrganizationIdAndUsagePeriod(organizationId, startTime.toDate(), endTime.toDate());
		assertEquals(1, usageHours.size());
		UsageHour actual = null;
		for (UsageHour usage : usageHours) {
			actual = usage;
		}
		assertEquals(expected.getClusterId(), actual.getClusterId());
		assertEquals(expected.getMachineId(), actual.getMachineId());
		assertEquals(expected.getOrganizationId(), actual.getOrganizationId());
		assertEquals(expected.getPlatformId(), actual.getPlatformId());
		assertEquals(expected.getVirtualMachineState().getValue(), actual.getVirtualMachineState().getValue());
		assertNotNull(actual.getTimeStamp());
		assertNotSame(expected.getId(), actual.getId());	
	}

}
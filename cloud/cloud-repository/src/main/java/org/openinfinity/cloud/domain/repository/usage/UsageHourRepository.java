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

import java.util.Collection;
import java.util.Date;

import org.openinfinity.cloud.domain.UsageHour;

/**
 * Interface for reporting virtual machine usage.
 * 
 * @author Ilkka Leinonen
 * @version 1.0.0
 * @since 1.2.0
 */
public interface UsageHourRepository {
	
	/**
	 * Store information about the virtual machines state.
	 *  
	 * @param usageHours Represents the usage hour domain object.
	 */
	void store(UsageHour usageHours);
	
	/**
	 * Load usage hours of a specified virtual machine.
	 * 
	 * @param machineId Represents the unique virtual machine id.
	 * @return Collection of virtual machine's usage hours.
	 */
	UsageHour loadByMachineId(String machineId);
	
	/**
	 * Load usage hours for a specified organization.
	 * 
	 * @param organizationId Represents the unique organization id.
	 * @return Collection of virtual machine's usage hours.
	 */
	Collection<UsageHour> loadUsageHoursByOrganizationId(long organizationId);
	
	/**
	 * Load usage hours for a specified organization within given time period.
	 * 
	 * @param organizationId Represents the unique organization id.
	 * @param startTime Represents the specified start time of the period.
	 * @param endTime Represents the specified end time of the period.
	 * @return Collection of virtual machine's usage hours.
	 */
	Collection<UsageHour> loadUsageHoursByOrganizationIdAndUsagePeriod(long organizationId, Date startTime, Date endTime);

}

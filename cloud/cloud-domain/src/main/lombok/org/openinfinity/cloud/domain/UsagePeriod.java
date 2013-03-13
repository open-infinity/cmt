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
package org.openinfinity.cloud.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

import lombok.Data;

/**
 * Represent usage period of certain virtual machines and their state.
 * 
 * @author Ilkka Leinonen
 * @version 1.0.0
 * @since 1.2.0
 */
@Data
public class UsagePeriod {
	
	private long organizationId;
	
	private Date startTime;
	
	private Date endTime;
	
	private Collection<UsageHour> usageHours = Collections.checkedCollection(new ArrayList<UsageHour>(), UsageHour.class);
	
	public void addUsageHour(UsageHour usageHour) {
		usageHours.add(usageHour);
	}
	
	public Collection<UsageHour> loadUsageHours() {
		return Collections.unmodifiableCollection(usageHours);
	}
	
}

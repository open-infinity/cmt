/*
 * Copyright (c) 2013 the original author or authors.
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

import org.openinfinity.cloud.domain.AvailabilityZone;

import java.util.Collection;
import java.util.List;

/**
 * Availability zone service interface provides access to availability zones
 * 
 * @author Timo Tapanainen
 * @version 1.0.0 Initial version
 * @since 1.0.0
 */
public interface AvailabilityZoneService {
	
	Collection<AvailabilityZone> getAvailabilityZones(int cloudId, List<String> userOrgNames);

}

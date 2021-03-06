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

package org.openinfinity.cloud.service.scaling;

/**
 * Enumeration defining cluster's scaling status.
 * 
 * @author Ilkka Leinonen
 * @author Vedran Bartonicek
 * @version 1.0.0
 * @since 1.0.0
 */
public final class Enumerations{

    public enum ScalingStatus {
        SCALING_NOT_REQUIRED,
        SCALING_OUT_REQUIRED,
        SCALING_IN_REQUIRED,
        SCALING_ALREADY_ONGOING,
        ERROR_SCALING_RULE_LIMIT,
        ERROR_MACHINE_CONFIGURATION_FAILURE,
        ERROR_INVALID_RULE,
        ERROR_SCALING_JOB_ERROR,
        ERROR_INVALID_INTERNAL_STATE
	}
	
	public enum ScalingPolicy {
		MANUAL,
		AUTOMATIC
    }

}



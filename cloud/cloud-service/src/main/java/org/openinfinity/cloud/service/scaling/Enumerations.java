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
 * Enumaration defining the status of the cluster's balance.
 * 
 * @author Ilkka Leinonen
 * @version 1.0.0
 * @since 1.0.0
 */
public final class Enumerations{
	public enum ScalingState {
		SCALE_IN,
		SCALE_OUT,
		HARMONIZED,
		SYSTEM_DISASTER_PANIC,
		RULE_NOT_DEFINED,
		SCALING_ONGOING;
	}
	
	public enum ScalingPolicy {
		MANUAL,
		AUTOMATIC;
	}
}



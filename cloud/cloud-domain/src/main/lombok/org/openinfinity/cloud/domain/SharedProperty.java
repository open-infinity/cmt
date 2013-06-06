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

package org.openinfinity.cloud.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.NonNull;
import lombok.AllArgsConstructor;
import lombok.ToString;

/**
 * Represents a key-value pair, also known as a shared property, in cloud_properties_tbl table.
 * 
 * @author Timo Saarinen
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public final class SharedProperty {
	@NonNull 
	private String organizationId;
	
	private String instanceId;
	
	private String clusterId;
	
	private String key;
	
	private String value;
	
	public SharedProperty(String organizationId, String key, String value) {
		this.organizationId = organizationId;
		this.key = key;
		this.value = value;
	}

/* POIS VAAN ROHKEASTI	
	public boolean equals(Object o) {
		SharedProperty p = (SharedProperty) o;
		if (o != null) {
			return eq(organizationId, p.organizationId)
					&& eq(instanceId, p.instanceId)
					&& eq(clusterId, p.clusterId)
					&& eq(key, p.key)
					&& eq(value, p.value);
		}
		return false;
	}
	
	private static boolean eq(String a, String b) {
		if (a == null && b == null) {
			return true;
		} else if (a == null && b != null) {
			return false;
		} else if (a != null && b == null) {
			return false;
		} else {
			return a.equals(b);
		}
	}
*/	
}

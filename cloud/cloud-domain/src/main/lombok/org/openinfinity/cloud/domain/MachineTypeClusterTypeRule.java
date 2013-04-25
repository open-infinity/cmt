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

import java.io.Serializable;
import org.openinfinity.core.annotation.NotScript;
import lombok.Data;

/**
 * Machine type Cluster type rule specification; defines allowed machine types for each cluster type.
 *
 * @author Ari Simanainen
 * @version 1.0.0 Initial version
 * @since 1.0.0
 */

@Data
public class MachineTypeClusterTypeRule implements Serializable {
	
	@NotScript
	private int machineTypeId;
	@NotScript
	private int clusterTypeId;
	@NotScript
	private boolean allowed;
}

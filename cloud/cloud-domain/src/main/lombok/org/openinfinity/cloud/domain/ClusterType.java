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

package org.openinfinity.cloud.domain;

import java.io.Serializable;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.openinfinity.core.annotation.NotScript;

import lombok.Data;
import lombok.NonNull;


/**
 * The class is responsible for storing cluster type specific information within a cloud instance.
 *
 * @author Vedran Bartonicek
 * @version 1.0.0 Initial version
 * @since 1.0.0
 */

@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class ClusterType implements Serializable {
	@NonNull
	@NotScript
	private int id;
	@NonNull
	@NotScript
	private int configurationId;
	@NonNull
	@NotScript
	private String name;
	@NonNull
	@NotScript
	private String title;
	@NonNull
	@NotScript
	private int dependency;
	@NonNull
	@NotScript
	private boolean replicated;
	@NonNull
	@NotScript
	private int minMachines;
	@NonNull
	@NotScript
	private int maxMachines;
	@NonNull
	@NotScript
	private int minReplicationMachines;
	@NonNull
	@NotScript
	private int maxReplicationMachines;
}
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
package org.openinfinity.cloud.domain.configurationtemplate.entity;

import org.openinfinity.core.annotation.NotScript;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * @author Vedran Bartonicek
 * @version 1.3.0
 * @since 1.3.0
 */
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class ConfigurationElement {
    
    @NonNull
    @NotScript
    private int id;
    
    @NonNull
    @NotScript
    private int type;
    
    @NonNull
    @NotScript
    private String name;
    
    @NonNull
    @NotScript
    private String version;
    
    @NonNull
    @NotScript
    private String description;
    
    @NonNull
    @NotScript
    private int minMachines;
    
    @NonNull
    @NotScript
    private int maxMachines;

    @NonNull
    @NotScript
    private boolean replicated;
    
    @NonNull
    @NotScript
    private int minReplicationMachines;
    
    @NonNull
    @NotScript
    private int maxReplicationMachines;
    
}

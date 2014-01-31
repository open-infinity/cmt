/*
 * Copyright (c) 2014 the original author or authors.
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

package org.openinfinity.cloud.serialization;

import org.openinfinity.cloud.domain.configurationtemplate.entity.InstallationModule;

import java.util.Collection;

public class ModuleContainer {
    InstallationModule module;
    Collection<ParametersContainer> parameters;

    public ModuleContainer() {
    }

    public ModuleContainer(InstallationModule module, Collection<ParametersContainer> parameters) {
        this.module = module;
        this.parameters = parameters;
    }

    public InstallationModule getModule() {
        return module;
    }

    public void setModule(InstallationModule module) {
        this.module = module;
    }

    public Collection<ParametersContainer> getParameters() {
        return parameters;
    }

    public void setParameters(Collection<ParametersContainer> parameters) {
        this.parameters = parameters;
    }
}

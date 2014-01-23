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

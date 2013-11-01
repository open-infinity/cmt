package org.openinfinity.cloud.application.template.controller;

import lombok.NonNull;
import org.openinfinity.cloud.domain.configurationtemplate.ConfigurationElement;

public class ConfigurationElementWithSelection extends ConfigurationElement {

    private boolean selectedForTemplate;

    public ConfigurationElementWithSelection(@NonNull int id, @NonNull int type, @NonNull String name, @NonNull String version, @NonNull String description, @NonNull int parameterKey, @NonNull int minMachines, @NonNull int maxMachines, @NonNull boolean replicated, @NonNull int minReplicationMachines, @NonNull int maxReplicationMachines, boolean selectedForTemplate) {
        super(id, type, name, version, description, parameterKey, minMachines, maxMachines, replicated, minReplicationMachines, maxReplicationMachines);
        this.selectedForTemplate = selectedForTemplate;
    }

    public ConfigurationElementWithSelection(ConfigurationElement elem, boolean selected) {
        super(elem.getId(), elem.getType(), elem.getName(), elem.getVersion(), elem.getDescription(), elem.getParameterKey(),
                elem.getMinMachines(), elem.getMaxMachines(), elem.isReplicated(), elem.getMinReplicationMachines(), elem.getMaxReplicationMachines());
        selectedForTemplate = selected;
    }

    public boolean isSelectedForTemplate() {
        return selectedForTemplate;
    }

    public void setSelectedForTemplate(boolean selectedForTemplate) {
        this.selectedForTemplate = selectedForTemplate;
    }
}

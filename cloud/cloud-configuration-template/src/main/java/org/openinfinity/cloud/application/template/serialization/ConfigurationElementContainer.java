package org.openinfinity.cloud.application.template.serialization;

import org.openinfinity.cloud.domain.configurationtemplate.entity.ConfigurationElement;

import java.util.Collection;

public class ConfigurationElementContainer {

    private Collection<ConfigurationElement> available;

    private Collection<ConfigurationElement> selected;

    public ConfigurationElementContainer() {
    }

    public ConfigurationElementContainer(Collection<ConfigurationElement> available, Collection<ConfigurationElement> selectedElements) {
        this.available = available;
        this.selected = selectedElements;
    }

    public Collection<ConfigurationElement> getAvailable() {
        return available;
    }

    public void setAvailable(Collection<ConfigurationElement> available) {
        this.available = available;
    }

    public Collection<ConfigurationElement> getSelected() {
        return selected;
    }

    public void setSelected(Collection<ConfigurationElement> selected) {
        this.selected = selected;
    }
}

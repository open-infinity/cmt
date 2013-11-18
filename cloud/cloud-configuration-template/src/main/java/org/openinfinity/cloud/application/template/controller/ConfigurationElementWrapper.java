package org.openinfinity.cloud.application.template.controller;

import org.openinfinity.cloud.domain.configurationtemplate.ConfigurationElement;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class ConfigurationElementWrapper{

    private Collection<ConfigurationElement> available;

    private Collection<ConfigurationElement> selected;

    public ConfigurationElementWrapper() {
    }

    public ConfigurationElementWrapper(Collection<ConfigurationElement> available, Collection<ConfigurationElement> selectedElements) {
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

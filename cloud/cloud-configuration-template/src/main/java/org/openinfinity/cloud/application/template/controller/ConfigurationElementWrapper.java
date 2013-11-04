package org.openinfinity.cloud.application.template.controller;

import org.openinfinity.cloud.domain.configurationtemplate.ConfigurationElement;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class ConfigurationElementWrapper{

    private Collection<ConfigurationElement> availableElements;

    private Collection<ConfigurationElement> selectedElements;

    public ConfigurationElementWrapper() {
    }

    public ConfigurationElementWrapper(Collection<ConfigurationElement> availableElements, Collection<ConfigurationElement> selectedElements) {
        this.availableElements = availableElements;
        this.selectedElements = selectedElements;
    }

    public Collection<ConfigurationElement> getAvailableElements() {
        return availableElements;
    }

    public void setAvailableElements(Collection<ConfigurationElement> availableElements) {
        this.availableElements = availableElements;
    }

    public Collection<ConfigurationElement> getSelectedElements() {
        return selectedElements;
    }

    public void setSelectedElements(Collection<ConfigurationElement> selectedElements) {
        this.selectedElements = selectedElements;
    }
}

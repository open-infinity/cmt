package org.openinfinity.cloud.serialization;

import org.openinfinity.cloud.domain.configurationtemplate.entity.ConfigurationElement;

import java.util.Collection;

public class ElementContainer {

    ConfigurationElement element;
    Collection<Integer> dependees;
    Collection<ModuleContainer> modules;

    public ElementContainer() {
    }

    public ElementContainer(ConfigurationElement element, Collection<Integer> dependees, Collection<ModuleContainer> modules) {
        this.element = element;
        this.dependees = dependees;
        this.modules = modules;
    }

    public ConfigurationElement getElement() {
        return element;
    }

    public void setElement(ConfigurationElement element) {
        this.element = element;
    }

    public Collection<Integer> getDependees() {
        return dependees;
    }

    public void setDependees(Collection<Integer> dependees) {
        this.dependees = dependees;
    }

    public Collection<ModuleContainer> getModules() {
        return modules;
    }

    public void setModules(Collection<ModuleContainer> modules) {
        this.modules = modules;
    }
}

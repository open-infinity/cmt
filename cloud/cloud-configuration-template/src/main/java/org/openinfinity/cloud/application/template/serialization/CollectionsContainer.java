package org.openinfinity.cloud.application.template.serialization;

import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonTypeInfo;

import java.util.Collection;

@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
@JsonSubTypes({
        @JsonSubTypes.Type(value=org.openinfinity.cloud.domain.configurationtemplate.entity.InstallationModule.class, name="package"),
        @JsonSubTypes.Type(value=org.openinfinity.cloud.domain.configurationtemplate.entity.InstallationModule.class, name="module"),
        @JsonSubTypes.Type(value=org.openinfinity.cloud.domain.configurationtemplate.entity.ConfigurationElement.class, name="element")
})
public class CollectionsContainer<T> {

    private Collection<T> available;

    private Collection<T> selected;

    public CollectionsContainer(Collection<T> available, Collection<T> selected) {
        this.available = available;
        this.selected = selected;
    }

    public Collection<T> getAvailable() {
        return available;
    }

    public void setAvailable(Collection<T> available) {
        this.available = available;
    }

    public Collection<T> getSelected() {
        return selected;
    }

    public void setSelected(Collection<T> selected) {
        this.selected = selected;
    }
}

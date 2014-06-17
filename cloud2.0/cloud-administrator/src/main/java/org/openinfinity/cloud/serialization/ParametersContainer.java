package org.openinfinity.cloud.serialization;

import org.openinfinity.cloud.domain.configurationtemplate.entity.ParameterKey;

import java.util.Collection;

public class ParametersContainer {
    ParameterKey key;
    Collection<String> values;

    public ParametersContainer() {
    }

    public ParametersContainer(ParameterKey key, Collection<String> values) {
        this.key = key;
        this.values = values;
    }

    public ParameterKey getKey() {
        return key;
    }

    public void setKey(ParameterKey key) {
        this.key = key;
    }

    public Collection<String> getValues() {
        return values;
    }

    public void setValues(Collection<String> values) {
        this.values = values;
    }
}

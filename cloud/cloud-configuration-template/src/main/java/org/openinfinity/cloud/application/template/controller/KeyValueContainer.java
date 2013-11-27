package org.openinfinity.cloud.application.template.controller;

import org.openinfinity.cloud.domain.configurationtemplate.ParameterKey;
import org.openinfinity.cloud.domain.configurationtemplate.ParameterValue;

import java.util.Collection;

public class KeyValueContainer {

    private Collection<ParameterKey> keys;

    private Collection<ParameterValue> values;

    public KeyValueContainer() {
    }

    public KeyValueContainer(Collection<ParameterKey> keys, Collection<ParameterValue> values) {
        this.keys = keys;
        this.values = values;
    }

    public Collection<ParameterKey> getKeys() {
        return keys;
    }

    public void setKeys(Collection<ParameterKey> keys) {
        this.keys = keys;
    }

    public Collection<ParameterValue> getValues() {
        return values;
    }

    public void setValues(Collection<ParameterValue> values) {
        this.values = values;
    }
}

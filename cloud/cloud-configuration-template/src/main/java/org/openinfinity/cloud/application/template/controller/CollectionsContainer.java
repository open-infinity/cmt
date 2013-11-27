package org.openinfinity.cloud.application.template.controller;

import java.util.Collection;

public class CollectionsContainer<T> {

    private Collection<T> available;

    private Collection<T> selected;

    public CollectionsContainer() {
    }

    public CollectionsContainer(Collection<T> available, Collection<T> selected) {
        this.available = available;
        this.selected = selected;
    }

    public void setAvailable(Collection<T> available) {
        this.available = available;
    }

    public void setSelected(Collection<T> selected) {
        this.selected = selected;
    }
}

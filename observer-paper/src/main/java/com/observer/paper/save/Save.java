package com.observer.paper.save;

import com.observer.paper.layout.LayoutComponent;
import java.util.Collections;
import java.util.Map;

public final class Save {

    private final String id;
    private final Map<String, LayoutComponent> components;

    public Save(String id, Map<String, LayoutComponent> components) {
        this.id = id;
        this.components = components;
    }

    public String getId() {
        return id;
    }

    public Map<String, LayoutComponent> getComponents() {
        return Collections.unmodifiableMap(components);
    }
}

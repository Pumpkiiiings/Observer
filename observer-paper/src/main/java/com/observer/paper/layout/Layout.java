package com.observer.paper.layout;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Represents a HUD layout — an ordered, named collection of LayoutComponents.
 *
 * A Layout is a server-side abstraction only. The Fabric client never
 * receives a "Layout" packet; it only receives individual ComponentCreatePayloads
 * namespaced as "layoutId:componentId".
 */
public final class Layout {

    private final String id;
    private final Map<String, LayoutComponent> components; // insertion-ordered

    public Layout(String id, Map<String, LayoutComponent> components) {
        this.id = id;
        this.components = Collections.unmodifiableMap(new LinkedHashMap<>(components));
    }

    public String getId() { return id; }

    /** Returns components in the order they were declared in the YAML file. */
    public Map<String, LayoutComponent> getComponents() { return components; }
}

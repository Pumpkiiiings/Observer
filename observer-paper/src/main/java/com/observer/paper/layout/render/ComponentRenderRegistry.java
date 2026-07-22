package com.observer.paper.layout.render;

import com.observer.api.model.ComponentType;
import com.observer.paper.layout.LayoutComponent;
import java.util.Map;
import java.util.HashMap;

public class ComponentRenderRegistry {
    
    private final Map<ComponentType, ComponentRenderer<? extends LayoutComponent>> renderers = new HashMap<>();

    public ComponentRenderRegistry() {
        // Register default renderers
        renderers.put(ComponentType.TEXT, new TextComponentRenderer());
        renderers.put(ComponentType.ITEM, new ItemComponentRenderer());
    }

    public <T extends LayoutComponent> void register(ComponentType type, ComponentRenderer<T> renderer) {
        renderers.put(type, renderer);
    }

    @SuppressWarnings("unchecked")
    public <T extends LayoutComponent> ComponentRenderer<T> getRenderer(ComponentType type) {
        return (ComponentRenderer<T>) renderers.get(type);
    }
}

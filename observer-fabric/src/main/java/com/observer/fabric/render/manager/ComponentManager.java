package com.observer.fabric.render.manager;

import com.observer.fabric.render.component.ObserverComponent;
import net.minecraft.resources.Identifier;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ComponentManager {
    private static final Map<Identifier, ObserverComponent> COMPONENTS = new ConcurrentHashMap<>();

    public static void addComponent(Identifier id, ObserverComponent component) {
        COMPONENTS.put(id, component);
        com.observer.fabric.ObserverFabric.LOGGER.info("[Observer] Stored component ID: {}", id);
        com.observer.fabric.ObserverFabric.LOGGER.info("[Observer] Component Type: {}", component.getClass().getSimpleName());
        com.observer.fabric.ObserverFabric.LOGGER.info("[Observer] Current map size: {}", COMPONENTS.size());
    }

    public static ObserverComponent getComponent(Identifier id) {
        return COMPONENTS.get(id);
    }

    public static void removeComponent(Identifier id) {
        COMPONENTS.remove(id);
    }

    public static void clear() {
        COMPONENTS.clear();
    }

    public static Collection<ObserverComponent> getActiveComponents() {
        return COMPONENTS.values();
    }
}

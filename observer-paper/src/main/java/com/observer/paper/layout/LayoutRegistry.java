package com.observer.paper.layout;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory registry of all loaded Layout objects.
 * Populated by LayoutLoader on startup and on /observer layout reload.
 */
public final class LayoutRegistry {

    private final Map<String, Layout> layouts = new ConcurrentHashMap<>();

    public void registerAll(Map<String, Layout> loaded) {
        layouts.clear();
        layouts.putAll(loaded);
    }

    public Optional<Layout> get(String id) {
        return Optional.ofNullable(layouts.get(id));
    }

    public Map<String, Layout> getAll() {
        return Collections.unmodifiableMap(layouts);
    }

    public boolean has(String id) {
        return layouts.containsKey(id);
    }
}

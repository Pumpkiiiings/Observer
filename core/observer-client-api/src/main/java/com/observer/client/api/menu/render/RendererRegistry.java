package com.observer.client.api.menu.render;

import java.util.HashMap;
import java.util.Map;

/**
 * Registry for available MenuRenderers on the client.
 */
public class RendererRegistry {
    
    private static final Map<String, MenuRenderer> renderers = new HashMap<>();
    private static String activeRendererId;

    public static void register(String id, MenuRenderer renderer) {
        renderers.put(id, renderer);
        if (activeRendererId == null) {
            activeRendererId = id; // Set default to first registered
        }
    }

    public static MenuRenderer getRenderer(String id) {
        return renderers.get(id);
    }

    public static MenuRenderer getActiveRenderer() {
        if (activeRendererId == null) {
            throw new IllegalStateException("No MenuRenderer is registered in the client!");
        }
        return renderers.get(activeRendererId);
    }

    public static void setActiveRenderer(String id) {
        if (!renderers.containsKey(id)) {
            throw new IllegalArgumentException("Renderer not found: " + id);
        }
        activeRendererId = id;
    }
}

package com.observer.client.api.menu.render;

/**
 * Interface defining how menus are rendered on the client.
 * This completely decouples observer-api from any UI framework (Vanilla, UI Lib, etc).
 */
public interface MenuRenderer {
    
    /**
     * Called when the server requests to open a menu.
     * @param menuJson The serialized MenuDefinition JSON.
     */
    void open(String menuJson);
    
    /**
     * Called when the server requests to close the current menu.
     */
    void close();
    
    /**
     * Called when the server sends a state/layout update without reopening the menu.
     * @param menuJson The updated serialized MenuDefinition JSON.
     */
    void update(String menuJson);
    
    /**
     * Called when the server sends an update for a single specific element.
     * @param elementId The ID of the element to update.
     * @param elementJson The serialized MenuElement JSON.
     */
    void updateElement(String elementId, String elementJson);
}

package com.observer.paper.layout.render;

import com.observer.paper.layout.LayoutComponent;
import org.bukkit.entity.Player;

/**
 * Handles extracting diff states, creating, updating, and removing a specific LayoutComponent type.
 * @param <T> The LayoutComponent subclass this renderer handles.
 */
public interface ComponentRenderer<T extends LayoutComponent> {
    
    /**
     * Extracts the current dynamic state of the component (e.g. placeholder resolved text).
     * This is used by the tracker to diff and determine if an update packet is needed.
     */
    String extractState(Player player, T component);
    
    /**
     * Called to send the creation payload to the player.
     */
    void create(Player player, String fullComponentId, T component, String state);
    
    /**
     * Called to send the update payload to the player when the state changes.
     */
    void update(Player player, String fullComponentId, T component, String oldState, String newState);
    
    /**
     * Called to send the removal payload to the player.
     */
    void remove(Player player, String fullComponentId);
}

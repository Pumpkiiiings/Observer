package com.observer.paper.layout;

import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tracks which layouts are currently visible to which players.
 * Also stores the last-rendered string for every component per player,
 * enabling diff-based updates — we only send a packet if the resolved
 * string changed since the last tick.
 *
 * Key design:
 *   activeLayouts:    playerUUID → Set of active layoutIds
 *   renderedValues:   playerUUID:layoutId:componentId → last rendered string
 */
public final class ActiveLayoutTracker {

    /** player UUID → set of active layout IDs */
    private final Map<UUID, Set<String>> activeLayouts = new ConcurrentHashMap<>();

    /**
     * Cache of the last string that was sent to the client.
     * Key: "uuid/layoutId/componentId"
     */
    private final Map<String, String> renderedValues = new ConcurrentHashMap<>();

    // -----------------------------------------------------------------------
    // Activation / Deactivation
    // -----------------------------------------------------------------------

    public void activate(Player player, String layoutId) {
        activeLayouts.computeIfAbsent(player.getUniqueId(), k -> ConcurrentHashMap.newKeySet())
                .add(layoutId);
    }

    public void deactivate(Player player, String layoutId) {
        Set<String> layouts = activeLayouts.get(player.getUniqueId());
        if (layouts != null) {
            layouts.remove(layoutId);
            // Purge cached rendered values for this layout
            String prefix = cachePrefix(player, layoutId);
            renderedValues.keySet().removeIf(k -> k.startsWith(prefix));
        }
    }

    public void deactivateAll(Player player) {
        activeLayouts.remove(player.getUniqueId());
        String uuidStr = player.getUniqueId().toString();
        renderedValues.keySet().removeIf(k -> k.startsWith(uuidStr + "/"));
    }

    public boolean isActive(Player player, String layoutId) {
        Set<String> layouts = activeLayouts.get(player.getUniqueId());
        return layouts != null && layouts.contains(layoutId);
    }

    public Set<String> getActiveLayouts(Player player) {
        return activeLayouts.getOrDefault(player.getUniqueId(), Collections.emptySet());
    }

    public Map<UUID, Set<String>> getAllActive() {
        return Collections.unmodifiableMap(activeLayouts);
    }

    // -----------------------------------------------------------------------
    // Diff-based value tracking
    // -----------------------------------------------------------------------

    /**
     * Returns the last string sent to this player for this component,
     * then updates the cache with the new value.
     * Returns null if no previous value is cached (first render).
     */
    public String getAndUpdate(Player player, String layoutId, String componentId, String newValue) {
        String key = cacheKey(player, layoutId, componentId);
        return renderedValues.put(key, newValue);
    }

    /** True if the new value differs from the last sent value (or was never sent). */
    public boolean isDiff(Player player, String layoutId, String componentId, String newValue) {
        String key = cacheKey(player, layoutId, componentId);
        String previous = renderedValues.get(key);
        return !newValue.equals(previous);
    }

    public void removeState(Player player, String layoutId, String componentId) {
        renderedValues.remove(cacheKey(player, layoutId, componentId));
    }

    // -----------------------------------------------------------------------
    // Internal helpers
    // -----------------------------------------------------------------------

    private static String cacheKey(Player player, String layoutId, String componentId) {
        return player.getUniqueId() + "/" + layoutId + "/" + componentId;
    }

    private static String cachePrefix(Player player, String layoutId) {
        return player.getUniqueId() + "/" + layoutId + "/";
    }
}

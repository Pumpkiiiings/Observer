package com.observer.paper.layout;

import com.observer.paper.ObserverPaper;
import com.observer.paper.api.ObserverAPI;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Optional;

/**
 * Central façade for the Observer Layout System.
 *
 * Owns the LayoutRegistry, ActiveLayoutTracker, and LayoutUpdateTask.
 * Exposes a clean API for showing, hiding, and reloading layouts.
 *
 * Usage:
 * LayoutManager.get().show("lobby", player);
 * LayoutManager.get().hide("lobby", player);
 * LayoutManager.get().reload();
 */
public final class LayoutManager {

    private static final boolean PAPI_AVAILABLE = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;

    private final ObserverPaper plugin;
    private final LayoutRegistry registry;
    private final ActiveLayoutTracker tracker;
    private final LayoutLoader loader;
    private final com.observer.paper.layout.render.ComponentRenderRegistry renderRegistry;
    private LayoutUpdateTask updateTask;

    public LayoutManager(ObserverPaper plugin) {
        this.plugin = plugin;
        this.registry = new LayoutRegistry();
        this.tracker = new ActiveLayoutTracker();
        this.loader = new LayoutLoader(plugin);
        this.renderRegistry = new com.observer.paper.layout.render.ComponentRenderRegistry();
    }

    /**
     * Called once from ObserverPaper#onEnable.
     * Loads all layouts from disk and starts the update task.
     */
    public void initialize() {
        reload();
        startUpdateTask();
    }

    /**
     * Reloads all layout files from disk.
     * Active display state is preserved — running layouts will pick up
     * new content on the next update tick.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void reload() {
        Map<String, Layout> oldLayouts = new java.util.HashMap<>(registry.getAll());
        
        registry.registerAll(loader.loadAll());
        
        // Hot swap active players
        for (Player player : Bukkit.getOnlinePlayers()) {
            for (String layoutId : tracker.getActiveLayouts(player)) {
                Optional<Layout> newOpt = registry.get(layoutId);
                Layout oldLayout = oldLayouts.get(layoutId);
                
                if (newOpt.isEmpty()) {
                    hide(layoutId, player);
                } else if (oldLayout != null) {
                    Layout newLayout = newOpt.get();
                    // Diff components
                    for (String oldCompKey : oldLayout.getComponents().keySet()) {
                        if (!newLayout.getComponents().containsKey(oldCompKey)) {
                            // Deleted
                            String compId = layoutId + ":" + oldCompKey;
                            com.observer.paper.layout.render.ComponentRenderer renderer = renderRegistry.getRenderer(oldLayout.getComponents().get(oldCompKey).getType());
                            if (renderer != null) renderer.remove(player, compId);
                            tracker.removeState(player, layoutId, oldCompKey);
                        }
                    }
                    for (LayoutComponent newComp : newLayout.getComponents().values()) {
                        LayoutComponent oldComp = oldLayout.getComponents().get(newComp.getId());
                        String compId = layoutId + ":" + newComp.getId();
                        com.observer.paper.layout.render.ComponentRenderer renderer = renderRegistry.getRenderer(newComp.getType());
                        if (renderer != null) {
                            if (oldComp == null) {
                                // New
                                String state = renderer.extractState(player, newComp);
                                tracker.getAndUpdate(player, layoutId, newComp.getId(), state);
                                renderer.create(player, compId, newComp, state);
                            } else {
                                // Existing. Check if static properties changed.
                                boolean changed = oldComp.getType() != newComp.getType() ||
                                        oldComp.getAlignment() != newComp.getAlignment() ||
                                        oldComp.getOffsetX() != newComp.getOffsetX() ||
                                        oldComp.getOffsetY() != newComp.getOffsetY() ||
                                        oldComp.getScale() != newComp.getScale() ||
                                        oldComp.getTextAlignment() != newComp.getTextAlignment();
                                
                                if (changed) {
                                    // Recreate
                                    renderer.remove(player, compId);
                                    String state = renderer.extractState(player, newComp);
                                    tracker.getAndUpdate(player, layoutId, newComp.getId(), state);
                                    renderer.create(player, compId, newComp, state);
                                } else {
                                    // Static props same. Just force text update immediately
                                    String newState = renderer.extractState(player, newComp);
                                    tracker.getAndUpdate(player, layoutId, newComp.getId(), newState);
                                    renderer.update(player, compId, newComp, "", newState);
                                }
                            }
                        }
                    }
                }
            }
        }
        
        plugin.getLogger().info("[Observer] Loaded " + registry.getAll().size() + " layout(s).");
    }

    /**
     * Displays a layout to a player.
     *
     * Sends one ComponentCreatePayload per component in the layout.
     * If the player already has this layout active, this is a no-op
     * to prevent duplicate component creation.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void show(String layoutId, Player player) {
        if (!ObserverAPI.isObserverPlayer(player)) {
            player.sendMessage("§c[Observer] Your client does not have Observer installed.");
            return;
        }

        Optional<Layout> opt = registry.get(layoutId);
        if (opt.isEmpty()) {
            player.sendMessage("§c[Observer] Unknown layout: " + layoutId);
            return;
        }

        if (tracker.isActive(player, layoutId)) {
            return; // Already displayed — avoid recreating components
        }

        Layout layout = opt.get();

        for (LayoutComponent component : layout.getComponents().values()) {
            String componentId = layout.getId() + ":" + component.getId();
            com.observer.paper.layout.render.ComponentRenderer renderer = renderRegistry.getRenderer(component.getType());
            if (renderer == null) continue;

            String state = renderer.extractState(player, component);

            // Prime the diff cache so the first update tick is a no-op
            tracker.getAndUpdate(player, layout.getId(), component.getId(), state);

            renderer.create(player, componentId, component, state);
        }

        tracker.activate(player, layoutId);
    }

    /**
     * Hides a layout from a player by removing all its components.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void hide(String layoutId, Player player) {
        Optional<Layout> opt = registry.get(layoutId);
        if (opt.isEmpty())
            return;

        Layout layout = opt.get();

        for (LayoutComponent component : layout.getComponents().values()) {
            String componentId = layout.getId() + ":" + component.getId();
            com.observer.paper.layout.render.ComponentRenderer renderer = renderRegistry.getRenderer(component.getType());
            if (renderer != null) {
                renderer.remove(player, componentId);
            }
        }

        tracker.deactivate(player, layoutId);
    }

    /** Hides all active layouts from a player (called on disconnect). */
    public void hideAll(Player player) {
        for (String layoutId : tracker.getActiveLayouts(player)) {
            hide(layoutId, player);
        }
        tracker.deactivateAll(player);
    }

    public LayoutRegistry getRegistry() {
        return registry;
    }

    public ActiveLayoutTracker getTracker() {
        return tracker;
    }
    
    public com.observer.paper.layout.render.ComponentRenderRegistry getRenderRegistry() {
        return renderRegistry;
    }

    // -----------------------------------------------------------------------

    private void startUpdateTask() {
        if (updateTask != null) {
            updateTask.cancel();
        }
        updateTask = new LayoutUpdateTask(registry, tracker, renderRegistry);
        updateTask.start(plugin, 20L); // every 1 second
    }
}

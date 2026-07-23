package com.observer.paper.layout;

import com.observer.paper.ObserverPaper;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Repeating task that resolves diff states for all active layouts
 * and sends update payloads ONLY when a state has changed since
 * the last tick (diff-based updates).
 */
public final class LayoutUpdateTask extends BukkitRunnable {

    private final LayoutRegistry registry;
    private final ActiveLayoutTracker tracker;
    private final com.observer.paper.layout.render.ComponentRenderRegistry renderRegistry;

    public LayoutUpdateTask(LayoutRegistry registry, ActiveLayoutTracker tracker, com.observer.paper.layout.render.ComponentRenderRegistry renderRegistry) {
        this.registry = registry;
        this.tracker = tracker;
        this.renderRegistry = renderRegistry;
    }

    @Override
    public void run() {
        for (Map.Entry<UUID, Set<String>> entry : tracker.getAllActive().entrySet()) {
            Player player = Bukkit.getPlayer(entry.getKey());
            if (player == null || !player.isOnline()) continue;

            for (String layoutId : entry.getValue()) {
                registry.get(layoutId).ifPresent(layout -> processLayout(player, layout));
            }
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void processLayout(Player player, Layout layout) {
        for (LayoutComponent component : layout.getComponents().values()) {
            com.observer.paper.layout.render.ComponentRenderer renderer = renderRegistry.getRenderer(component.getType());
            if (renderer == null) continue;

            String state = renderer.extractState(player, component);

            // Diff check — skip packet if state did not change
            if (!tracker.isDiff(player, layout.getId(), component.getId(), state)) {
                continue;
            }

            // Update cache and send packet
            String oldState = tracker.getAndUpdate(player, layout.getId(), component.getId(), state);
            String componentId = layout.getId() + ":" + component.getId();
            
            renderer.update(player, componentId, component, oldState, state);
        }
    }

    /**
     * Starts the task, running every {@code intervalTicks} server ticks.
     */
    public void start(ObserverPaper plugin, long intervalTicks) {
        this.runTaskTimer(plugin, intervalTicks, intervalTicks);
    }
}

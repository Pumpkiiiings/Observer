package com.observer.paper.save;

import com.observer.paper.ObserverPaper;
import com.observer.paper.api.ObserverAPI;
import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class SaveManager {

    private final ObserverPaper plugin;
    private final SaveLoader loader;
    private final Map<String, Save> registry = new HashMap<>();

    public SaveManager(ObserverPaper plugin) {
        this.plugin = plugin;
        this.loader = new SaveLoader(plugin);
    }

    public void initialize() {
        reload();
    }

    public void reload() {
        registry.clear();
        registry.putAll(loader.loadAll());
        plugin.getLogger().info("[Observer] Loaded " + registry.size() + " save(s).");
    }

    public Optional<Save> getSave(String id) {
        return Optional.ofNullable(registry.get(id));
    }

    public Map<String, Save> getAll() {
        return registry;
    }

    /**
     * Displays a save to a player. Unlike Layouts, Saves are just one-time static renders.
     * They do not auto-update or use the diff tracker. (Wait, if they use PlaceholderAPI, 
     * should they auto-update? The user said "Cada save será una colección reutilizable de componentes."
     * We'll render them statically for now. If they need updates, they can be added to the tracker later).
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void display(String saveId, Player player) {
        if (!ObserverAPI.isObserverPlayer(player)) {
            player.sendMessage("§c[Observer] Your client does not have Observer installed.");
            return;
        }

        Save save = registry.get(saveId);
        if (save == null) {
            player.sendMessage("§c[Observer] Unknown save: " + saveId);
            return;
        }

        for (com.observer.paper.layout.LayoutComponent component : save.getComponents().values()) {
            String componentId = save.getId() + ":" + component.getId();
            com.observer.paper.layout.render.ComponentRenderer renderer = plugin.getLayoutManager().getRenderRegistry().getRenderer(component.getType());
            if (renderer == null) continue;

            String state = renderer.extractState(player, component);
            renderer.create(player, componentId, component, state);
        }
    }
}

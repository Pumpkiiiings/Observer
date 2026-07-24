package com.observer.paper.animation;

import com.observer.paper.ObserverPaper;
import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class ServerAnimationManager {
    private final ObserverPaper plugin;
    private final Map<String, String> loadedAnimations = new HashMap<>();
    private IdleDetector idleDetector;

    public ServerAnimationManager(ObserverPaper plugin) {
        this.plugin = plugin;
    }

    public void initialize() {
        this.idleDetector = new IdleDetector(plugin);
        reload();
    }

    public void reload() {
        loadedAnimations.clear();
        File animDir = new File(plugin.getDataFolder(), "animations");
        
        if (!animDir.exists()) {
            animDir.mkdirs();
            plugin.getLogger().info("Created animations directory.");
            return;
        }

        File[] files = animDir.listFiles((d, name) -> name.endsWith(".json"));
        if (files == null) return;

        for (File file : files) {
            try {
                String content = Files.readString(file.toPath());
                String name = file.getName().replace(".json", "");
                loadedAnimations.put(name, content);
            } catch (Exception e) {
                plugin.getLogger().severe("Failed to load animation: " + file.getName());
                e.printStackTrace();
            }
        }
        plugin.getLogger().info("Loaded " + loadedAnimations.size() + " server-side animations.");

        if (plugin.getNetworkManager() != null && plugin.getPlayerManager() != null) {
            com.observer.api.payload.action.SyncAnimationsPayload syncPayload = new com.observer.api.payload.action.SyncAnimationsPayload(loadedAnimations);
            for (org.bukkit.entity.Player p : org.bukkit.Bukkit.getOnlinePlayers()) {
                if (plugin.getPlayerManager().isObserver(p)) {
                    plugin.getNetworkManager().sendPayload(p, com.observer.api.ObserverChannels.channel(com.observer.api.ObserverChannels.SYNC_ANIMATIONS), syncPayload, com.observer.api.payload.action.SyncAnimationsPayload.CODEC);
                }
            }
        }
    }

    public Map<String, String> getAnimations() {
        return loadedAnimations;
    }
}

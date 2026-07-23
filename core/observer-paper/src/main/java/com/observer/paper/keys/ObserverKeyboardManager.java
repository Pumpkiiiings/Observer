package com.observer.paper.keys;

import com.observer.paper.ObserverPaper;
import com.observer.paper.api.event.ObserverPlayerKeyEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class ObserverKeyboardManager {
    private final Map<UUID, Set<Byte>> playerKeyStates = new ConcurrentHashMap<>();
    
    // Stats
    private final AtomicLong packetsReceived = new AtomicLong(0);
    private final AtomicLong lastUpdateGlobal = new AtomicLong(0);
    private final Map<UUID, Long> lastUpdatePerPlayer = new ConcurrentHashMap<>();

    public void updateKeys(Player player, Set<Byte> pressedKeys) {
        packetsReceived.incrementAndGet();
        long now = System.currentTimeMillis();
        lastUpdateGlobal.set(now);
        lastUpdatePerPlayer.put(player.getUniqueId(), now);

        if (ObserverPaper.getInstance().getConfig().getBoolean("debug.keyboard", false)) {
            ObserverPaper.getInstance().getLogger().info("[Observer Keys Debug] Received keys from " + player.getName() + " (UUID: " + player.getUniqueId() + "): " + pressedKeys);
        }

        playerKeyStates.put(player.getUniqueId(), pressedKeys);

        // Fire Bukkit Event
        ObserverPlayerKeyEvent event = new ObserverPlayerKeyEvent(player, pressedKeys);
        Bukkit.getPluginManager().callEvent(event);
    }

    public boolean isKeyDown(UUID uuid, byte asciiKey) {
        Set<Byte> keys = playerKeyStates.getOrDefault(uuid, Collections.emptySet());
        return keys.contains(asciiKey);
    }

    public Set<Byte> getPressedKeys(UUID uuid) {
        return playerKeyStates.getOrDefault(uuid, Collections.emptySet());
    }

    public void flush(UUID uuid) {
        playerKeyStates.remove(uuid);
    }

    // Stats getters
    public long getPacketsReceived() {
        return packetsReceived.get();
    }

    public long getLastUpdateGlobal() {
        return lastUpdateGlobal.get();
    }

    public int getTrackedPlayersCount() {
        return playerKeyStates.size();
    }
}

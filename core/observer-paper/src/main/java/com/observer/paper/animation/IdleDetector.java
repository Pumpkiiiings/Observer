package com.observer.paper.animation;

import com.observer.paper.ObserverPaper;
import com.observer.paper.api.PaperObserverAnimationAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import com.observer.paper.api.events.ObserverPlayerIdleEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class IdleDetector implements Listener {
    private final ObserverPaper plugin;
    private final Map<UUID, Long> lastMoveTimes = new HashMap<>();
    private final Map<UUID, Boolean> isIdle = new HashMap<>();

    public IdleDetector(ObserverPaper plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);

        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (!plugin.getConfig().getBoolean("animations.idle.enabled", false)) return;

            long threshold = plugin.getConfig().getLong("animations.idle.threshold-ms", 1000);
            String animName = plugin.getConfig().getString("animations.idle.animation-name", "idle");
            long now = System.currentTimeMillis();

            for (Player player : Bukkit.getOnlinePlayers()) {
                long lastMove = lastMoveTimes.getOrDefault(player.getUniqueId(), now);

                if (now - lastMove > threshold && player.isOnGround() && !player.isFlying()) {
                    if (!isIdle.getOrDefault(player.getUniqueId(), false)) {
                        isIdle.put(player.getUniqueId(), true);
                        
                        // Lanzar evento de API
                        ObserverPlayerIdleEvent idleEvent = new ObserverPlayerIdleEvent(player, true);
                        Bukkit.getPluginManager().callEvent(idleEvent);
                        
                        PaperObserverAnimationAPI.playAnimation(player, animName);
                    }
                }
            }
        }, 10L, 10L);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!plugin.getConfig().getBoolean("animations.idle.enabled", false)) return;

        // Check if player actually moved (ignore head rotation)
        if (event.getFrom().distanceSquared(event.getTo()) > 0.0001) {
            Player player = event.getPlayer();
            lastMoveTimes.put(player.getUniqueId(), System.currentTimeMillis());

            // If player was idle and now moved, stop idle animation
            if (isIdle.getOrDefault(player.getUniqueId(), false)) {
                isIdle.put(player.getUniqueId(), false);
                
                // Lanzar evento de API
                ObserverPlayerIdleEvent idleEvent = new ObserverPlayerIdleEvent(player, false);
                Bukkit.getPluginManager().callEvent(idleEvent);
                
                PaperObserverAnimationAPI.stopAnimation(player);
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        lastMoveTimes.remove(event.getPlayer().getUniqueId());
        isIdle.remove(event.getPlayer().getUniqueId());
    }
}

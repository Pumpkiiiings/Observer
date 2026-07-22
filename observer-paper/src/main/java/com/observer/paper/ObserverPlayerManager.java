package com.observer.paper;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ObserverPlayerManager implements Listener {
    private final Map<Player, ObserverPlayer> observerPlayers = new ConcurrentHashMap<>();

    public void addObserverPlayer(Player player, ObserverPlayer observerPlayer) {
        observerPlayers.put(player, observerPlayer);
    }

    public void removeObserverPlayer(Player player) {
        observerPlayers.remove(player);
    }

    public ObserverPlayer getObserverPlayer(Player player) {
        return observerPlayers.get(player);
    }

    public int getObserverPlayerCount() {
        return observerPlayers.size();
    }

    public boolean isObserver(Player player) {
        return observerPlayers.containsKey(player);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        org.bukkit.Bukkit.getScheduler().runTaskLater(ObserverPaper.getInstance(), () -> {
            if (event.getPlayer().isOnline()) {
                ObserverPaper.getInstance().getNetworkManager().sendHandshakeRequest(event.getPlayer());
            }
        }, 20L);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        removeObserverPlayer(event.getPlayer());
        ObserverPaper.getInstance().getLayoutManager().getTracker().deactivateAll(event.getPlayer());
    }
}

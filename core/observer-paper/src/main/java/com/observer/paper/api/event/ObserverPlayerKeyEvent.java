package com.observer.paper.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * Fired when a player's set of pressed keys changes (Client -> Server).
 */
public class ObserverPlayerKeyEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final Set<Byte> pressedKeys;

    public ObserverPlayerKeyEvent(Player player, Set<Byte> pressedKeys) {
        super(!org.bukkit.Bukkit.isPrimaryThread()); // Determine if async dynamically based on current thread
        this.player = player;
        this.pressedKeys = pressedKeys;
    }

    public Player getPlayer() {
        return player;
    }

    public Set<Byte> getPressedKeys() {
        return pressedKeys;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}

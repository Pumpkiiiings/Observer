package com.observer.paper.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ObserverPlayerIdleEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Player player;
    private final boolean isIdle;

    public ObserverPlayerIdleEvent(Player player, boolean isIdle) {
        this.player = player;
        this.isIdle = isIdle;
    }

    public Player getPlayer() {
        return player;
    }

    public boolean isIdle() {
        return isIdle;
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

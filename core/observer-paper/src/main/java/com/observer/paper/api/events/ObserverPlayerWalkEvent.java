package com.observer.paper.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ObserverPlayerWalkEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Player player;
    private final boolean isWalking;

    public ObserverPlayerWalkEvent(Player player, boolean isWalking) {
        this.player = player;
        this.isWalking = isWalking;
    }

    public Player getPlayer() {
        return player;
    }

    public boolean isWalking() {
        return isWalking;
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

package com.observer.paper.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ObserverPlayerSneakEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Player player;
    private final boolean sneaking;

    public ObserverPlayerSneakEvent(Player player, boolean sneaking) {
        this.player = player;
        this.sneaking = sneaking;
    }

    public Player getPlayer() {
        return player;
    }

    public boolean isSneaking() {
        return sneaking;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}

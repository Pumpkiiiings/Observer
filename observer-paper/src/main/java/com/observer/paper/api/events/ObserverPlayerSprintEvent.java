package com.observer.paper.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ObserverPlayerSprintEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Player player;
    private final boolean sprinting;

    public ObserverPlayerSprintEvent(Player player, boolean sprinting) {
        this.player = player;
        this.sprinting = sprinting;
    }

    public Player getPlayer() {
        return player;
    }

    public boolean isSprinting() {
        return sprinting;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}

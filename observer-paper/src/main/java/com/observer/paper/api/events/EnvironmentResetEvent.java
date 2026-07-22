package com.observer.paper.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class EnvironmentResetEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Player player;

    public EnvironmentResetEvent(@NotNull Player player) {
        this.player = player;
    }

    @NotNull
    public Player getPlayer() {
        return player;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}

package com.observer.paper.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlayerKeyReleaseEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Player player;
    private final int asciiKey;

    public PlayerKeyReleaseEvent(@NotNull Player player, int asciiKey) {
        this.player = player;
        this.asciiKey = asciiKey;
    }

    @NotNull
    public Player getPlayer() {
        return player;
    }

    public int getAsciiKey() {
        return asciiKey;
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

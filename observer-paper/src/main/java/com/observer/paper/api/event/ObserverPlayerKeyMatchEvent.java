package com.observer.paper.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * Event for the matchgroup functionality if needed by other plugins.
 */
public class ObserverPlayerKeyMatchEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final Set<Byte> matchedKeys;

    public ObserverPlayerKeyMatchEvent(Player player, Set<Byte> matchedKeys) {
        super(true);
        this.player = player;
        this.matchedKeys = matchedKeys;
    }

    public Player getPlayer() {
        return player;
    }

    public Set<Byte> getMatchedKeys() {
        return matchedKeys;
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

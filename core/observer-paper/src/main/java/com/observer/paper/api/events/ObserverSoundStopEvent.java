package com.observer.paper.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ObserverSoundStopEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Player player;
    private final String soundId;

    public ObserverSoundStopEvent(@NotNull Player player, @NotNull String soundId) {
        this.player = player;
        this.soundId = soundId;
    }

    @NotNull
    public Player getPlayer() {
        return player;
    }

    @NotNull
    public String getSoundId() {
        return soundId;
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

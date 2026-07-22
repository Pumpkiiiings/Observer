package com.observer.paper.api.events;

import com.observer.api.sound.SoundDefinition;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ObserverSoundStartEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Player player;
    private final SoundDefinition sound;
    private boolean cancelled = false;

    public ObserverSoundStartEvent(@NotNull Player player, @NotNull SoundDefinition sound) {
        this.player = player;
        this.sound = sound;
    }

    @NotNull
    public Player getPlayer() {
        return player;
    }

    @NotNull
    public SoundDefinition getSound() {
        return sound;
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

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}

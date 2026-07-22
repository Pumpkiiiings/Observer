package com.observer.paper.api.events;

import com.observer.api.environment.EnvironmentProfile;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class EnvironmentApplyEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Player player;
    private final EnvironmentProfile profile;

    public EnvironmentApplyEvent(@NotNull Player player, @NotNull EnvironmentProfile profile) {
        this.player = player;
        this.profile = profile;
    }

    @NotNull
    public Player getPlayer() {
        return player;
    }

    @NotNull
    public EnvironmentProfile getProfile() {
        return profile;
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

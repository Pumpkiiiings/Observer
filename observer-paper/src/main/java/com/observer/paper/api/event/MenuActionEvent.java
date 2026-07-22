package com.observer.paper.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class MenuActionEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Player player;
    private final String menuId;
    private final String reference;

    public MenuActionEvent(Player player, String menuId, String reference) {
        this.player = player;
        this.menuId = menuId;
        this.reference = reference;
    }

    public Player getPlayer() {
        return player;
    }

    public String getMenuId() {
        return menuId;
    }

    public String getReference() {
        return reference;
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

package com.observer.paper.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ObserverMenuActionEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final String menuId;
    private final String elementId;
    private final String actionId;
    private final String value;

    public ObserverMenuActionEvent(Player player, String menuId, String elementId, String actionId, String value) {
        this.player = player;
        this.menuId = menuId;
        this.elementId = elementId;
        this.actionId = actionId;
        this.value = value;
    }

    public Player getPlayer() { return player; }
    public String getMenuId() { return menuId; }
    public String getElementId() { return elementId; }
    public String getActionId() { return actionId; }
    public String getValue() { return value; }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}

package com.observer.paper.layout.render;

import com.observer.paper.api.ObserverAPI;
import com.observer.paper.layout.component.ItemComponentImpl;
import org.bukkit.entity.Player;

public class ItemComponentRenderer implements ComponentRenderer<ItemComponentImpl> {

    @Override
    public String extractState(Player player, ItemComponentImpl component) {
        // Items are currently static. If we support placeholders in items later, we extract them here.
        return component.getMaterial() + ":" + component.getAmount();
    }

    @Override
    public void create(Player player, String fullComponentId, ItemComponentImpl component, String state) {
        ObserverAPI.createItem(
                player,
                fullComponentId,
                component.getMaterial(),
                component.getAmount(),
                component.getAlignment(),
                component.getOffsetX(),
                component.getOffsetY(),
                component.getScale(),
                component.getTextAlignment()
        );
    }

    @Override
    public void update(Player player, String fullComponentId, ItemComponentImpl component, String oldState, String newState) {
        // Not implemented for items yet
    }

    @Override
    public void remove(Player player, String fullComponentId) {
        ObserverAPI.removeComponent(player, fullComponentId);
    }
}

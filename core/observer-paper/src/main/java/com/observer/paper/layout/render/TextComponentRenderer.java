package com.observer.paper.layout.render;

import com.observer.paper.api.ObserverAPI;
import com.observer.paper.layout.component.TextComponentImpl;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TextComponentRenderer implements ComponentRenderer<TextComponentImpl> {

    private static final boolean PAPI_AVAILABLE = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;

    @Override
    public String extractState(Player player, TextComponentImpl component) {
        String template = component.getContent();
        if (PAPI_AVAILABLE && template != null) {
            return PlaceholderAPI.setPlaceholders(player, template);
        }
        return template == null ? "" : template;
    }

    @Override
    public void create(Player player, String fullComponentId, TextComponentImpl component, String state) {
        ObserverAPI.createText(player, fullComponentId, state, component.getAlignment(), component.getOffsetX(), component.getOffsetY(), component.getScale(), component.getTextAlignment());
    }

    @Override
    public void update(Player player, String fullComponentId, TextComponentImpl component, String oldState, String newState) {
        ObserverAPI.updateText(player, fullComponentId, newState);
    }

    @Override
    public void remove(Player player, String fullComponentId) {
        ObserverAPI.removeComponent(player, fullComponentId);
    }
}

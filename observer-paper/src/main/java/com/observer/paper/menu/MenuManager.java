package com.observer.paper.menu;

import com.observer.paper.ObserverPaper;
import com.observer.paper.api.event.MenuActionEvent;
import com.observer.paper.api.event.MenuCloseEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;

public class MenuManager implements Listener {
    private final Map<String, ObserverMenu> registeredMenus = new HashMap<>();

    public MenuManager(ObserverPaper plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void registerMenu(ObserverMenu menu) {
        registeredMenus.put(menu.getMenuId(), menu);
    }

    public ObserverMenu getMenu(String menuId) {
        return registeredMenus.get(menuId);
    }

    @EventHandler
    public void onMenuAction(MenuActionEvent event) {
        ObserverMenu menu = registeredMenus.get(event.getMenuId());
        if (menu != null) {
            menu.onClick(event.getPlayer(), event.getReference());
        }
    }

    @EventHandler
    public void onMenuClose(MenuCloseEvent event) {
        ObserverMenu menu = registeredMenus.get(event.getMenuId());
        if (menu != null) {
            menu.onClose(event.getPlayer());
        }
    }
}

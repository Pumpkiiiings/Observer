package com.observer.paper.menu;

import com.observer.api.payload.ui.MenuOpenPayload;
import com.observer.paper.ObserverPaper;
import org.bukkit.entity.Player;

public abstract class ObserverMenu {
    private final String menuId;

    public ObserverMenu(String menuId) {
        this.menuId = menuId;
    }

    public String getMenuId() {
        return menuId;
    }

    protected abstract void build(MenuBuilder builder);

    public void open(Player player) {
        MenuBuilder builder = new MenuBuilder();
        build(builder);
        
        MenuOpenPayload payload = new MenuOpenPayload(menuId, builder.build());
        ObserverPaper.getInstance().getNetworkManager().sendPayload(
                player,
                com.observer.api.ObserverChannels.channel(com.observer.api.ObserverChannels.MENU_OPEN),
                payload,
                MenuOpenPayload.CODEC
        );
    }
    
    public abstract void onClick(Player player, String reference);
    public abstract void onClose(Player player);
}

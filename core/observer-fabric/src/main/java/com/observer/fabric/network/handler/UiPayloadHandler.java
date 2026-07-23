package com.observer.fabric.network.handler;

import com.observer.api.payload.ui.MenuOpenPayload;
import com.observer.fabric.client.ObserverDynamicScreen;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;

public class UiPayloadHandler {

    public static void handleOpen(MenuOpenPayload payload, ClientPlayNetworking.Context context) {
        context.client().execute(() -> {
            try {
                com.observer.fabric.ObserverFabric.LOGGER.info("[Observer] Received MenuOpenPayload for menu: {}", payload.menuId());
                Minecraft.getInstance().setScreen(new ObserverDynamicScreen(payload));
                com.observer.fabric.ObserverFabric.LOGGER.info("[Observer] Successfully set screen to ObserverDynamicScreen.");
            } catch (Exception e) {
                com.observer.fabric.ObserverFabric.LOGGER.error("Failed to open ObserverDynamicScreen", e);
            }
        });
    }
}

package com.observer.fabric.render;

import com.observer.fabric.render.component.ObserverComponent;
import com.observer.fabric.render.manager.ComponentManager;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.DeltaTracker;
import net.minecraft.resources.Identifier;

public class ScreenRenderIntegration implements HudElement {

    private static final Identifier ID = Identifier.fromNamespaceAndPath("observer", "hud_renderer");

    public static void register() {
        HudElementRegistry.addLast(ID, new ScreenRenderIntegration());
        com.observer.fabric.ObserverFabric.LOGGER.info("[Observer] ScreenRenderIntegration registered via HudElementRegistry");
    }

    private int renderTicks = 0;

    @Override
    public void extractRenderState(GuiGraphicsExtractor context, DeltaTracker tickCounter) {
        if (com.observer.fabric.config.ObserverConfig.DEBUG_MODE && renderTicks++ % 60 == 0) {
            com.observer.fabric.ObserverFabric.LOGGER.info("[Observer] Render tick");
            com.observer.fabric.ObserverFabric.LOGGER.info("[Observer] Active components: {}", ComponentManager.getActiveComponents().size());
        }
        for (ObserverComponent component : ComponentManager.getActiveComponents()) {
            component.render(context, tickCounter);
        }
    }
}

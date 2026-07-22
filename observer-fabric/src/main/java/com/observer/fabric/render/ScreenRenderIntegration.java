package com.observer.fabric.render;

import com.observer.fabric.render.component.ObserverComponent;
import com.observer.fabric.render.manager.ComponentManager;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.DeltaTracker;

public class ScreenRenderIntegration implements HudRenderCallback {

    public static void register() {
        HudRenderCallback.EVENT.register(new ScreenRenderIntegration());
        com.observer.fabric.ObserverFabric.LOGGER.info("[Observer] HudRenderCallback registered successfully");
        
        com.observer.fabric.ObserverFabric.LOGGER.info("[Observer-Debug] Validating GuiGraphics environment...");
        com.observer.fabric.ObserverFabric.LOGGER.info("[Observer-Debug] GuiGraphics class: {}", GuiGraphics.class.getName());
        for (java.lang.reflect.Method m : GuiGraphics.class.getMethods()) {
            if (m.getName().toLowerCase().contains("pose")) {
                com.observer.fabric.ObserverFabric.LOGGER.info("[Observer-Debug] Found pose-related method: {} returning {}", m.getName(), m.getReturnType().getName());
            }
        }
        com.observer.fabric.ObserverFabric.LOGGER.info("[Observer-Debug] Render callback source: HudRenderCallback.EVENT");
    }

    private int renderTicks = 0;

    @Override
    public void onHudRender(GuiGraphics drawContext, DeltaTracker tickCounter) {
        if (com.observer.fabric.config.ObserverConfig.DEBUG_MODE && renderTicks++ % 60 == 0) {
            com.observer.fabric.ObserverFabric.LOGGER.info("[Observer] Render tick");
            com.observer.fabric.ObserverFabric.LOGGER.info("[Observer] Active components: {}", ComponentManager.getActiveComponents().size());
        }
        for (ObserverComponent component : ComponentManager.getActiveComponents()) {
            component.render(drawContext, tickCounter);
        }
    }
}

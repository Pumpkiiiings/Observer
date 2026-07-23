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
    private static final java.util.Random SHAKE_RANDOM = new java.util.Random();

    @Override
    public void extractRenderState(GuiGraphicsExtractor context, DeltaTracker tickCounter) {
        // --- Screen Effects ---

        // Screenshake: offset the entire render matrix
        if (com.observer.fabric.screen.ScreenEffectState.screenshakeActive) {
            float intensity = com.observer.fabric.screen.ScreenEffectState.screenshakeIntensity;
            float offsetX = (SHAKE_RANDOM.nextFloat() - 0.5f) * 2f * intensity * 8f;
            float offsetY = (SHAKE_RANDOM.nextFloat() - 0.5f) * 2f * intensity * 8f;
            context.pose().pushMatrix();
            context.pose().translate(offsetX, offsetY);
        }

        // Tint: draw a fullscreen color overlay with fading alpha
        if (com.observer.fabric.screen.ScreenEffectState.tintActive) {
            int r = com.observer.fabric.screen.ScreenEffectState.tintR;
            int g = com.observer.fabric.screen.ScreenEffectState.tintG;
            int b = com.observer.fabric.screen.ScreenEffectState.tintB;
            float baseAlpha = com.observer.fabric.screen.ScreenEffectState.tintAlpha;
            int remaining = com.observer.fabric.screen.ScreenEffectState.tintTicksRemaining;
            int total = com.observer.fabric.screen.ScreenEffectState.tintTotalTicks;

            // Fade out during the last 25% of the duration
            float fadeProgress = total > 0 ? (float) remaining / total : 1f;
            float alpha = fadeProgress < 0.25f ? baseAlpha * (fadeProgress / 0.25f) : baseAlpha;

            int screenWidth = context.guiWidth();
            int screenHeight = context.guiHeight();
            int color = ((int)(alpha * 255) << 24) | (r << 16) | (g << 8) | b;
            context.fill(0, 0, screenWidth, screenHeight, color);
        }

        // --- HUD Components ---
        if (com.observer.fabric.config.ObserverConfig.DEBUG_MODE && renderTicks++ % 60 == 0) {
            com.observer.fabric.ObserverFabric.LOGGER.info("[Observer] Render tick");
            com.observer.fabric.ObserverFabric.LOGGER.info("[Observer] Active components: {}", ComponentManager.getActiveComponents().size());
        }
        for (ObserverComponent component : ComponentManager.getActiveComponents()) {
            component.render(context, tickCounter);
        }

        // Pop the screenshake matrix if it was pushed
        if (com.observer.fabric.screen.ScreenEffectState.screenshakeActive) {
            context.pose().popMatrix();
        }
    }
}

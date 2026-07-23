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
    private static final java.util.Random SHAKE_RANDOM = new java.util.Random();

    @Override
    public void onHudRender(GuiGraphics drawContext, DeltaTracker tickCounter) {
        // --- Screen Effects ---
        com.mojang.blaze3d.vertex.PoseStack poseStack = drawContext.pose();

        // Screenshake is now handled in CameraMixin to shake the actual 3D world instead of the HUD.

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

            int screenWidth = drawContext.guiWidth();
            int screenHeight = drawContext.guiHeight();
            int color = ((int)(alpha * 255) << 24) | (r << 16) | (g << 8) | b;
            drawContext.fill(0, 0, screenWidth, screenHeight, color);
        }

        // Vignette: draw gradients on the edges
        if (com.observer.fabric.screen.ScreenEffectState.vignetteActive) {
            int r = com.observer.fabric.screen.ScreenEffectState.vignetteR;
            int g = com.observer.fabric.screen.ScreenEffectState.vignetteG;
            int b = com.observer.fabric.screen.ScreenEffectState.vignetteB;
            float baseAlpha = com.observer.fabric.screen.ScreenEffectState.vignetteAlpha;
            int remaining = com.observer.fabric.screen.ScreenEffectState.vignetteTicksRemaining;
            int total = com.observer.fabric.screen.ScreenEffectState.vignetteTotalTicks;

            float fadeProgress = total > 0 ? (float) remaining / total : 1f;
            float alpha = fadeProgress < 0.25f ? baseAlpha * (fadeProgress / 0.25f) : baseAlpha;

            int screenWidth = drawContext.guiWidth();
            int screenHeight = drawContext.guiHeight();
            int color = ((int)(alpha * 255) << 24) | (r << 16) | (g << 8) | b;
            int transparent = 0x00000000;

            int thickness = Math.max(10, Math.min(screenWidth, screenHeight) / 3);

            // Top
            drawContext.fillGradient(0, 0, screenWidth, thickness, color, transparent);
            // Bottom
            drawContext.fillGradient(0, screenHeight - thickness, screenWidth, screenHeight, transparent, color);
        }

        // --- HUD Components ---
        if (com.observer.fabric.config.ObserverConfig.DEBUG_MODE && renderTicks++ % 60 == 0) {
            com.observer.fabric.ObserverFabric.LOGGER.info("[Observer] Render tick");
            com.observer.fabric.ObserverFabric.LOGGER.info("[Observer] Active components: {}", ComponentManager.getActiveComponents().size());
        }
        for (ObserverComponent component : ComponentManager.getActiveComponents()) {
            component.render(drawContext, tickCounter);
        }

        // Pop the screenshake matrix if it was pushed
        if (com.observer.fabric.screen.ScreenEffectState.screenshakeActive) {
            poseStack.popPose();
        }
    }
}

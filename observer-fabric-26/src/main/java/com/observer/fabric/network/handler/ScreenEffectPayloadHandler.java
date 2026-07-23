package com.observer.fabric.network.handler;

import com.observer.api.payload.screen.ScreenEffectPayload;
import com.observer.fabric.client.ObserverClient;
import com.observer.fabric.screen.ScreenEffectState;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

/**
 * Handles ScreenEffectPayload packets from the server.
 * Applies screenshake or tint data into ScreenEffectState for the renderer to pick up.
 */
@Environment(EnvType.CLIENT)
public final class ScreenEffectPayloadHandler {

    private ScreenEffectPayloadHandler() {}

    public static void handle(ScreenEffectPayload payload, ClientPlayNetworking.Context context) {
        ObserverClient.LOGGER.info("[Observer-ScreenFX] Received screen effect: " + payload.effectType()
                + " | intensity=" + payload.intensity()
                + " | duration=" + payload.durationTicks()
                + " | rgba=(" + payload.r() + "," + payload.g() + "," + payload.b() + "," + payload.alpha() + ")");

        context.client().execute(() -> {
            switch (payload.effectType()) {
                case SCREENSHAKE -> {
                    ScreenEffectState.screenshakeActive = true;
                    ScreenEffectState.screenshakeIntensity = payload.intensity();
                    ScreenEffectState.screenshakeTicksRemaining = payload.durationTicks();
                    ObserverClient.LOGGER.info("[Observer-ScreenFX] Screenshake activated: intensity="
                            + payload.intensity() + " duration=" + payload.durationTicks() + " ticks");
                }
                case TINT -> {
                    ScreenEffectState.tintActive = true;
                    ScreenEffectState.tintR = payload.r();
                    ScreenEffectState.tintG = payload.g();
                    ScreenEffectState.tintB = payload.b();
                    ScreenEffectState.tintAlpha = payload.alpha();
                    ScreenEffectState.tintTicksRemaining = payload.durationTicks();
                    ScreenEffectState.tintTotalTicks = payload.durationTicks();
                    ObserverClient.LOGGER.info("[Observer-ScreenFX] Screen tint activated: rgba=("
                            + payload.r() + "," + payload.g() + "," + payload.b() + "," + payload.alpha()
                            + ") duration=" + payload.durationTicks() + " ticks");
                }
                case VIGNETTE -> {
                    ScreenEffectState.vignetteActive = true;
                    ScreenEffectState.vignetteR = payload.r();
                    ScreenEffectState.vignetteG = payload.g();
                    ScreenEffectState.vignetteB = payload.b();
                    ScreenEffectState.vignetteAlpha = payload.alpha();
                    ScreenEffectState.vignetteTicksRemaining = payload.durationTicks();
                    ScreenEffectState.vignetteTotalTicks = payload.durationTicks();
                    ObserverClient.LOGGER.info("[Observer-ScreenFX] Vignette activated: rgba=("
                            + payload.r() + "," + payload.g() + "," + payload.b() + "," + payload.alpha()
                            + ") duration=" + payload.durationTicks() + " ticks");
                }
            }
        });
    }
}

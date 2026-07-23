package com.observer.fabric.network.handler;

import com.observer.api.payload.environment.EnvironmentUpdatePayload;
import com.observer.fabric.client.ObserverClient;
import com.observer.fabric.environment.EnvironmentState;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

/**
 * Handles EnvironmentUpdatePayload packets from the server.
 *
 * Pipeline stages logged here:
 *   [STAGE-3] Environment received  — packet arrived on the client render thread queue.
 *   [STAGE-4] Environment activated — EnvironmentState written, ready for renderer.
 */
@Environment(EnvType.CLIENT)
public final class EnvironmentPayloadHandler {

    private EnvironmentPayloadHandler() {}

    public static void handle(EnvironmentUpdatePayload payload, ClientPlayNetworking.Context context) {
        // STAGE-3 — packet received, about to schedule on render thread
        ObserverClient.LOGGER.info(
                "[STAGE-3][ENV-RECEIVED] EnvironmentUpdatePayload received on network thread"
                + " | updateType=" + payload.updateType()
                + " | rgb=(" + payload.r() + "," + payload.g() + "," + payload.b() + ")"
                + " | enabled=" + payload.enabled()
                + " | fogStart=" + payload.fogStart()
                + " | fogEnd=" + payload.fogEnd()
                + " | alpha=" + payload.alpha());

        context.client().execute(() -> {
            // STAGE-4 — now on render thread, applying to EnvironmentState
            ObserverClient.LOGGER.info(
                    "[STAGE-4][ENV-ACTIVATED] Applying to EnvironmentState on render thread"
                    + " | updateType=" + payload.updateType());

            switch (payload.updateType()) {
                case SET_FOG_COLOR -> {
                    EnvironmentState.hasFogOverride = true;
                    EnvironmentState.fogR = payload.r();
                    EnvironmentState.fogG = payload.g();
                    EnvironmentState.fogB = payload.b();
                    ObserverClient.LOGGER.info(
                            "[STAGE-4][ENV-ACTIVATED] SET_FOG_COLOR applied"
                            + " | hasFogOverride=true"
                            + " | fogRGB=(" + EnvironmentState.fogR + "," + EnvironmentState.fogG + "," + EnvironmentState.fogB + ")");
                }
                case RESET_FOG_COLOR -> {
                    EnvironmentState.hasFogOverride = false;
                    ObserverClient.LOGGER.info("[STAGE-4][ENV-ACTIVATED] RESET_FOG_COLOR applied | hasFogOverride=false");
                }
                case SET_SKY_COLOR -> {
                    EnvironmentState.hasSkyOverride = true;
                    EnvironmentState.skyR = payload.r();
                    EnvironmentState.skyG = payload.g();
                    EnvironmentState.skyB = payload.b();
                    ObserverClient.LOGGER.info(
                            "[STAGE-4][ENV-ACTIVATED] SET_SKY_COLOR applied"
                            + " | hasSkyOverride=true"
                            + " | skyRGB=(" + EnvironmentState.skyR + "," + EnvironmentState.skyG + "," + EnvironmentState.skyB + ")");
                }
                case RESET_SKY_COLOR -> {
                    EnvironmentState.hasSkyOverride = false;
                    ObserverClient.LOGGER.info("[STAGE-4][ENV-ACTIVATED] RESET_SKY_COLOR applied | hasSkyOverride=false");
                }
                case SET_MOON_COLOR -> {
                    EnvironmentState.hasMoonOverride = true;
                    EnvironmentState.moonR = payload.r();
                    EnvironmentState.moonG = payload.g();
                    EnvironmentState.moonB = payload.b();
                    ObserverClient.LOGGER.info(
                            "[STAGE-4][ENV-ACTIVATED] SET_MOON_COLOR applied"
                            + " | hasMoonOverride=true"
                            + " | moonRGB=(" + EnvironmentState.moonR + "," + EnvironmentState.moonG + "," + EnvironmentState.moonB + ")");
                }
                case RESET_MOON_COLOR -> {
                    EnvironmentState.hasMoonOverride = false;
                    ObserverClient.LOGGER.info("[STAGE-4][ENV-ACTIVATED] RESET_MOON_COLOR applied | hasMoonOverride=false");
                }
                case SET_TRUE_DARKNESS -> {
                    EnvironmentState.trueDarknessEnabled = payload.enabled();
                    ObserverClient.LOGGER.info(
                            "[STAGE-4][ENV-ACTIVATED] SET_TRUE_DARKNESS applied"
                            + " | trueDarknessEnabled=" + EnvironmentState.trueDarknessEnabled);
                }
                case START_DENSE_FOG -> {
                    EnvironmentState.denseFogEnabled = true;
                    EnvironmentState.fogStart = payload.fogStart();
                    EnvironmentState.fogEnd = payload.fogEnd();
                    EnvironmentState.fogAlpha = payload.alpha();
                    ObserverClient.LOGGER.info(
                            "[STAGE-4][ENV-ACTIVATED] START_DENSE_FOG applied"
                            + " | denseFogEnabled=true"
                            + " | fogStart=" + EnvironmentState.fogStart
                            + " | fogEnd=" + EnvironmentState.fogEnd
                            + " | fogAlpha=" + EnvironmentState.fogAlpha);
                }
                case STOP_DENSE_FOG -> {
                    EnvironmentState.denseFogEnabled = false;
                    ObserverClient.LOGGER.info("[STAGE-4][ENV-ACTIVATED] STOP_DENSE_FOG applied | denseFogEnabled=false");
                }
            }

            // Full state dump after every activation
            ObserverClient.LOGGER.info(
                    "[STAGE-4][ENV-ACTIVATED] Full EnvironmentState dump"
                    + " | hasFogOverride=" + EnvironmentState.hasFogOverride
                    + " | fogRGB=(" + EnvironmentState.fogR + "," + EnvironmentState.fogG + "," + EnvironmentState.fogB + ")"
                    + " | hasSkyOverride=" + EnvironmentState.hasSkyOverride
                    + " | skyRGB=(" + EnvironmentState.skyR + "," + EnvironmentState.skyG + "," + EnvironmentState.skyB + ")"
                    + " | hasMoonOverride=" + EnvironmentState.hasMoonOverride
                    + " | moonRGB=(" + EnvironmentState.moonR + "," + EnvironmentState.moonG + "," + EnvironmentState.moonB + ")"
                    + " | trueDarknessEnabled=" + EnvironmentState.trueDarknessEnabled
                    + " | denseFogEnabled=" + EnvironmentState.denseFogEnabled
                    + " | fogStart=" + EnvironmentState.fogStart
                    + " | fogEnd=" + EnvironmentState.fogEnd
                    + " | fogAlpha=" + EnvironmentState.fogAlpha);
        });
    }
}

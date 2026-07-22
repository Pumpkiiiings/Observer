package com.observer.paper.environment;

import com.observer.api.payload.environment.EnvironmentUpdatePayload;
import com.observer.api.payload.environment.EnvironmentUpdateType;
import com.observer.paper.api.ObserverAPI;
import org.bukkit.entity.Player;
import java.util.logging.Logger;

/**
 * Manages environment effects for players running the Observer client.
 *
 * Pipeline stages logged here (if debug.environment is true):
 *   [STAGE-1] Environment loaded — a call was made to dispatch an environment update.
 *   [STAGE-2] Environment synced — packet encoded and sent over the plugin channel.
 */
public class ObserverEnvironmentManager {

    private final Logger logger;

    public ObserverEnvironmentManager(Logger logger) {
        this.logger = logger;
    }

    private boolean isDebug() {
        return com.observer.paper.ObserverPaper.getInstance().getConfig().getBoolean("debug.environment", false);
    }

    // -----------------------------------------------------------------------
    // Public API
    // -----------------------------------------------------------------------

    public void setFogColor(Player player, int red, int green, int blue) {
        if (isDebug()) {
            logger.info("[STAGE-1][ENV-LOADED] setFogColor called | player=" + player.getName()
                    + " | rgb=(" + red + "," + green + "," + blue + ")"
                    + " | isObserver=" + ObserverAPI.isObserverPlayer(player));
        }
        sendUpdate(player, new EnvironmentUpdatePayload(
                EnvironmentUpdateType.SET_FOG_COLOR, red, green, blue, false, 0, 0, 0));
    }

    public void resetFogColor(Player player) {
        if (isDebug()) {
            logger.info("[STAGE-1][ENV-LOADED] resetFogColor called | player=" + player.getName()
                    + " | isObserver=" + ObserverAPI.isObserverPlayer(player));
        }
        sendUpdate(player, new EnvironmentUpdatePayload(
                EnvironmentUpdateType.RESET_FOG_COLOR, 0, 0, 0, false, 0, 0, 0));
    }

    public void setSkyColor(Player player, int red, int green, int blue) {
        if (isDebug()) {
            logger.info("[STAGE-1][ENV-LOADED] setSkyColor called | player=" + player.getName()
                    + " | rgb=(" + red + "," + green + "," + blue + ")"
                    + " | isObserver=" + ObserverAPI.isObserverPlayer(player));
        }
        sendUpdate(player, new EnvironmentUpdatePayload(
                EnvironmentUpdateType.SET_SKY_COLOR, red, green, blue, false, 0, 0, 0));
    }

    public void resetSkyColor(Player player) {
        if (isDebug()) {
            logger.info("[STAGE-1][ENV-LOADED] resetSkyColor called | player=" + player.getName()
                    + " | isObserver=" + ObserverAPI.isObserverPlayer(player));
        }
        sendUpdate(player, new EnvironmentUpdatePayload(
                EnvironmentUpdateType.RESET_SKY_COLOR, 0, 0, 0, false, 0, 0, 0));
    }

    public void setMoonColor(Player player, int red, int green, int blue) {
        if (isDebug()) {
            logger.info("[STAGE-1][ENV-LOADED] setMoonColor called | player=" + player.getName()
                    + " | rgb=(" + red + "," + green + "," + blue + ")"
                    + " | isObserver=" + ObserverAPI.isObserverPlayer(player));
        }
        sendUpdate(player, new EnvironmentUpdatePayload(
                EnvironmentUpdateType.SET_MOON_COLOR, red, green, blue, false, 0, 0, 0));
    }

    public void resetMoonColor(Player player) {
        if (isDebug()) {
            logger.info("[STAGE-1][ENV-LOADED] resetMoonColor called | player=" + player.getName()
                    + " | isObserver=" + ObserverAPI.isObserverPlayer(player));
        }
        sendUpdate(player, new EnvironmentUpdatePayload(
                EnvironmentUpdateType.RESET_MOON_COLOR, 0, 0, 0, false, 0, 0, 0));
    }

    public void setTrueDarkness(Player player, boolean enabled) {
        if (isDebug()) {
            logger.info("[STAGE-1][ENV-LOADED] setTrueDarkness called | player=" + player.getName()
                    + " | enabled=" + enabled
                    + " | isObserver=" + ObserverAPI.isObserverPlayer(player));
        }
        sendUpdate(player, new EnvironmentUpdatePayload(
                EnvironmentUpdateType.SET_TRUE_DARKNESS, 0, 0, 0, enabled, 0, 0, 0));
    }

    public void startDenseFog(Player player, float fogStart, float fogEnd, float alpha) {
        if (isDebug()) {
            logger.info("[STAGE-1][ENV-LOADED] startDenseFog called | player=" + player.getName()
                    + " | start=" + fogStart + " end=" + fogEnd + " alpha=" + alpha
                    + " | isObserver=" + ObserverAPI.isObserverPlayer(player));
        }
        sendUpdate(player, new EnvironmentUpdatePayload(
                EnvironmentUpdateType.START_DENSE_FOG, 0, 0, 0, false, fogStart, fogEnd, alpha));
    }

    public void stopDenseFog(Player player) {
        if (isDebug()) {
            logger.info("[STAGE-1][ENV-LOADED] stopDenseFog called | player=" + player.getName()
                    + " | isObserver=" + ObserverAPI.isObserverPlayer(player));
        }
        sendUpdate(player, new EnvironmentUpdatePayload(
                EnvironmentUpdateType.STOP_DENSE_FOG, 0, 0, 0, false, 0, 0, 0));
    }

    // -----------------------------------------------------------------------
    // Internal send helper
    // -----------------------------------------------------------------------

    private void sendUpdate(Player player, EnvironmentUpdatePayload payload) {
        // Stage-1 guard — must be Observer player
        if (!ObserverAPI.isObserverPlayer(player)) {
            // We always log warnings so admins know if something failed silently.
            logger.warning("[STAGE-1][ENV-LOADED] BLOCKED — player '" + player.getName()
                    + "' is not a registered Observer client. Packet will NOT be sent."
                    + " | updateType=" + payload.updateType());
            return;
        }

        String channel = EnvironmentUpdatePayload.TYPE.id().toString();

        if (isDebug()) {
            logger.info("[STAGE-2][ENV-SYNCED] Sending EnvironmentUpdatePayload"
                    + " | player=" + player.getName()
                    + " | updateType=" + payload.updateType()
                    + " | channel=" + channel
                    + " | rgb=(" + payload.r() + "," + payload.g() + "," + payload.b() + ")"
                    + " | enabled=" + payload.enabled()
                    + " | fogStart=" + payload.fogStart()
                    + " | fogEnd=" + payload.fogEnd()
                    + " | alpha=" + payload.alpha());
        }

        try {
            ObserverAPI.send(player, channel, payload, EnvironmentUpdatePayload.CODEC);
            if (isDebug()) {
                logger.info("[STAGE-2][ENV-SYNCED] Packet dispatched successfully"
                        + " | player=" + player.getName()
                        + " | updateType=" + payload.updateType());
            }
        } catch (Exception e) {
            // Always log severe network errors.
            logger.severe("[STAGE-2][ENV-SYNCED] FAILED to send EnvironmentUpdatePayload"
                    + " | player=" + player.getName()
                    + " | updateType=" + payload.updateType()
                    + " | error=" + e.getMessage());
            e.printStackTrace();
        }
    }
}

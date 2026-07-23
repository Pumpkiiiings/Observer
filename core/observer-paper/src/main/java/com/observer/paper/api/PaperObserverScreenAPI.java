package com.observer.paper.api;

import com.observer.api.ObserverChannels;
import com.observer.api.payload.screen.ScreenEffectPayload;
import com.observer.api.payload.screen.ScreenEffectType;
import com.observer.paper.ObserverPaper;
import org.bukkit.entity.Player;

/**
 * API for applying screen effects (screenshake, tint) on players who have the Observer mod.
 *
 * <h3>Usage example:</h3>
 * <pre>
 *   // Shake the player's screen for 20 ticks at intensity 0.5
 *   PaperObserverScreenAPI.playScreenshake(player, 0.5f, 20);
 *
 *   // Apply a red tint for 40 ticks at 60% opacity
 *   PaperObserverScreenAPI.playScreenTint(player, 255, 0, 0, 0.6f, 40);
 * </pre>
 */
public class PaperObserverScreenAPI {

    /**
     * Triggers a screenshake effect on the target player's client.
     *
     * @param player        The player to affect
     * @param intensity     Shake magnitude (0.0 = none, 1.0 = strong, values above 1.0 allowed)
     * @param durationTicks How long the shake lasts in ticks (20 ticks = 1 second)
     */
    public static void playScreenshake(Player player, float intensity, int durationTicks) {
        ScreenEffectPayload payload = new ScreenEffectPayload(
                ScreenEffectType.SCREENSHAKE,
                intensity,
                durationTicks,
                0, 0, 0, 0f
        );
        sendEffect(player, payload);
    }

    /**
     * Applies a color tint/vignette overlay on the target player's screen.
     *
     * @param player        The player to affect
     * @param r             Red component (0-255)
     * @param g             Green component (0-255)
     * @param b             Blue component (0-255)
     * @param alpha         Opacity (0.0 = transparent, 1.0 = fully opaque)
     * @param durationTicks How long the tint lasts in ticks (20 ticks = 1 second)
     */
    public static void playScreenTint(Player player, int r, int g, int b, float alpha, int durationTicks) {
        ScreenEffectPayload payload = new ScreenEffectPayload(
                ScreenEffectType.TINT,
                0f,
                durationTicks,
                r, g, b, alpha
        );
        sendEffect(player, payload);
    }

    /**
     * Applies a vignette overlay on the target player's screen (edges only).
     *
     * @param player        The player to affect
     * @param r             Red component (0-255)
     * @param g             Green component (0-255)
     * @param b             Blue component (0-255)
     * @param alpha         Opacity (0.0 = transparent, 1.0 = fully opaque)
     * @param durationTicks How long the vignette lasts in ticks (20 ticks = 1 second)
     */
    public static void playScreenVignette(Player player, int r, int g, int b, float alpha, int durationTicks) {
        ScreenEffectPayload payload = new ScreenEffectPayload(
                ScreenEffectType.VIGNETTE,
                0f,
                durationTicks,
                r, g, b, alpha
        );
        sendEffect(player, payload);
    }

    private static void sendEffect(Player player, ScreenEffectPayload payload) {
        ObserverPaper plugin = ObserverPaper.getInstance();
        if (plugin == null) return;

        // Only send to players that have the Observer mod
        if (!plugin.getPlayerManager().isObserver(player)) return;

        plugin.getNetworkManager().sendPayload(
                player,
                "observer:screen_effect",
                payload,
                ScreenEffectPayload.CODEC
        );

        if (plugin.getConfig().getBoolean("debug.screen-effects", false)) {
            plugin.getLogger().info("[DEBUG-SCREEN] Sent " + payload.effectType()
                    + " to " + player.getName()
                    + " | intensity=" + payload.intensity()
                    + " | duration=" + payload.durationTicks() + " ticks"
                    + " | rgba=(" + payload.r() + "," + payload.g() + "," + payload.b() + "," + payload.alpha() + ")");
        }
    }
}

package com.observer.paper.api;

import com.observer.api.environment.EnvironmentProfile;
import com.observer.paper.ObserverPaper;
import com.observer.paper.api.events.EnvironmentApplyEvent;
import com.observer.paper.api.events.EnvironmentResetEvent;
import com.observer.paper.environment.ObserverEnvironmentManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PaperObserverEnvironmentAPI {

    /**
     * Sets the sky color for the player.
     * @param player The player
     * @param rgbColor The RGB integer color (e.g., 0xFF0000 for red)
     */
    public static void setSkyColor(Player player, int rgbColor) {
        int r = (rgbColor >> 16) & 0xFF;
        int g = (rgbColor >> 8) & 0xFF;
        int b = rgbColor & 0xFF;
        getManager().setSkyColor(player, r, g, b);
    }

    /**
     * Resets the sky color to default for the player.
     * @param player The player
     */
    public static void resetSkyColor(Player player) {
        getManager().resetSkyColor(player);
    }

    /**
     * Sets the fog color for the player.
     * @param player The player
     * @param rgbColor The RGB integer color
     */
    public static void setFogColor(Player player, int rgbColor) {
        int r = (rgbColor >> 16) & 0xFF;
        int g = (rgbColor >> 8) & 0xFF;
        int b = rgbColor & 0xFF;
        getManager().setFogColor(player, r, g, b);
    }

    /**
     * Resets the fog color to default for the player.
     * @param player The player
     */
    public static void resetFogColor(Player player) {
        getManager().resetFogColor(player);
    }

    /**
     * Sets the fog density for the player.
     * @param player The player
     * @param density The density value (e.g., 0.05)
     */
    public static void setFogDensity(Player player, float density) {
        // ObserverEnvironmentManager in paper uses startDenseFog(player, start, end, alpha)
        // Let's abstract this for the simple API. If density is high, we bring fog start closer.
        // For a simple density API, let's map density to fog start/end.
        // A density of 0.05 could mean fog starts at 10 and ends at 50.
        // density of 0.1 means fog starts at 5 and ends at 20.
        float start = Math.max(1, 1 / density);
        float end = Math.max(start + 5, 5 / density);
        getManager().startDenseFog(player, start, end, 1.0f);
    }

    /**
     * Resets the fog density to default for the player.
     * @param player The player
     */
    public static void resetFogDensity(Player player) {
        getManager().stopDenseFog(player);
    }

    /**
     * Enables or disables true darkness for the player.
     * @param player The player
     * @param enabled True to enable true darkness, false to disable
     */
    public static void setTrueDarkness(Player player, boolean enabled) {
        getManager().setTrueDarkness(player, enabled);
    }

    /**
     * Resets the entire environment to default for the player.
     * @param player The player
     */
    public static void resetEnvironment(Player player) {
        EnvironmentResetEvent event = new EnvironmentResetEvent(player);
        Bukkit.getPluginManager().callEvent(event);

        ObserverEnvironmentManager mgr = getManager();
        mgr.resetSkyColor(player);
        mgr.resetFogColor(player);
        mgr.resetMoonColor(player);
        mgr.stopDenseFog(player);
        mgr.setTrueDarkness(player, false);
    }

    /**
     * Applies a predefined environment profile to the player.
     * @param player The player
     * @param profile The environment profile to apply
     */
    public static void applyProfile(Player player, EnvironmentProfile profile) {
        EnvironmentApplyEvent event = new EnvironmentApplyEvent(player, profile);
        Bukkit.getPluginManager().callEvent(event);

        if (profile.getSkyColor() != null) setSkyColor(player, profile.getSkyColor());
        if (profile.getFogColor() != null) setFogColor(player, profile.getFogColor());
        if (profile.getFogDensity() != null) setFogDensity(player, profile.getFogDensity());
        if (profile.getTrueDarkness() != null) setTrueDarkness(player, profile.getTrueDarkness());
    }

    private static ObserverEnvironmentManager getManager() {
        return ObserverPaper.getInstance().getEnvironmentManager();
    }
}

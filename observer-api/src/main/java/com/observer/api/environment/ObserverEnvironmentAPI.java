package com.observer.api.environment;

import java.util.UUID;

public interface ObserverEnvironmentAPI {

    /**
     * Sets the sky color for the player.
     * @param playerId The UUID of the player
     * @param rgbColor The RGB integer color
     */
    void setSkyColor(UUID playerId, int rgbColor);

    /**
     * Resets the sky color to default for the player.
     * @param playerId The UUID of the player
     */
    void resetSkyColor(UUID playerId);

    /**
     * Sets the fog color for the player.
     * @param playerId The UUID of the player
     * @param rgbColor The RGB integer color
     */
    void setFogColor(UUID playerId, int rgbColor);

    /**
     * Resets the fog color to default for the player.
     * @param playerId The UUID of the player
     */
    void resetFogColor(UUID playerId);

    /**
     * Sets the fog density for the player.
     * @param playerId The UUID of the player
     * @param density The density value (e.g., 0.05)
     */
    void setFogDensity(UUID playerId, float density);

    /**
     * Resets the fog density to default for the player.
     * @param playerId The UUID of the player
     */
    void resetFogDensity(UUID playerId);

    /**
     * Enables or disables true darkness for the player.
     * @param playerId The UUID of the player
     * @param enabled True to enable true darkness, false to disable
     */
    void setTrueDarkness(UUID playerId, boolean enabled);

    /**
     * Resets the entire environment to default for the player.
     * @param playerId The UUID of the player
     */
    void resetEnvironment(UUID playerId);

    /**
     * Applies a predefined environment profile to the player.
     * @param playerId The UUID of the player
     * @param profile The environment profile to apply
     */
    void applyProfile(UUID playerId, EnvironmentProfile profile);
}

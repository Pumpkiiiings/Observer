package com.observer.api.sound;

import java.util.UUID;

public interface ObserverSoundAPI {

    /**
     * Plays a spatial sound for the specified player.
     * @param playerId The UUID of the player
     * @param sound The sound definition to play
     */
    void playSound(UUID playerId, SoundDefinition sound);

    /**
     * Stops a specific sound currently playing for the player.
     * @param playerId The UUID of the player
     * @param soundId The ID of the sound to stop
     */
    void stopSound(UUID playerId, String soundId);

    /**
     * Stops all custom spatial sounds playing for the player.
     * @param playerId The UUID of the player
     */
    void stopAllSounds(UUID playerId);

}

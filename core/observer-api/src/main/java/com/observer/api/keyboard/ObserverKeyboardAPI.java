package com.observer.api.keyboard;

import java.util.Set;
import java.util.UUID;

public interface ObserverKeyboardAPI {

    /**
     * Checks if a player is currently holding down a specific ASCII key.
     * @param playerId The UUID of the player
     * @param ascii The ASCII byte code of the key
     * @return True if the key is pressed, false otherwise
     */
    boolean isKeyDown(UUID playerId, int ascii);

    /**
     * Gets all currently pressed keys for a player.
     * @param playerId The UUID of the player
     * @return Set of ASCII byte codes currently pressed
     */
    Set<Integer> getPressedKeys(UUID playerId);

    /**
     * Flushes the key state for a player, releasing all currently held keys.
     * @param playerId The UUID of the player
     */
    void flushKeys(UUID playerId);

}

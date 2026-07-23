package com.observer.paper.api;

import com.observer.paper.ObserverPaper;
import org.bukkit.entity.Player;

import java.util.Set;

public class PaperObserverKeyboardAPI {

    /**
     * Checks if a player is currently holding down a specific ASCII key.
     * @param player The player
     * @param asciiKey The ASCII byte code of the key
     * @return True if the key is pressed, false otherwise
     */
    public static boolean isKeyDown(Player player, int asciiKey) {
        int ordinal = getOrdinalFromAscii(asciiKey);
        if (ordinal == -1) return false;
        return ObserverPaper.getInstance().getKeyboardManager().isKeyDown(player.getUniqueId(), (byte) ordinal);
    }

    /**
     * Gets all currently pressed keys for a player.
     * @param player The player
     * @return Set of ASCII byte codes currently pressed
     */
    public static Set<Integer> getPressedKeys(Player player) {
        Set<Byte> ordinals = ObserverPaper.getInstance().getKeyboardManager().getPressedKeys(player.getUniqueId());
        Set<Integer> asciis = new java.util.HashSet<>();
        com.observer.api.model.ObserverKey[] keys = com.observer.api.model.ObserverKey.values();
        for (Byte b : ordinals) {
            if (b >= 0 && b < keys.length) {
                com.observer.api.model.ObserverKey key = keys[b];
                int ascii = getAsciiFromObserverKey(key);
                if (ascii != -1) asciis.add(ascii);
            }
        }
        return asciis;
    }

    /**
     * Flushes the key state for a player, releasing all currently held keys.
     * @param player The player
     */
    public static void flushKeys(Player player) {
        ObserverPaper.getInstance().getKeyboardManager().flush(player.getUniqueId());
    }

    private static int getOrdinalFromAscii(int ascii) {
        if (ascii >= 'A' && ascii <= 'Z') {
            return ascii - 'A';
        } else if (ascii >= 'a' && ascii <= 'z') {
            return ascii - 'a';
        } else if (ascii >= '0' && ascii <= '9') {
            return (ascii - '0') + 26; // NUM_0 is ordinal 26
        }
        // Fallback for others if needed
        return -1;
    }

    private static int getAsciiFromObserverKey(com.observer.api.model.ObserverKey key) {
        if (key.ordinal() >= 0 && key.ordinal() <= 25) {
            return 'A' + key.ordinal();
        } else if (key.ordinal() >= 26 && key.ordinal() <= 35) {
            return '0' + (key.ordinal() - 26);
        }
        return -1;
    }
}

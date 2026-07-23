package com.observer.paper.audio;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.entity.Player;
import java.util.logging.Logger;

/**
 * Manages audio effects for players.
 * Modeled after typical IAudioManager architectures to provide centralized
 * error logging and validation for sound identifiers.
 */
public class ObserverSoundManager {

    private final Logger logger;
    private int totalSoundsSent = 0;
    private String lastPlayedSound = "None";

    public ObserverSoundManager(Logger logger) {
        this.logger = logger;
    }

    public void playSound(Player player, String soundId) {
        playSound(player, soundId, 1.0f, 1.0f);
    }

    public void playSound(Player player, String soundId, float volume, float pitch) {
        try {
            // Validate the namespace (Adventure Key requires a namespace, usually "minecraft")
            String[] parts = soundId.split(":");
            if (parts.length != 2) {
                logger.warning("[Observer] Invalid sound identifier (must contain ':'): " + soundId);
                return;
            }

            Key key = Key.key(parts[0], parts[1]);
            Sound sound = Sound.sound(key, Sound.Source.MASTER, volume, pitch);

            player.playSound(sound);
            
            this.lastPlayedSound = soundId;
            this.totalSoundsSent++;

        } catch (Exception e) {
            logger.warning("[Observer] Failed to play sound '" + soundId + "' to " + player.getName() + ": " + e.getMessage());
        }
    }

    public int getTotalSoundsSent() {
        return totalSoundsSent;
    }

    public String getLastPlayedSound() {
        return lastPlayedSound;
    }
}

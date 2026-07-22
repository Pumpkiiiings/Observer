package com.observer.paper.api;

import com.observer.api.sound.SoundDefinition;
import com.observer.paper.ObserverPaper;
import com.observer.paper.api.events.ObserverSoundStartEvent;
import com.observer.paper.api.events.ObserverSoundStopEvent;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.sound.SoundStop;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PaperObserverSoundAPI {

    /**
     * Plays a spatial sound for the specified player.
     * @param player The player
     * @param sound The sound definition to play
     */
    public static void playSound(Player player, SoundDefinition sound) {
        ObserverSoundStartEvent event = new ObserverSoundStartEvent(player, sound);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return;

        Key key = Key.key(sound.getNamespace(), sound.getSoundId());
        Sound adventureSound = Sound.sound(key, Sound.Source.MASTER, sound.getVolume(), sound.getPitch());
        
        if (sound.getX() != 0 || sound.getY() != 0 || sound.getZ() != 0) {
            player.playSound(adventureSound, sound.getX(), sound.getY(), sound.getZ());
        } else {
            player.playSound(adventureSound);
        }
    }

    /**
     * Stops a specific sound currently playing for the player.
     * @param player The player
     * @param soundId The ID of the sound to stop (e.g., "minecraft:entity.generic.explode")
     */
    public static void stopSound(Player player, String soundId) {
        ObserverSoundStopEvent event = new ObserverSoundStopEvent(player, soundId);
        Bukkit.getPluginManager().callEvent(event);

        String namespace = "minecraft";
        String id = soundId;
        if (soundId.contains(":")) {
            String[] parts = soundId.split(":", 2);
            namespace = parts[0];
            id = parts[1];
        }
        
        player.stopSound(SoundStop.named(Key.key(namespace, id)));
    }

    /**
     * Stops all custom spatial sounds playing for the player.
     * @param player The player
     */
    public static void stopAllSounds(Player player) {
        player.stopSound(SoundStop.all());
    }
}

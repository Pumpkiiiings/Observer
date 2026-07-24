package com.observer.paper.api;

import com.observer.paper.ObserverPaper;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class PaperObserverAnimationAPI {

    /**
     * Broadcasts an animation to all nearby players in the same world.
     * The animation will be played on the target player's model.
     * 
     * @param targetPlayer The player that will perform the animation.
     * @param animationName The name of the animation to play (e.g., "mistaken:heavy_attack").
     */
    public static void playAnimation(Player targetPlayer, String animationName) {
        ObserverPaper plugin = JavaPlugin.getPlugin(ObserverPaper.class);
        plugin.getNetworkManager().broadcastAnimation(targetPlayer, animationName);
    }

    /**
     * Broadcasts a stop animation signal to all nearby players.
     * The animation will be stopped on the target player's model.
     *
     * @param targetPlayer The player that will stop the animation.
     */
    public static void stopAnimation(Player targetPlayer) {
        ObserverPaper plugin = JavaPlugin.getPlugin(ObserverPaper.class);
        plugin.getNetworkManager().broadcastAnimation(targetPlayer, "stop");
    }
}

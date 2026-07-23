package com.observer.example;

import com.observer.api.environment.EnvironmentProfile;
import com.observer.api.sound.SoundDefinition;
import com.observer.paper.api.PaperObserverEnvironmentAPI;
import com.observer.paper.api.PaperObserverKeyboardAPI;
import com.observer.paper.api.PaperObserverSoundAPI;
import com.observer.paper.api.events.PlayerKeyPressEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class ObserverExamplePlugin extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("ObserverExample plugin enabled!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("scareme") && sender instanceof Player player) {
            
            // Apply nightmare profile
            PaperObserverEnvironmentAPI.applyProfile(player, EnvironmentProfile.NIGHTMARE);

            // Play scream sound
            SoundDefinition scream = SoundDefinition.builder()
                .sound("minecraft:entity.enderman.scream")
                .position(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ())
                .volume(3.0f)
                .pitch(0.5f)
                .build();
            PaperObserverSoundAPI.playSound(player, scream);
            
            player.sendMessage("§cYou feel a chill down your spine...");

            // Reset after 3 seconds
            Bukkit.getScheduler().runTaskLater(this, () -> {
                PaperObserverEnvironmentAPI.resetEnvironment(player);
                player.sendMessage("§aThe air clears up.");
            }, 60L);

            return true;
        }
        return false;
    }

    @EventHandler
    public void onKeyPress(PlayerKeyPressEvent event) {
        Player player = event.getPlayer();
        // F is ASCII 70
        if (event.getAsciiKey() == 'F') {
            player.sendMessage("§a[ObserverExample] You paid respects (Pressed F).");
        }
    }
}

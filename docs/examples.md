# Observer API Examples

This document provides complete, working examples of how to integrate the Observer API into your Bukkit/Paper plugins.

## Example 1: The "Jump Scare" (Sound & Environment)

This example listens for a player stepping on a specific block and triggers a jump scare using the Sound and Environment APIs.

```java
import com.observer.paper.api.PaperObserverEnvironmentAPI;
import com.observer.paper.api.PaperObserverSoundAPI;
import com.observer.api.sound.SoundDefinition;
import com.observer.api.environment.EnvironmentProfile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class JumpScareListener implements Listener {

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (event.getTo().getBlock().getType() == org.bukkit.Material.REDSTONE_BLOCK) {
            
            // 1. Instantly pitch the world to black
            PaperObserverEnvironmentAPI.applyProfile(event.getPlayer(), EnvironmentProfile.NIGHTMARE);

            // 2. Play a spatial scream exactly where they stepped
            SoundDefinition scream = SoundDefinition.builder()
                .sound("minecraft:entity.enderman.scream")
                .position(event.getTo().getX(), event.getTo().getY(), event.getTo().getZ())
                .volume(3.0f)
                .pitch(0.5f)
                .build();
            
            PaperObserverSoundAPI.playSound(event.getPlayer(), scream);

            // 3. Reset after 3 seconds
            org.bukkit.Bukkit.getScheduler().runTaskLater(myPlugin, () -> {
                PaperObserverEnvironmentAPI.resetEnvironment(event.getPlayer());
            }, 60L);
        }
    }
}
```

## Example 2: Interactive Cutscene Prompts (Keyboard)

This example forces a player to press 'F' to pay respects during a cutscene.

```java
import com.observer.paper.api.PaperObserverKeyboardAPI;
import com.observer.paper.api.events.PlayerKeyPressEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class CutsceneListener implements Listener {

    @EventHandler
    public void onKeyPress(PlayerKeyPressEvent event) {
        // 'F' is ASCII 70
        if (event.getAsciiKey() == 'F') {
            event.getPlayer().sendMessage("Respects paid.");
            // Proceed to next stage of cutscene
        }
    }

    // You can also check state manually in a runnable
    public void tickCutscene(org.bukkit.entity.Player player) {
        if (PaperObserverKeyboardAPI.isKeyDown(player, 'SPACE')) { // ASCII 32
            player.sendMessage("You skipped the cutscene.");
        }
    }
}
```

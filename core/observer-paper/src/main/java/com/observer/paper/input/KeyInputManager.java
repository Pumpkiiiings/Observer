package com.observer.paper.input;

import com.observer.api.ObserverChannels;
import com.observer.api.payload.event.KeyEventPayload;
import com.observer.paper.api.event.ObserverKeyEvent;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

/**
 * Listens for key events coming from the Fabric client and dispatches Bukkit events.
 */
public class KeyInputManager {

    private final JavaPlugin plugin;
    private final Logger logger;

    public KeyInputManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
    }

    public void register() {
        Bukkit.getMessenger().registerIncomingPluginChannel(
                plugin,
                ObserverChannels.channel(ObserverChannels.OBSERVER_EVENT),
                (channel, player, message) -> {
                    if (!channel.equals(ObserverChannels.channel(ObserverChannels.OBSERVER_EVENT))) return;

                    try {
                        // Decode the payload
                        ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
                        RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(
                                io.netty.buffer.Unpooled.wrappedBuffer(message),
                                serverPlayer.registryAccess()
                        );

                        KeyEventPayload payload = KeyEventPayload.CODEC.decode(buf);

                        // Fire Bukkit Event
                        ObserverKeyEvent event = new ObserverKeyEvent(
                                player,
                                payload.key(),
                                payload.action(),
                                payload.timestamp(),
                                payload.shiftDown(),
                                payload.ctrlDown(),
                                payload.altDown()
                        );
                        
                        // Update KeyInputManager / ObserverKeyboardManager
                        byte keyCode = (byte) payload.key().ordinal();
                        java.util.Set<Byte> currentKeys = new java.util.HashSet<>(com.observer.paper.ObserverPaper.getInstance().getKeyboardManager().getPressedKeys(player.getUniqueId()));
                        
                        // Fire API events and update keys
                        int asciiCode = -1;
                        if (payload.key().ordinal() >= 0 && payload.key().ordinal() <= 25) {
                            asciiCode = 'A' + payload.key().ordinal();
                        } else if (payload.key().ordinal() >= 26 && payload.key().ordinal() <= 35) {
                            asciiCode = '0' + (payload.key().ordinal() - 26);
                        }
                        
                        if (payload.action() == com.observer.api.model.KeyAction.PRESS) {
                            currentKeys.add(keyCode);
                            if (asciiCode != -1) {
                                Bukkit.getPluginManager().callEvent(new com.observer.paper.api.events.PlayerKeyPressEvent(player, asciiCode));
                            }
                        } else if (payload.action() == com.observer.api.model.KeyAction.RELEASE) {
                            currentKeys.remove(keyCode);
                            if (asciiCode != -1) {
                                Bukkit.getPluginManager().callEvent(new com.observer.paper.api.events.PlayerKeyReleaseEvent(player, asciiCode));
                            }
                        }
                        com.observer.paper.ObserverPaper.getInstance().getKeyboardManager().updateKeys(player, currentKeys);

                        logger.fine("PrimaryThread=" + Bukkit.isPrimaryThread() + " | Thread=" + Thread.currentThread().getName());
                        Bukkit.getPluginManager().callEvent(event);

                        // Debug logging
                        if (com.observer.paper.ObserverPaper.getInstance().getConfig().getBoolean("debug.keyboard", false)) {
                            logger.info("[Observer Keys Debug] Received granular key event from " + player.getName() + ": " + payload.key() + " " + payload.action());
                        }

                    } catch (Exception e) {
                        logger.warning("Failed to decode or process KeyEventPayload from " + player.getName() + ": " + e.getMessage());
                        // Invalid payloads are ignored, not crashing the player
                    }
                }
        );
    }
}

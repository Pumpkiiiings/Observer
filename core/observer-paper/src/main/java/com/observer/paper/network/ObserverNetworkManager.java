package com.observer.paper.network;

import com.observer.api.ObserverChannels;
import com.observer.api.ObserverProtocol;
import com.observer.api.payload.HandshakePayload;
import com.observer.paper.ObserverPaper;
import com.observer.paper.ObserverPlayer;
import com.observer.paper.ObserverPlayerManager;
import com.observer.paper.api.event.ObserverMenuActionEvent;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.MinecraftServer;

import net.minecraft.core.RegistryAccess;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.EnumSet;

public class ObserverNetworkManager implements PluginMessageListener {
    private final ObserverPaper plugin;
    private final ObserverPlayerManager playerManager;

    public ObserverNetworkManager(ObserverPaper plugin, ObserverPlayerManager playerManager) {
        this.plugin = plugin;
        this.playerManager = playerManager;

        Bukkit.getMessenger().registerOutgoingPluginChannel(plugin, ObserverChannels.channel(ObserverChannels.HANDSHAKE));
        Bukkit.getMessenger().registerOutgoingPluginChannel(plugin, ObserverChannels.channel(ObserverChannels.COMPONENT_CREATE));
        Bukkit.getMessenger().registerOutgoingPluginChannel(plugin, ObserverChannels.channel(ObserverChannels.COMPONENT_REMOVE));
        Bukkit.getMessenger().registerOutgoingPluginChannel(plugin, ObserverChannels.channel(ObserverChannels.UPDATE_TEXT_CONTENT));
        Bukkit.getMessenger().registerOutgoingPluginChannel(plugin, ObserverChannels.channel(ObserverChannels.UPDATE_POSITION));
        Bukkit.getMessenger().registerOutgoingPluginChannel(plugin, ObserverChannels.channel(ObserverChannels.ENVIRONMENT_UPDATE));
        Bukkit.getMessenger().registerOutgoingPluginChannel(plugin, ObserverChannels.channel(ObserverChannels.CLEAR_HUD));
        Bukkit.getMessenger().registerOutgoingPluginChannel(plugin, ObserverChannels.channel(ObserverChannels.MENU_OPEN));
        Bukkit.getMessenger().registerOutgoingPluginChannel(plugin, ObserverChannels.channel(ObserverChannels.OBSERVER_KEYS_SYNC));
        Bukkit.getMessenger().registerOutgoingPluginChannel(plugin, ObserverChannels.channel(ObserverChannels.SCREEN_EFFECT));
        Bukkit.getMessenger().registerOutgoingPluginChannel(plugin, ObserverChannels.channel(ObserverChannels.SYNC_ANIMATIONS));
        Bukkit.getMessenger().registerOutgoingPluginChannel(plugin, ObserverChannels.channel(ObserverChannels.PLAY_ANIMATION));

        Bukkit.getMessenger().registerIncomingPluginChannel(plugin, ObserverChannels.channel(ObserverChannels.HANDSHAKE), this);
        Bukkit.getMessenger().registerIncomingPluginChannel(plugin, ObserverChannels.channel(ObserverChannels.MENU_ACTION), this);
        Bukkit.getMessenger().registerIncomingPluginChannel(plugin, ObserverChannels.channel(ObserverChannels.MENU_CLOSE), this);

        Bukkit.getMessenger().registerIncomingPluginChannel(plugin, ObserverChannels.channel(ObserverChannels.OBSERVER_KEYS_UPDATE), this);
        Bukkit.getMessenger().registerIncomingPluginChannel(plugin, ObserverChannels.channel(ObserverChannels.PLAYER_ACTION), this);
    }

    public void sendHandshakeRequest(Player player) {
        HandshakePayload request = new HandshakePayload(
                ObserverProtocol.VERSION,
                ObserverProtocol.OBSERVER_VERSION,
                EnumSet.noneOf(com.observer.api.ObserverFeature.class)
        );
        sendPayload(player, ObserverChannels.channel(ObserverChannels.HANDSHAKE), request, HandshakePayload.CODEC);
    }

    public <T extends CustomPacketPayload> void sendPayload(Player player, String channel, T payload, StreamCodec<? super RegistryFriendlyByteBuf, T> codec) {
        try {
            ByteBuf buf = Unpooled.buffer();
            
            MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
            RegistryAccess registryAccess = server.registryAccess();
            RegistryFriendlyByteBuf registryBuf = new RegistryFriendlyByteBuf(buf, registryAccess);
            
            codec.encode(registryBuf, payload);
            
            byte[] bytes = new byte[buf.readableBytes()];
            buf.readBytes(bytes);
            
            if (plugin.getConfig().getBoolean("debug.network", false)) {
                plugin.getLogger().info("[DEBUG-SERVER] Sending " + bytes.length + " bytes on channel " + channel + " to " + player.getName());
            }
            player.sendPluginMessage(plugin, channel, bytes);
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to send payload on channel " + channel);
            e.printStackTrace();
        }
    }

    public void broadcastAnimation(Player targetPlayer, String animationName) {
        if (targetPlayer == null || animationName == null || animationName.isEmpty()) return;

        com.observer.api.payload.action.PlayAnimationPayload payload = new com.observer.api.payload.action.PlayAnimationPayload(
                targetPlayer.getUniqueId(),
                animationName
        );

        MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
        RegistryAccess registryAccess = server.registryAccess();
        ByteBuf buf = Unpooled.buffer();
        RegistryFriendlyByteBuf registryBuf = new RegistryFriendlyByteBuf(buf, registryAccess);
        
        com.observer.api.payload.action.PlayAnimationPayload.CODEC.encode(registryBuf, payload);
        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);

        for (Player p : targetPlayer.getWorld().getPlayers()) {
            if (playerManager.isObserver(p)) {
                p.sendPluginMessage(plugin, ObserverChannels.channel(ObserverChannels.PLAY_ANIMATION), bytes);
            }
        }
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (channel.equals(ObserverChannels.channel(ObserverChannels.HANDSHAKE))) {
            try {
                ByteBuf buf = Unpooled.wrappedBuffer(message);
                MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
                RegistryAccess registryAccess = server.registryAccess();
                RegistryFriendlyByteBuf registryBuf = new RegistryFriendlyByteBuf(buf, registryAccess);
                
                HandshakePayload response = HandshakePayload.CODEC.decode(registryBuf);

                ObserverPlayer observerPlayer = new ObserverPlayer(player, response.protocolVersion(), response.observerVersion(), response.features());

                // --- UPDATER CHECK ---
                if (plugin.getConfig().getBoolean("updater.enabled", true)) {
                    String requiredModVer = plugin.getUpdateChecker().getLatestModVersion();
                    String clientVer = response.observerVersion();
                    if (com.observer.paper.updater.UpdateChecker.isNewerVersion(clientVer, requiredModVer)) {
                        String action = plugin.getConfig().getString("updater.on-outdated-client.action", "MESSAGE").toUpperCase();
                        if (action.equals("KICK")) {
                            String kickMsg = plugin.getConfig().getString("updater.on-outdated-client.kick-message", "<red>Outdated mod!");
                            kickMsg = kickMsg.replace("<version>", requiredModVer);
                            net.kyori.adventure.text.Component comp = net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize(kickMsg);
                            
                            for (String m : plugin.getUpdateChecker().getModMessages()) {
                                comp = comp.append(net.kyori.adventure.text.Component.newline())
                                           .append(net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize(m));
                            }
                            
                            final net.kyori.adventure.text.Component finalComp = comp;
                            Bukkit.getScheduler().runTask(plugin, () -> player.kick(finalComp));
                            return; // Stop handshake process
                        } else if (action.equals("MESSAGE")) {
                            String msg = plugin.getConfig().getString("updater.on-outdated-client.chat-message", "<gold>Outdated mod!");
                            msg = msg.replace("<version>", requiredModVer);
                            net.kyori.adventure.text.Component comp = net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize(msg);
                            player.sendMessage(comp);
                            
                            for (String m : plugin.getUpdateChecker().getModMessages()) {
                                player.sendMessage(net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize(m));
                            }
                            
                            if (plugin.getConfig().getBoolean("updater.on-outdated-client.play-sound", true)) {
                                String sound = plugin.getConfig().getString("updater.on-outdated-client.sound-name", "entity.experience_orb.pickup");
                                Bukkit.getScheduler().runTask(plugin, () -> player.playSound(player.getLocation(), sound, 1f, 1f));
                            }
                        }
                    }
                }
                // ---------------------

                playerManager.addObserverPlayer(player, observerPlayer);
                plugin.getLogger().info("Verified Observer client for player " + player.getName());

                com.observer.api.payload.action.SyncAnimationsPayload syncPayload = new com.observer.api.payload.action.SyncAnimationsPayload(plugin.getAnimationManager().getAnimations());
                sendPayload(player, ObserverChannels.channel(ObserverChannels.SYNC_ANIMATIONS), syncPayload, com.observer.api.payload.action.SyncAnimationsPayload.CODEC);

            } catch (Exception e) {
                plugin.getLogger().warning("Failed to decode handshake from " + player.getName());
                e.printStackTrace();
            }
        } else if (channel.equals(ObserverChannels.channel(ObserverChannels.MENU_ACTION))) {
            try {
                ByteBuf buf = Unpooled.wrappedBuffer(message);
                MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
                RegistryAccess registryAccess = server.registryAccess();
                RegistryFriendlyByteBuf registryBuf = new RegistryFriendlyByteBuf(buf, registryAccess);
                
                com.observer.api.payload.ui.MenuActionPayload payload = com.observer.api.payload.ui.MenuActionPayload.CODEC.decode(registryBuf);
                
                com.observer.paper.api.event.MenuActionEvent event = new com.observer.paper.api.event.MenuActionEvent(
                        player, payload.menuId(), payload.reference()
                );
                Bukkit.getPluginManager().callEvent(event);
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to decode MenuActionPayload from " + player.getName());
                e.printStackTrace();
            }
        } else if (channel.equals(ObserverChannels.channel(ObserverChannels.MENU_CLOSE))) {
            try {
                ByteBuf buf = Unpooled.wrappedBuffer(message);
                MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
                RegistryAccess registryAccess = server.registryAccess();
                RegistryFriendlyByteBuf registryBuf = new RegistryFriendlyByteBuf(buf, registryAccess);
                
                com.observer.api.payload.ui.MenuClosePayload payload = com.observer.api.payload.ui.MenuClosePayload.CODEC.decode(registryBuf);
                
                com.observer.paper.api.event.MenuCloseEvent event = new com.observer.paper.api.event.MenuCloseEvent(
                        player, payload.menuId()
                );
                Bukkit.getPluginManager().callEvent(event);
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to decode MenuClosePayload from " + player.getName());
                e.printStackTrace();
            }
        } else if (channel.equals(ObserverChannels.channel(ObserverChannels.OBSERVER_KEYS_UPDATE))) {
            try {
                ByteBuf buf = Unpooled.wrappedBuffer(message);
                MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
                RegistryAccess registryAccess = server.registryAccess();
                RegistryFriendlyByteBuf registryBuf = new RegistryFriendlyByteBuf(buf, registryAccess);
                
                com.observer.api.payload.keys.KeysUpdatePayload payload = com.observer.api.payload.keys.KeysUpdatePayload.CODEC.decode(registryBuf);
                ObserverPaper.getInstance().getKeyboardManager().updateKeys(player, payload.pressedKeys());
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to decode KeysUpdatePayload from " + player.getName());
                e.printStackTrace();
            }
        } else if (channel.equals(ObserverChannels.channel(ObserverChannels.PLAYER_ACTION))) {
            try {
                ByteBuf buf = Unpooled.wrappedBuffer(message);
                MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
                RegistryAccess registryAccess = server.registryAccess();
                RegistryFriendlyByteBuf registryBuf = new RegistryFriendlyByteBuf(buf, registryAccess);
                
                com.observer.api.payload.action.PlayerActionPayload payload = com.observer.api.payload.action.PlayerActionPayload.CODEC.decode(registryBuf);
                
                // Fire Bukkit event on the main thread
                Bukkit.getScheduler().runTask(plugin, () -> {
                    switch (payload.actionType()) {
                        case JUMP -> {
                            com.observer.paper.api.events.ObserverPlayerJumpEvent event = new com.observer.paper.api.events.ObserverPlayerJumpEvent(player);
                            Bukkit.getPluginManager().callEvent(event);
                        }
                        case START_SPRINTING -> {
                            com.observer.paper.api.events.ObserverPlayerSprintEvent event = new com.observer.paper.api.events.ObserverPlayerSprintEvent(player, true);
                            Bukkit.getPluginManager().callEvent(event);
                        }
                        case STOP_SPRINTING -> {
                            com.observer.paper.api.events.ObserverPlayerSprintEvent event = new com.observer.paper.api.events.ObserverPlayerSprintEvent(player, false);
                            Bukkit.getPluginManager().callEvent(event);
                        }
                        case START_SNEAKING -> {
                            com.observer.paper.api.events.ObserverPlayerSneakEvent event = new com.observer.paper.api.events.ObserverPlayerSneakEvent(player, true);
                            Bukkit.getPluginManager().callEvent(event);
                        }
                        case STOP_SNEAKING -> {
                            com.observer.paper.api.events.ObserverPlayerSneakEvent event = new com.observer.paper.api.events.ObserverPlayerSneakEvent(player, false);
                            Bukkit.getPluginManager().callEvent(event);
                        }
                        case LEFT_CLICK -> {
                            com.observer.paper.api.events.ObserverPlayerLeftClickEvent event = new com.observer.paper.api.events.ObserverPlayerLeftClickEvent(player);
                            Bukkit.getPluginManager().callEvent(event);
                        }
                        case RIGHT_CLICK -> {
                            com.observer.paper.api.events.ObserverPlayerRightClickEvent event = new com.observer.paper.api.events.ObserverPlayerRightClickEvent(player);
                            Bukkit.getPluginManager().callEvent(event);
                        }
                        case START_WALKING -> {
                            com.observer.paper.api.events.ObserverPlayerWalkEvent event = new com.observer.paper.api.events.ObserverPlayerWalkEvent(player, true);
                            Bukkit.getPluginManager().callEvent(event);
                        }
                        case STOP_WALKING -> {
                            com.observer.paper.api.events.ObserverPlayerWalkEvent event = new com.observer.paper.api.events.ObserverPlayerWalkEvent(player, false);
                            Bukkit.getPluginManager().callEvent(event);
                        }
                    }
                });
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to decode PlayerActionPayload from " + player.getName());
                e.printStackTrace();
            }
        }
    }
}

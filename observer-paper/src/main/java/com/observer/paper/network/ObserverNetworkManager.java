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

        Bukkit.getMessenger().registerOutgoingPluginChannel(plugin, ObserverChannels.HANDSHAKE.toString());
        Bukkit.getMessenger().registerOutgoingPluginChannel(plugin, ObserverChannels.COMPONENT_CREATE.toString());
        Bukkit.getMessenger().registerOutgoingPluginChannel(plugin, ObserverChannels.COMPONENT_REMOVE.toString());
        Bukkit.getMessenger().registerOutgoingPluginChannel(plugin, ObserverChannels.UPDATE_TEXT_CONTENT.toString());
        Bukkit.getMessenger().registerOutgoingPluginChannel(plugin, ObserverChannels.UPDATE_POSITION.toString());
        Bukkit.getMessenger().registerOutgoingPluginChannel(plugin, ObserverChannels.ENVIRONMENT_UPDATE.toString());
        Bukkit.getMessenger().registerOutgoingPluginChannel(plugin, ObserverChannels.CLEAR_HUD.toString());
        Bukkit.getMessenger().registerOutgoingPluginChannel(plugin, ObserverChannels.MENU_OPEN.toString());
        Bukkit.getMessenger().registerOutgoingPluginChannel(plugin, ObserverChannels.OBSERVER_KEYS_SYNC.toString());

        Bukkit.getMessenger().registerIncomingPluginChannel(plugin, ObserverChannels.HANDSHAKE.toString(), this);
        Bukkit.getMessenger().registerIncomingPluginChannel(plugin, ObserverChannels.MENU_ACTION.toString(), this);
        Bukkit.getMessenger().registerIncomingPluginChannel(plugin, ObserverChannels.MENU_CLOSE.toString(), this);

        Bukkit.getMessenger().registerIncomingPluginChannel(plugin, ObserverChannels.OBSERVER_KEYS_UPDATE.toString(), this);
    }

    public void sendHandshakeRequest(Player player) {
        HandshakePayload request = new HandshakePayload(
                ObserverProtocol.VERSION,
                ObserverProtocol.OBSERVER_VERSION,
                EnumSet.noneOf(com.observer.api.ObserverFeature.class)
        );
        sendPayload(player, ObserverChannels.HANDSHAKE.toString(), request, HandshakePayload.CODEC);
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
            
            plugin.getLogger().info("[DEBUG-SERVER] Sending " + bytes.length + " bytes on channel " + channel + " to " + player.getName());
            player.sendPluginMessage(plugin, channel, bytes);
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to send payload on channel " + channel);
            e.printStackTrace();
        }
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (channel.equals(ObserverChannels.HANDSHAKE.toString())) {
            try {
                ByteBuf buf = Unpooled.wrappedBuffer(message);
                MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
                RegistryAccess registryAccess = server.registryAccess();
                RegistryFriendlyByteBuf registryBuf = new RegistryFriendlyByteBuf(buf, registryAccess);
                
                HandshakePayload response = HandshakePayload.CODEC.decode(registryBuf);

                ObserverPlayer observerPlayer = new ObserverPlayer(player, response.protocolVersion(), response.observerVersion(), response.features());
                playerManager.addObserverPlayer(player, observerPlayer);
                plugin.getLogger().info("Verified Observer client for player " + player.getName());

            } catch (Exception e) {
                plugin.getLogger().warning("Failed to decode handshake from " + player.getName());
                e.printStackTrace();
            }
        } else if (channel.equals(ObserverChannels.MENU_ACTION.toString())) {
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
        } else if (channel.equals(ObserverChannels.MENU_CLOSE.toString())) {
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
        } else if (channel.equals(ObserverChannels.OBSERVER_KEYS_UPDATE.toString())) {
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
        }
    }
}

package com.observer.fabric;

import com.observer.api.payload.HandshakePayload;
import com.observer.api.payload.component.ComponentCreatePayload;
import com.observer.api.payload.component.ComponentRemovePayload;
import com.observer.api.payload.component.update.UpdatePositionPayload;
import com.observer.api.payload.component.update.UpdateTextContentPayload;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ObserverFabric implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("Observer");

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void registerS2C(Object type, Object codec) {
        try {
            java.lang.reflect.Method m = null;
            try {
                m = PayloadTypeRegistry.class.getMethod("clientboundPlay");
            } catch (NoSuchMethodException e) {
                try {
                    m = PayloadTypeRegistry.class.getMethod("playS2C");
                } catch (NoSuchMethodException e2) {
                    m = PayloadTypeRegistry.class.getMethod("play");
                }
            }
            PayloadTypeRegistry registry = (PayloadTypeRegistry) m.invoke(null);
            registry.register((net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type) type, (net.minecraft.network.codec.StreamCodec) codec);
        } catch (Throwable e) {
            LOGGER.error("[Observer] Failed to register S2C payload", e);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void registerC2S(Object type, Object codec) {
        try {
            java.lang.reflect.Method m = null;
            try {
                m = PayloadTypeRegistry.class.getMethod("serverboundPlay");
            } catch (NoSuchMethodException e) {
                try {
                    m = PayloadTypeRegistry.class.getMethod("playC2S");
                } catch (NoSuchMethodException e2) {
                    m = PayloadTypeRegistry.class.getMethod("play");
                }
            }
            PayloadTypeRegistry registry = (PayloadTypeRegistry) m.invoke(null);
            registry.register((net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type) type, (net.minecraft.network.codec.StreamCodec) codec);
        } catch (Throwable e) {
            LOGGER.error("[Observer] Failed to register C2S payload", e);
        }
    }

    @Override
    public void onInitialize() {
        try {
            LOGGER.info("Registering Observer Payloads...");
            registerS2C(HandshakePayload.TYPE, HandshakePayload.CODEC);
            registerC2S(HandshakePayload.TYPE, HandshakePayload.CODEC);
            
            // Register KeyEventPayload for Client -> Server
            registerC2S(com.observer.api.payload.event.KeyEventPayload.TYPE, com.observer.api.payload.event.KeyEventPayload.CODEC);
            
            registerS2C(ComponentCreatePayload.TYPE, ComponentCreatePayload.CODEC);
            registerS2C(ComponentRemovePayload.TYPE, ComponentRemovePayload.CODEC);
            registerS2C(UpdateTextContentPayload.TYPE, UpdateTextContentPayload.CODEC);
            registerS2C(UpdatePositionPayload.TYPE, UpdatePositionPayload.CODEC);
            registerS2C(com.observer.api.payload.ResourceReloadPayload.TYPE, com.observer.api.payload.ResourceReloadPayload.CODEC);
            registerS2C(com.observer.api.payload.environment.EnvironmentUpdatePayload.TYPE, com.observer.api.payload.environment.EnvironmentUpdatePayload.CODEC);
            registerS2C(com.observer.api.payload.component.ClearHUDPayload.TYPE, com.observer.api.payload.component.ClearHUDPayload.CODEC);

            registerS2C(com.observer.api.payload.ui.MenuOpenPayload.TYPE, com.observer.api.payload.ui.MenuOpenPayload.CODEC);
            registerC2S(com.observer.api.payload.ui.MenuActionPayload.TYPE, com.observer.api.payload.ui.MenuActionPayload.CODEC);
            registerC2S(com.observer.api.payload.ui.MenuClosePayload.TYPE, com.observer.api.payload.ui.MenuClosePayload.CODEC);

            registerC2S(com.observer.api.payload.keys.KeysUpdatePayload.TYPE, com.observer.api.payload.keys.KeysUpdatePayload.CODEC);

            // Screen Effects (Server -> Client)
            registerS2C(com.observer.api.payload.screen.ScreenEffectPayload.TYPE, com.observer.api.payload.screen.ScreenEffectPayload.CODEC);
            
            // Player Actions
            registerC2S(com.observer.api.payload.action.PlayerActionPayload.TYPE, com.observer.api.payload.action.PlayerActionPayload.CODEC);
            registerS2C(com.observer.api.payload.action.PlayAnimationPayload.TYPE, com.observer.api.payload.action.PlayAnimationPayload.CODEC);

            LOGGER.info("[Observer] Payload registration success");
            LOGGER.info("[Observer] Codec registration successful");
        } catch (Throwable t) {
            LOGGER.error("CRITICAL ERROR DURING OBSERVER INIT", t);
        }
    }
}

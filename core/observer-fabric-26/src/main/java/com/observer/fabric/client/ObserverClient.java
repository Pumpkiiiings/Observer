package com.observer.fabric.client;

import com.observer.fabric.client.input.ObserverKeybindings;
import com.observer.fabric.render.ScreenRenderIntegration;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import com.observer.api.payload.HandshakePayload;
import com.observer.api.payload.component.ComponentCreatePayload;
import com.observer.api.payload.component.ComponentRemovePayload;
import com.observer.api.payload.component.update.UpdateTextContentPayload;
import com.observer.api.payload.component.update.UpdatePositionPayload;
import com.observer.fabric.network.handler.HandshakeHandler;
import com.observer.fabric.network.handler.ComponentPayloadHandler;
import com.observer.fabric.network.handler.UiPayloadHandler;
import com.observer.fabric.network.handler.EnvironmentPayloadHandler;

@Environment(EnvType.CLIENT)
public class ObserverClient implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("ObserverClient");

    @Override
    public void onInitializeClient() {
        String[] asciiArt = {
            "§b                                                    ",
            "§b  ▄▄▄▄▄   ▄▄                                        ",
            "§b▄███████▄ ██                                        ",
            "§b███   ███ ████▄ ▄█▀▀▀ ▄█▀█▄ ████▄ ██ ██ ▄█▀█▄ ████▄ ",
            "§b███▄▄▄███ ██ ██ ▀███▄ ██▄█▀ ██ ▀▀ ██▄██ ██▄█▀ ██ ▀▀ ",
            "§b ▀█████▀  ████▀ ▄▄▄█▀ ▀█▄▄▄ ██     ▀█▀  ▀█▄▄▄ ██    ",
            "§b                                                    "
        };
        for (String line : asciiArt) {
            LOGGER.info(line.replace("§b", "\u001B[36m") + "\u001B[0m");
        }
        
        LOGGER.info("Initializing Observer Client...");

        LOGGER.info("Initializing Observer Fabric Client...");
        
        try {
            com.observer.fabric.animation.ObserverAnimationManager.initialize();
        } catch (Throwable t) {
            LOGGER.error("[Observer-FATAL] Crash during ObserverAnimationManager initialization:", t);
            throw new RuntimeException("Observer failed to initialize animation manager", t);
        }

        try {
            LOGGER.info("[Observer-DEBUG] Checking mod compatibility...");
            if (net.fabricmc.loader.api.FabricLoader.getInstance().isModLoaded("sodium")) {
                LOGGER.warn("[Observer-WARN] Sodium is installed! Sodium overrides chunk rendering and fog logic. Fog overrides may be bypassed.");
            }
            if (net.fabricmc.loader.api.FabricLoader.getInstance().isModLoaded("iris")) {
                LOGGER.warn("[Observer-WARN] Iris is installed! Shaders completely replace the rendering pipeline. Environment effects (sky, fog, darkness) WILL be bypassed!");
            }
            if (net.fabricmc.loader.api.FabricLoader.getInstance().isModLoaded("nvidium")) {
                LOGGER.warn("[Observer-WARN] Nvidium is installed! Nvidium modifies vertex rendering and may conflict with specific visual overrides.");
            }
            if (net.fabricmc.loader.api.FabricLoader.getInstance().isModLoaded("immediatelyfast")) {
                LOGGER.warn("[Observer-WARN] ImmediatelyFast is installed! This usually optimizes immediate mode rendering but check for visual conflicts.");
            }
        } catch (Throwable t) {
            LOGGER.error("Failed during compatibility check", t);
        }

        try {
            // Payloads are already registered in ObserverFabric (the common initializer)

            // Register S2C payload receivers
            ClientPlayNetworking.registerGlobalReceiver(HandshakePayload.TYPE, HandshakeHandler::handle);
            ClientPlayNetworking.registerGlobalReceiver(ComponentCreatePayload.TYPE, ComponentPayloadHandler::handleCreate);
            ClientPlayNetworking.registerGlobalReceiver(ComponentRemovePayload.TYPE, ComponentPayloadHandler::handleRemove);
            ClientPlayNetworking.registerGlobalReceiver(UpdateTextContentPayload.TYPE, ComponentPayloadHandler::handleUpdateText);
            ClientPlayNetworking.registerGlobalReceiver(UpdatePositionPayload.TYPE, ComponentPayloadHandler::handleUpdatePosition);
            ClientPlayNetworking.registerGlobalReceiver(com.observer.api.payload.component.ClearHUDPayload.TYPE, ComponentPayloadHandler::handleClearHUD);
            ClientPlayNetworking.registerGlobalReceiver(com.observer.api.payload.ResourceReloadPayload.TYPE, (payload, context) -> {
                context.client().execute(() -> {
                    context.client().reloadResourcePacks();
                });
            });
            ClientPlayNetworking.registerGlobalReceiver(com.observer.api.payload.ui.MenuOpenPayload.TYPE, UiPayloadHandler::handleOpen);
            
            // com.observer.fabric.keys.KeyboardTrackerClient handles key sync manually or we need to put it back
            // I'll comment out keys sync for now if the handler is missing
            // ClientPlayNetworking.registerGlobalReceiver(com.observer.api.payload.keys.KeysSyncPayload.TYPE, KeysPayloadHandler::handleSync);
            
            ClientPlayNetworking.registerGlobalReceiver(com.observer.api.payload.environment.EnvironmentUpdatePayload.TYPE, EnvironmentPayloadHandler::handle);
            
            // Screen Effects
            ClientPlayNetworking.registerGlobalReceiver(com.observer.api.payload.screen.ScreenEffectPayload.TYPE, com.observer.fabric.network.handler.ScreenEffectPayloadHandler::handle);
            
            // Animations
            ClientPlayNetworking.registerGlobalReceiver(com.observer.api.payload.action.PlayAnimationPayload.TYPE, com.observer.fabric.network.handler.PlayAnimationHandler::handle);
            ClientPlayNetworking.registerGlobalReceiver(com.observer.api.payload.action.SyncAnimationsPayload.TYPE, com.observer.fabric.network.handler.SyncAnimationsHandler::handle);
            
            LOGGER.info("PayloadReceiverManager and KeyboardTrackerClient registration complete.");
        } catch (Throwable t) {
            LOGGER.error("[Observer-FATAL] Crash during ObserverClient payload registration:", t);
        }

        // Register client tick for screen effects countdown
        try {
            net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.END_CLIENT_TICK.register(client -> {
                com.observer.fabric.screen.ScreenEffectState.tick();
            });
        } catch (Exception e) {
            LOGGER.error("Failed during ScreenEffectState tick registration", e);
        }

        try {
            LOGGER.info("Starting ScreenRenderIntegration registration (HUD components)...");
            ScreenRenderIntegration.register();
            LOGGER.info("ScreenRenderIntegration registration complete.");
        } catch (Exception e) {
            LOGGER.error("Failed during ScreenRenderIntegration initialization", e);
        }

        try {
            LOGGER.info("Starting ObserverKeybindings registration (Keyboard events)...");
            ObserverKeybindings.register();
            LOGGER.info("ObserverKeybindings registration complete.");
        } catch (Exception e) {
            LOGGER.error("Failed during ObserverKeybindings initialization", e);
        }
    }
}

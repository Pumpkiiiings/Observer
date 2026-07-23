package com.observer.fabric.network.handler;

import com.observer.api.ObserverFeature;
import com.observer.api.ObserverProtocol;
import com.observer.api.payload.HandshakePayload;
import com.observer.fabric.client.ObserverClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

import java.util.EnumSet;

@Environment(EnvType.CLIENT)
public final class HandshakeHandler {

    private HandshakeHandler() {}

    public static void handle(HandshakePayload payload, ClientPlayNetworking.Context context) {
        ObserverClient.LOGGER.info("Observer handshake received. Server Protocol: {}, Server Version: {}", 
                payload.protocolVersion(), payload.observerVersion());

        EnumSet<ObserverFeature> supportedFeatures = EnumSet.of(
            ObserverFeature.TEXT, 
            ObserverFeature.IMAGE, 
            ObserverFeature.PROGRESS_BAR
        );

        HandshakePayload responsePayload = new HandshakePayload(
                ObserverProtocol.VERSION,
                ObserverProtocol.OBSERVER_VERSION,
                supportedFeatures
        );

        context.responseSender().sendPacket(responsePayload);
        ObserverClient.LOGGER.info("Observer handshake response sent.");
    }
}

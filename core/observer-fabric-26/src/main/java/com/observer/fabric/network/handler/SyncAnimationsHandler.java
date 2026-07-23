package com.observer.fabric.network.handler;

import com.observer.api.payload.action.SyncAnimationsPayload;
import com.observer.fabric.animation.ObserverAnimationManager;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class SyncAnimationsHandler {
    public static void handle(SyncAnimationsPayload payload, ClientPlayNetworking.Context context) {
        context.client().execute(() -> {
            ObserverAnimationManager.loadDynamicAnimations(payload.animations());
            com.observer.fabric.client.ObserverClient.LOGGER.info("[Observer] Received and registered " + payload.animations().size() + " server-side animations.");
        });
    }
}

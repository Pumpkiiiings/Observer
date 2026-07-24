package com.observer.fabric.network.handler;

import com.observer.api.payload.action.PlayAnimationPayload;
import com.observer.fabric.animation.ObserverAnimationManager;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

public class PlayAnimationHandler {
    public static void handle(PlayAnimationPayload payload, ClientPlayNetworking.Context context) {
        context.client().execute(() -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.level == null) return;
            
            Player targetPlayer = mc.level.getPlayerByUUID(payload.targetPlayer());
            if (targetPlayer != null) {
                if (payload.animationName().isEmpty() || payload.animationName().equals("stop")) {
                    ObserverAnimationManager.stopAnimation(targetPlayer);
                } else {
                    ObserverAnimationManager.playAnimation(targetPlayer, payload.animationName());
                }
            }
        });
    }
}

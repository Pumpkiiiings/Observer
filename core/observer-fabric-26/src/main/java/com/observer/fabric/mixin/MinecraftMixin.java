package com.observer.fabric.mixin;

import com.observer.api.payload.action.PlayerActionPayload;
import com.observer.api.payload.action.PlayerActionType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Inject(method = "startAttack", at = @At("HEAD"))
    private void onStartAttack(CallbackInfoReturnable<Boolean> cir) {
        if (ClientPlayNetworking.canSend(PlayerActionPayload.TYPE)) {
            ClientPlayNetworking.send(new PlayerActionPayload(PlayerActionType.LEFT_CLICK));
        }
    }

    @Inject(method = "startUseItem", at = @At("HEAD"))
    private void onStartUseItem(CallbackInfo ci) {
        if (ClientPlayNetworking.canSend(PlayerActionPayload.TYPE)) {
            ClientPlayNetworking.send(new PlayerActionPayload(PlayerActionType.RIGHT_CLICK));
        }
    }
}

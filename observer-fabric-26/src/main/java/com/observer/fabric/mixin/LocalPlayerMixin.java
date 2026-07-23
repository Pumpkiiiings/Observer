package com.observer.fabric.mixin;

import com.observer.api.payload.action.PlayerActionPayload;
import com.observer.api.payload.action.PlayerActionType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin {

    @Inject(method = "jumpFromGround", at = @At("HEAD"))
    private void onJump(CallbackInfo ci) {
        if (ClientPlayNetworking.canSend(PlayerActionPayload.TYPE)) {
            ClientPlayNetworking.send(new PlayerActionPayload(PlayerActionType.JUMP));
        }
    }

    @Inject(method = "setSprinting", at = @At("HEAD"))
    private void onSprint(boolean sprinting, CallbackInfo ci) {
        LocalPlayer player = (LocalPlayer) (Object) this;
        if (player.isSprinting() != sprinting) {
            if (ClientPlayNetworking.canSend(PlayerActionPayload.TYPE)) {
                ClientPlayNetworking.send(new PlayerActionPayload(
                        sprinting ? PlayerActionType.START_SPRINTING : PlayerActionType.STOP_SPRINTING
                ));
            }
        }
    }

    @Inject(method = "setShiftKeyDown", at = @At("HEAD"))
    private void onSneak(boolean shiftKeyDown, CallbackInfo ci) {
        LocalPlayer player = (LocalPlayer) (Object) this;
        if (player.isShiftKeyDown() != shiftKeyDown) {
            if (ClientPlayNetworking.canSend(PlayerActionPayload.TYPE)) {
                ClientPlayNetworking.send(new PlayerActionPayload(
                        shiftKeyDown ? PlayerActionType.START_SNEAKING : PlayerActionType.STOP_SNEAKING
                ));
            }
        }
    }
}

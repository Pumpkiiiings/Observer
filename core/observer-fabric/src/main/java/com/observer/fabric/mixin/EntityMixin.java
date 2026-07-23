package com.observer.fabric.mixin;

import com.observer.api.payload.action.PlayerActionPayload;
import com.observer.api.payload.action.PlayerActionType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Inject(method = "setSprinting(Z)V", at = @At("HEAD"))
    private void onSprint(boolean sprinting, CallbackInfo ci) {
        if ((Object) this instanceof LocalPlayer player) {
            if (player.isSprinting() != sprinting) {
                if (ClientPlayNetworking.canSend(PlayerActionPayload.TYPE)) {
                    ClientPlayNetworking.send(new PlayerActionPayload(
                            sprinting ? PlayerActionType.START_SPRINTING : PlayerActionType.STOP_SPRINTING
                    ));
                }
            }
        }
    }

    @Inject(method = "setShiftKeyDown(Z)V", at = @At("HEAD"))
    private void onSneak(boolean shiftKeyDown, CallbackInfo ci) {
        if ((Object) this instanceof LocalPlayer player) {
            if (player.isShiftKeyDown() != shiftKeyDown) {
                if (ClientPlayNetworking.canSend(PlayerActionPayload.TYPE)) {
                    ClientPlayNetworking.send(new PlayerActionPayload(
                            shiftKeyDown ? PlayerActionType.START_SNEAKING : PlayerActionType.STOP_SNEAKING
                    ));
                }
            }
        }
    }
}

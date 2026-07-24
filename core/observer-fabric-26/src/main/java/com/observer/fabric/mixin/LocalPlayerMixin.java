package com.observer.fabric.mixin;

import com.observer.api.payload.action.PlayerActionPayload;
import com.observer.api.payload.action.PlayerActionType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin {

    @Unique
    private boolean observer$wasWalking = false;

    @Inject(method = "tick", at = @At("TAIL"))
    private void onTick(CallbackInfo ci) {
        LocalPlayer player = (LocalPlayer) (Object) this;
        
        boolean isWalking = player.getDeltaMovement().horizontalDistanceSqr() > 0.0001;
        
        if (isWalking != observer$wasWalking) {
            observer$wasWalking = isWalking;
            if (ClientPlayNetworking.canSend(PlayerActionPayload.TYPE)) {
                if (isWalking) {
                    ClientPlayNetworking.send(new PlayerActionPayload(PlayerActionType.START_WALKING));
                } else {
                    ClientPlayNetworking.send(new PlayerActionPayload(PlayerActionType.STOP_WALKING));
                }
            }
        }
    }
}

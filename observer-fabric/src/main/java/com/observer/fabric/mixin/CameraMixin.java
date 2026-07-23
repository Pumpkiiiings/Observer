package com.observer.fabric.mixin;

import com.observer.fabric.screen.ScreenEffectState;
import net.minecraft.client.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraMixin {

    @Shadow protected abstract void setRotation(float yRot, float xRot);

    @Shadow private float yRot;
    @Shadow private float xRot;

    private static final java.util.Random SHAKE_RANDOM = new java.util.Random();

    @Inject(method = "setup", at = @At("RETURN"))
    private void onSetup(net.minecraft.world.level.BlockGetter level, net.minecraft.world.entity.Entity entity, boolean detached, boolean thirdPersonReverse, float partialTick, CallbackInfo ci) {
        if (ScreenEffectState.screenshakeActive) {
            float intensity = ScreenEffectState.screenshakeIntensity;
            float randomYaw = (SHAKE_RANDOM.nextFloat() - 0.5f) * 2f * intensity * 5f;
            float randomPitch = (SHAKE_RANDOM.nextFloat() - 0.5f) * 2f * intensity * 5f;
            this.setRotation(this.yRot + randomYaw, this.xRot + randomPitch);
        }
    }
}

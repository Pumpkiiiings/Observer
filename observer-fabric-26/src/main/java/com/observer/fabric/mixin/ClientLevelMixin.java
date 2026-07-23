package com.observer.fabric.mixin;

import com.observer.fabric.client.ObserverClient;
import com.observer.fabric.environment.EnvironmentState;
import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin for sky color override.
 *
 * Pipeline stage logged here:
 *   [STAGE-5] Sky renderer modified — getSkyColor() return value is being replaced.
 *
 * MC 1.21.10 BREAKING CHANGE:
 *   getSkyColor() return type changed from Vec3 (float r/g/b) to int (packed ARGB).
 *   We now pack EnvironmentState.skyR/G/B into 0xFFRRGGBB format.
 *
 * KNOWN CONFLICTS:
 *   - Iris shaders (active): sky rendering is fully replaced — override has NO effect.
 *   - Iris shaders (disabled): override works.
 *   - Sodium: does NOT affect ClientLevel.getSkyColor() — override works.
 */
@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin {

    @Inject(method = "getSkyColor", at = @At("HEAD"), cancellable = true, require = 0)
    private void onGetSkyColor(CallbackInfoReturnable<Integer> cir) {
        if (EnvironmentState.hasSkyOverride) {
            int r = EnvironmentState.skyR & 0xFF;
            int g = EnvironmentState.skyG & 0xFF;
            int b = EnvironmentState.skyB & 0xFF;
            // MC 1.21.10: getSkyColor() returns packed ARGB int (0xFFRRGGBB)
            int argb = (0xFF << 24) | (r << 16) | (g << 8) | b;
            cir.setReturnValue(argb);
        }
    }
}

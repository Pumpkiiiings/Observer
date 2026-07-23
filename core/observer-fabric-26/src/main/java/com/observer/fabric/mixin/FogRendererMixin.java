package com.observer.fabric.mixin;

import com.observer.fabric.environment.EnvironmentState;
import net.minecraft.client.renderer.fog.FogRenderer;
import net.minecraft.client.renderer.fog.FogData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin for fog color and density override.
 *
 * Pipeline stage logged here:
 *   [STAGE-6] Fog renderer modified — intercepting updateBuffer(FogData).
 *
 * MC 26.1 BREAKING CHANGE:
 *   FogRenderer moved to net.minecraft.client.renderer.fog.FogRenderer.
 *   setupFog now returns FogData, updateBuffer accepts FogData.
 *   We inject at the head of updateBuffer to overwrite color/distances before GPU upload.
 *
 * KNOWN CONFLICTS:
 *   - Iris shaders: Bypass this pipeline entirely.
 *   - Sodium: Partially replaces fog rendering, but usually respects the vanilla UBO.
 */
@Mixin(FogRenderer.class)
public abstract class FogRendererMixin {

    @Inject(
            method = "updateBuffer(Ljava/nio/ByteBuffer;ILorg/joml/Vector4f;FFFFFF)V",
            at = @At("HEAD"),
            require = 0
    )
    private void onUpdateFogBuffer(java.nio.ByteBuffer buffer, int i, org.joml.Vector4f color, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
        if (EnvironmentState.hasFogOverride && color != null) {
            color.set(
                    EnvironmentState.fogR / 255.0f,
                    EnvironmentState.fogG / 255.0f,
                    EnvironmentState.fogB / 255.0f,
                    color.w
            );
        }

        if (EnvironmentState.denseFogEnabled) {
            // Note: FogData fields are inaccessible in this specific signature variant
        }
    }
}

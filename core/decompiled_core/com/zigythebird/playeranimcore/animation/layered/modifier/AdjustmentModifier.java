/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package com.zigythebird.playeranimcore.animation.layered.modifier;

import com.zigythebird.playeranimcore.animation.AnimationController;
import com.zigythebird.playeranimcore.animation.AnimationData;
import com.zigythebird.playeranimcore.animation.layered.modifier.AbstractModifier;
import com.zigythebird.playeranimcore.bones.PlayerAnimBone;
import com.zigythebird.playeranimcore.math.Vec3f;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;

public class AdjustmentModifier
extends AbstractModifier {
    public boolean fadeIn = true;
    public boolean fadeOut = true;
    public boolean enabled = true;
    private AnimationData data;
    protected BiFunction<String, AnimationData, Optional<PartModifier>> source;
    protected int instructedFadeout = 0;
    private int remainingFadeout = 0;

    public AdjustmentModifier(Function<String, Optional<PartModifier>> source) {
        this((String name, AnimationData data) -> (Optional)source.apply((String)name));
    }

    public AdjustmentModifier(BiFunction<String, AnimationData, Optional<PartModifier>> source) {
        this.source = source;
    }

    @Override
    public void tick(AnimationData state) {
        super.tick(state);
        this.data = state;
        if (this.remainingFadeout > 0) {
            --this.remainingFadeout;
            if (this.remainingFadeout <= 0) {
                this.instructedFadeout = 0;
            }
        }
    }

    @Override
    public void setupAnim(AnimationData state) {
        super.setupAnim(state);
        this.data = state;
    }

    public void fadeOut(int fadeOut) {
        this.instructedFadeout = fadeOut;
        this.remainingFadeout = fadeOut + 1;
    }

    protected float getFadeOut(float delta) {
        AnimationController controller;
        AnimationController animationController;
        float fadeOut = 1.0f;
        if (this.remainingFadeout > 0 && this.instructedFadeout > 0) {
            float current = Math.max((float)this.remainingFadeout - delta, 0.0f);
            fadeOut = current / (float)this.instructedFadeout;
            fadeOut = Math.min(fadeOut, 1.0f);
            return fadeOut;
        }
        if (this.fadeOut && (animationController = this.getController()) instanceof AnimationController && (controller = animationController).getCurrentAnimation() != null) {
            float stopTick = controller.getCurrentAnimation().animation().length();
            float endTick = controller.getCurrentAnimation().animation().data().get("endTick").orElse(Float.valueOf(stopTick)).floatValue();
            float position = -1.0f * (controller.getAnimationTicks() - stopTick);
            float length = stopTick - endTick;
            if (length > 0.0f) {
                fadeOut = position / length;
                fadeOut = Math.min(fadeOut, 1.0f);
            }
        }
        return fadeOut;
    }

    protected float getFadeIn() {
        AnimationController controller;
        AnimationController animationController;
        float fadeIn = 1.0f;
        if (this.fadeIn && (animationController = this.getController()) instanceof AnimationController && (controller = animationController).getCurrentAnimation() != null) {
            float beginTick = controller.getCurrentAnimation().animation().data().get("beginTick").orElse(Float.valueOf(0.0f)).floatValue();
            fadeIn = beginTick > 0.0f ? controller.getAnimationTicks() / beginTick : 1.0f;
            fadeIn = Math.min(fadeIn, 1.0f);
        }
        return fadeIn;
    }

    @Override
    public void get3DTransform(@NotNull PlayerAnimBone bone) {
        if (!this.enabled) {
            super.get3DTransform(bone);
            return;
        }
        Optional<PartModifier> partModifier = this.source.apply(bone.getName(), this.data);
        float fade = this.getFadeIn() * this.getFadeOut(this.data.getPartialTick());
        if (partModifier.isPresent()) {
            super.get3DTransform(bone);
            this.transformBone(bone, partModifier.get(), fade);
            return;
        }
        super.get3DTransform(bone);
    }

    protected void transformBone(PlayerAnimBone bone, PartModifier partModifier, float fade) {
        Vec3f pos = partModifier.offset().mul(fade);
        Vec3f rot = partModifier.rotation().mul(fade);
        Vec3f scale = partModifier.scale().mul(fade);
        bone.position.add(pos.x(), pos.y(), pos.z());
        bone.rotation.add(rot.x(), rot.y(), rot.z());
        bone.scale.add(scale.x(), scale.y(), scale.z());
    }

    @Override
    public String toString() {
        return "AdjustmentModifier{anim=" + String.valueOf(this.anim) + ", enabled=" + this.enabled + "}";
    }

    public record PartModifier(Vec3f rotation, Vec3f scale, Vec3f offset) {
        public PartModifier(Vec3f rotation, Vec3f offset) {
            this(rotation, Vec3f.ZERO, offset);
        }
    }
}


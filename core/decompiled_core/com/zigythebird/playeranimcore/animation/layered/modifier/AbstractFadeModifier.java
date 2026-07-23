/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.zigythebird.playeranimcore.animation.layered.modifier;

import com.zigythebird.playeranimcore.PlayerAnimLib;
import com.zigythebird.playeranimcore.animation.AnimationController;
import com.zigythebird.playeranimcore.animation.AnimationData;
import com.zigythebird.playeranimcore.animation.layered.IAnimation;
import com.zigythebird.playeranimcore.animation.layered.modifier.AbstractModifier;
import com.zigythebird.playeranimcore.bones.PlayerAnimBone;
import com.zigythebird.playeranimcore.easing.EasingType;
import com.zigythebird.playeranimcore.enums.FadeType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractFadeModifier
extends AbstractModifier {
    protected int length;
    protected int time;
    protected float tickDelta;
    @Nullable
    protected IAnimation transitionAnimation = null;

    public void setTransitionAnimation(@Nullable IAnimation transitionAnimation) {
        this.transitionAnimation = transitionAnimation;
    }

    protected AbstractFadeModifier(int length) {
        this.length = length;
    }

    @Override
    public boolean isActive() {
        return super.isActive() || this.transitionAnimation != null && this.transitionAnimation.isActive();
    }

    @Override
    public boolean canRemove() {
        return this.getFadeType() == FadeType.FADE_IN && this.calculateProgress(this.tickDelta, null) >= 1.0f;
    }

    @Override
    public void setupAnim(AnimationData state) {
        super.setupAnim(state);
        if (this.transitionAnimation != null) {
            this.transitionAnimation.setupAnim(state);
        }
        this.tickDelta = state.getPartialTick();
    }

    @Override
    public void tick(AnimationData state) {
        super.tick(state);
        if (this.transitionAnimation != null) {
            this.transitionAnimation.tick(state);
        }
        ++this.time;
    }

    @Override
    public void get3DTransform(@NotNull PlayerAnimBone bone) {
        if (this.calculateProgress(this.tickDelta, bone.getName()) > 1.0f) {
            super.get3DTransform(bone);
            return;
        }
        PlayerAnimBone copy2 = new PlayerAnimBone(bone.getName());
        copy2.copyOtherBone(bone);
        super.get3DTransform(copy2);
        float a = this.getAlpha(copy2.getName(), this.calculateProgress(this.tickDelta, bone.getName()));
        if (this.getFadeType() == FadeType.FADE_IN && this.transitionAnimation != null && this.transitionAnimation.isActive()) {
            this.transitionAnimation.get3DTransform(bone);
        }
        bone.scale(1.0f - a).add(copy2.scale(a));
    }

    protected float calculateProgress(float f, String boneName) {
        float actualTime = (float)this.time + f;
        if (this.getFadeType() == FadeType.FADE_IN) {
            return actualTime / (float)this.length;
        }
        float endTime = this.getEndTime(boneName);
        if (actualTime >= endTime) {
            return 0.0f;
        }
        if (actualTime < endTime - (float)this.length) {
            return 1.0f;
        }
        return (endTime - actualTime) / (float)this.length;
    }

    protected abstract float getAlpha(String var1, float var2);

    protected abstract FadeType getFadeType();

    protected float getEndTime(String boneName) {
        AnimationController controller;
        AnimationController animationController = this.getController();
        if (animationController instanceof AnimationController && (controller = animationController).getCurrentAnimation() != null) {
            return controller.getCurrentAnimation().animation().length();
        }
        PlayerAnimLib.LOGGER.debug("The fade out modifier doesn't work on animations that aren't AnimationController instances! Please override the getEndTime method.");
        return 0.0f;
    }

    public static AbstractFadeModifier standardFadeIn(int length, EasingType ease) {
        return AbstractFadeModifier.standardFadeIn(length, ease, null);
    }

    public static AbstractFadeModifier standardFadeIn(int length, EasingType ease, @Nullable Float easingVariable) {
        return AbstractFadeModifier.standardFade(length, ease, easingVariable, FadeType.FADE_IN);
    }

    public static AbstractFadeModifier functionalFadeIn(int length, EasingFunction function) {
        return AbstractFadeModifier.functionalFade(length, function, FadeType.FADE_IN);
    }

    public static AbstractFadeModifier standardFadeOut(int length, EasingType ease) {
        return AbstractFadeModifier.standardFadeOut(length, ease, null);
    }

    public static AbstractFadeModifier standardFadeOut(int length, EasingType ease, @Nullable Float easingVariable) {
        return AbstractFadeModifier.standardFade(length, ease, easingVariable, FadeType.FADE_OUT);
    }

    public static AbstractFadeModifier functionalFadeOut(int length, EasingFunction function) {
        return AbstractFadeModifier.functionalFade(length, function, FadeType.FADE_OUT);
    }

    public static AbstractFadeModifier standardFade(int length, final EasingType ease, final @Nullable Float easingVariable, final FadeType fadeType) {
        return new AbstractFadeModifier(length){

            @Override
            protected float getAlpha(String boneName, float progress) {
                return ((Float)ease.buildTransformer(easingVariable).apply((Object)Float.valueOf(progress))).floatValue();
            }

            @Override
            protected FadeType getFadeType() {
                return fadeType;
            }
        };
    }

    public static AbstractFadeModifier functionalFade(int length, final EasingFunction function, final FadeType fadeType) {
        return new AbstractFadeModifier(length){

            @Override
            protected float getAlpha(String boneName, float progress) {
                return function.ease(boneName, progress);
            }

            @Override
            protected FadeType getFadeType() {
                return fadeType;
            }
        };
    }

    @Override
    public String toString() {
        return "AbstractFadeModifier{anim=" + String.valueOf(this.anim) + ", length=" + this.length + ", transitionAnimation=" + String.valueOf(this.transitionAnimation) + "}";
    }

    @FunctionalInterface
    public static interface EasingFunction {
        public float ease(String var1, float var2);
    }
}


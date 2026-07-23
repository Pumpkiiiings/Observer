/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.zigythebird.playeranimcore.animation.layered;

import com.zigythebird.playeranimcore.animation.AnimationData;
import com.zigythebird.playeranimcore.animation.layered.IAnimation;
import com.zigythebird.playeranimcore.api.firstPerson.FirstPersonConfiguration;
import com.zigythebird.playeranimcore.api.firstPerson.FirstPersonMode;
import com.zigythebird.playeranimcore.bones.PlayerAnimBone;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AnimationContainer<T extends IAnimation>
implements IAnimation {
    @Nullable
    protected T anim;

    public AnimationContainer(@Nullable T anim) {
        this.anim = anim;
    }

    public AnimationContainer() {
        this.anim = null;
    }

    public void setAnim(@Nullable T newAnim) {
        this.anim = newAnim;
    }

    @Nullable
    public T getAnim() {
        return this.anim;
    }

    @Override
    public boolean isActive() {
        return this.anim != null && this.anim.isActive();
    }

    @Override
    public void tick(AnimationData state) {
        if (this.anim != null) {
            this.anim.tick(state);
        }
    }

    @Override
    public void get3DTransform(@NotNull PlayerAnimBone bone) {
        if (this.anim != null) {
            this.anim.get3DTransform(bone);
        }
    }

    @Override
    public void setupAnim(AnimationData state) {
        if (this.anim != null) {
            this.anim.setupAnim(state);
        }
    }

    @Override
    @NotNull
    public FirstPersonMode getFirstPersonMode() {
        return this.anim != null ? this.anim.getFirstPersonMode() : FirstPersonMode.NONE;
    }

    @Override
    @NotNull
    public FirstPersonConfiguration getFirstPersonConfiguration() {
        return this.anim != null ? this.anim.getFirstPersonConfiguration() : IAnimation.super.getFirstPersonConfiguration();
    }

    @Override
    public boolean isFirstPersonFollowsCamera() {
        return this.anim != null ? this.anim.isFirstPersonFollowsCamera() : IAnimation.super.isFirstPersonFollowsCamera();
    }

    @Override
    public int getFirstPersonTransitionLength() {
        return this.anim != null ? this.anim.getFirstPersonTransitionLength() : IAnimation.super.getFirstPersonTransitionLength();
    }

    public String toString() {
        return "AnimationContainer{anim=" + String.valueOf(this.anim) + "}";
    }
}


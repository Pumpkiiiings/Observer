/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package com.zigythebird.playeranimcore.animation.layered;

import com.zigythebird.playeranimcore.animation.AnimationData;
import com.zigythebird.playeranimcore.api.firstPerson.FirstPersonConfiguration;
import com.zigythebird.playeranimcore.api.firstPerson.FirstPersonMode;
import com.zigythebird.playeranimcore.bones.PlayerAnimBone;
import org.jetbrains.annotations.NotNull;

public interface IAnimation {
    public static final FirstPersonConfiguration DEFAULT_FIRST_PERSON_CONFIG = new FirstPersonConfiguration();

    default public void tick(AnimationData state) {
    }

    default public void setupAnim(AnimationData state) {
    }

    public boolean isActive();

    public void get3DTransform(@NotNull PlayerAnimBone var1);

    default public PlayerAnimBone get3DTransform(@NotNull String name) {
        PlayerAnimBone bone = new PlayerAnimBone(name);
        this.get3DTransform(bone);
        return bone;
    }

    @NotNull
    default public FirstPersonMode getFirstPersonMode() {
        return FirstPersonMode.NONE;
    }

    @NotNull
    default public FirstPersonConfiguration getFirstPersonConfiguration() {
        return DEFAULT_FIRST_PERSON_CONFIG;
    }

    default public boolean isFirstPersonFollowsCamera() {
        return false;
    }

    default public int getFirstPersonTransitionLength() {
        return 0;
    }

    default public boolean canRemove() {
        return false;
    }
}


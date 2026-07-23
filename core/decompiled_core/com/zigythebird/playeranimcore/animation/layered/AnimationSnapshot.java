/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package com.zigythebird.playeranimcore.animation.layered;

import com.zigythebird.playeranimcore.animation.layered.IAnimation;
import com.zigythebird.playeranimcore.bones.PlayerAnimBone;
import com.zigythebird.playeranimcore.bones.ToggleablePlayerAnimBone;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

public record AnimationSnapshot(Map<String, ToggleablePlayerAnimBone> snapshots) implements IAnimation
{
    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public void get3DTransform(@NotNull PlayerAnimBone bone) {
        if (this.snapshots.containsKey(bone.getName())) {
            bone.copyOtherBoneIfNotDisabled(this.snapshots.get(bone.getName()));
        }
    }

    @Override
    @NotNull
    public String toString() {
        return "AnimationSnapshot{snapshots=" + String.valueOf(this.snapshots) + "}";
    }
}


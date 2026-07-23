/*
 * Decompiled with CFR 0.152.
 */
package com.zigythebird.playeranimcore.bones;

import com.zigythebird.playeranimcore.bones.PlayerAnimBone;

public class ToggleablePlayerAnimBone
extends PlayerAnimBone {
    public boolean scaleXEnabled = true;
    public boolean scaleYEnabled = true;
    public boolean scaleZEnabled = true;
    public boolean positionXEnabled = true;
    public boolean positionYEnabled = true;
    public boolean positionZEnabled = true;
    public boolean rotXEnabled = true;
    public boolean rotYEnabled = true;
    public boolean rotZEnabled = true;
    @Deprecated(forRemoval=true)
    public boolean bendEnabled = true;

    public ToggleablePlayerAnimBone(String name) {
        super(name);
    }

    public ToggleablePlayerAnimBone(PlayerAnimBone bone) {
        super(bone);
        if (bone instanceof ToggleablePlayerAnimBone) {
            ToggleablePlayerAnimBone boneEnabled = (ToggleablePlayerAnimBone)bone;
            this.scaleXEnabled = boneEnabled.isScaleXEnabled();
            this.scaleYEnabled = boneEnabled.isScaleYEnabled();
            this.scaleZEnabled = boneEnabled.isScaleZEnabled();
            this.positionXEnabled = boneEnabled.isPositionXEnabled();
            this.positionYEnabled = boneEnabled.isPositionYEnabled();
            this.positionZEnabled = boneEnabled.isPositionZEnabled();
            this.rotXEnabled = boneEnabled.isRotXEnabled();
            this.rotYEnabled = boneEnabled.isRotYEnabled();
            this.rotZEnabled = boneEnabled.isRotZEnabled();
            this.bendEnabled = boneEnabled.isBendEnabled();
        }
    }

    public void setPositionEnabled(boolean enabled) {
        this.positionXEnabled = enabled;
        this.positionYEnabled = enabled;
        this.positionZEnabled = enabled;
    }

    public void setRotEnabled(boolean enabled) {
        this.rotXEnabled = enabled;
        this.rotYEnabled = enabled;
        this.rotZEnabled = enabled;
    }

    public void setScaleEnabled(boolean enabled) {
        this.scaleXEnabled = enabled;
        this.scaleYEnabled = enabled;
        this.scaleZEnabled = enabled;
    }

    public boolean isScaleXEnabled() {
        return this.scaleXEnabled;
    }

    public boolean isScaleYEnabled() {
        return this.scaleYEnabled;
    }

    public boolean isScaleZEnabled() {
        return this.scaleZEnabled;
    }

    public boolean isPositionXEnabled() {
        return this.positionXEnabled;
    }

    public boolean isPositionYEnabled() {
        return this.positionYEnabled;
    }

    public boolean isPositionZEnabled() {
        return this.positionZEnabled;
    }

    public boolean isRotXEnabled() {
        return this.rotXEnabled;
    }

    public boolean isRotYEnabled() {
        return this.rotYEnabled;
    }

    public boolean isRotZEnabled() {
        return this.rotZEnabled;
    }

    @Deprecated(forRemoval=true)
    public boolean isBendEnabled() {
        return this.bendEnabled;
    }
}


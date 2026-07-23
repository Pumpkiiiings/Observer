/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package com.zigythebird.playeranimcore.animation.layered;

import com.zigythebird.playeranimcore.animation.AnimationData;
import com.zigythebird.playeranimcore.animation.layered.IAnimation;
import com.zigythebird.playeranimcore.bones.PlayerAnimBone;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

public abstract class PlayerAnimationFrame
implements IAnimation {
    protected boolean isActive = false;
    protected PlayerBone head = new PlayerBone();
    protected PlayerBone body = new PlayerBone();
    protected PlayerBone rightArm = new PlayerBone();
    protected PlayerBone leftArm = new PlayerBone();
    protected PlayerBone rightLeg = new PlayerBone();
    protected PlayerBone leftLeg = new PlayerBone();
    protected PlayerBone rightItem = new PlayerBone();
    protected PlayerBone leftItem = new PlayerBone();
    HashMap<String, PlayerBone> parts = new HashMap();

    public PlayerAnimationFrame() {
        this.parts.put("head", this.head);
        this.parts.put("body", this.body);
        this.parts.put("right_arm", this.rightArm);
        this.parts.put("left_arm", this.leftArm);
        this.parts.put("right_leg", this.rightLeg);
        this.parts.put("left_leg", this.leftLeg);
        this.parts.put("right_item", this.rightItem);
        this.parts.put("left_item", this.leftItem);
    }

    @Override
    public void tick(AnimationData state) {
        IAnimation.super.tick(state);
    }

    @Override
    public boolean isActive() {
        return this.isActive;
    }

    public void resetPose() {
        for (Map.Entry<String, PlayerBone> entry : this.parts.entrySet()) {
            entry.getValue().setToInitialPose();
        }
    }

    public void enableAll() {
        for (Map.Entry<String, PlayerBone> entry : this.parts.entrySet()) {
            entry.getValue().enableAll();
        }
    }

    @Override
    public void get3DTransform(@NotNull PlayerAnimBone bone) {
        PlayerBone part = this.parts.get(bone.getName());
        if (part != null) {
            part.applyToBone(bone);
        }
    }

    public String toString() {
        return "PlayerAnimationFrame{isActive=" + this.isActive + ", parts=" + String.valueOf(this.parts) + "}";
    }

    public static class PlayerBone {
        public Float offsetPosX = null;
        public Float offsetPosY = null;
        public Float offsetPosZ = null;
        public Float rotX = null;
        public Float rotY = null;
        public Float rotZ = null;
        public Float scaleX = null;
        public Float scaleY = null;
        public Float scaleZ = null;

        public void setToInitialPose() {
            this.rotX = null;
            this.rotY = null;
            this.rotZ = null;
            this.offsetPosX = null;
            this.offsetPosY = null;
            this.offsetPosZ = null;
            this.scaleX = null;
            this.scaleY = null;
            this.scaleZ = null;
        }

        public void enableAll() {
            this.rotX = Float.valueOf(0.0f);
            this.rotY = Float.valueOf(0.0f);
            this.rotZ = Float.valueOf(0.0f);
            this.offsetPosX = Float.valueOf(0.0f);
            this.offsetPosY = Float.valueOf(0.0f);
            this.offsetPosZ = Float.valueOf(0.0f);
            this.scaleX = Float.valueOf(1.0f);
            this.scaleY = Float.valueOf(1.0f);
            this.scaleZ = Float.valueOf(1.0f);
        }

        public PlayerAnimBone applyToBone(PlayerAnimBone bone) {
            if (this.offsetPosX != null) {
                bone.position.x = this.offsetPosX.floatValue();
            }
            if (this.offsetPosY != null) {
                bone.position.y = this.offsetPosY.floatValue();
            }
            if (this.offsetPosZ != null) {
                bone.position.z = this.offsetPosZ.floatValue();
            }
            if (this.rotX != null) {
                bone.rotation.x = this.rotX.floatValue();
            }
            if (this.rotY != null) {
                bone.rotation.y = this.rotY.floatValue();
            }
            if (this.rotZ != null) {
                bone.rotation.z = this.rotZ.floatValue();
            }
            if (this.scaleX != null) {
                bone.scale.x = this.scaleX.floatValue();
            }
            if (this.scaleY != null) {
                bone.scale.y = this.scaleY.floatValue();
            }
            if (this.scaleZ != null) {
                bone.scale.z = this.scaleZ.floatValue();
            }
            return bone;
        }
    }
}


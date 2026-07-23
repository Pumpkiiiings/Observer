/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package com.zigythebird.playeranimcore.bones;

import com.zigythebird.playeranimcore.animation.Animation;
import com.zigythebird.playeranimcore.animation.keyframe.BoneAnimation;
import com.zigythebird.playeranimcore.animation.keyframe.Keyframe;
import com.zigythebird.playeranimcore.animation.keyframe.KeyframeStack;
import com.zigythebird.playeranimcore.bones.AdvancedPlayerAnimBone;
import com.zigythebird.playeranimcore.bones.ToggleablePlayerAnimBone;
import com.zigythebird.playeranimcore.easing.EasingType;
import com.zigythebird.playeranimcore.enums.Axis;
import com.zigythebird.playeranimcore.enums.TransformType;
import java.util.List;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class PlayerAnimBone {
    public final String name;
    public final Vector3f position;
    public final Vector3f rotation;
    public final Vector3f scale;
    @Deprecated(forRemoval=true)
    public float bend;

    public PlayerAnimBone(String name) {
        this.name = name;
        this.position = new Vector3f();
        this.rotation = new Vector3f();
        this.scale = new Vector3f(1.0f);
    }

    public PlayerAnimBone(PlayerAnimBone bone) {
        this.name = bone.getName();
        this.position = new Vector3f((Vector3fc)bone.position);
        this.rotation = new Vector3f((Vector3fc)bone.rotation);
        this.scale = new Vector3f((Vector3fc)bone.scale);
        this.bend = bone.bend;
    }

    public String getName() {
        return this.name;
    }

    public void setToInitialPose() {
        this.position.set(0.0f, 0.0f, 0.0f);
        this.rotation.set(0.0f, 0.0f, 0.0f);
        this.scale.set(1.0f, 1.0f, 1.0f);
        this.bend = 0.0f;
    }

    public PlayerAnimBone scale(float value) {
        this.position.mul(value);
        this.rotation.mul(value);
        this.scale.mul(value);
        this.bend *= value;
        return this;
    }

    public PlayerAnimBone add(PlayerAnimBone bone) {
        this.position.add((Vector3fc)bone.position);
        this.rotation.add((Vector3fc)bone.rotation);
        this.scale.add((Vector3fc)bone.scale);
        this.bend += bone.bend;
        return this;
    }

    public PlayerAnimBone applyOtherBone(PlayerAnimBone bone) {
        this.position.add((Vector3fc)bone.position);
        this.rotation.add((Vector3fc)bone.rotation);
        this.scale.mul((Vector3fc)bone.scale);
        this.bend += bone.bend;
        return this;
    }

    public PlayerAnimBone copyOtherBone(PlayerAnimBone bone) {
        this.position.set((Vector3fc)bone.position);
        this.rotation.set((Vector3fc)bone.rotation);
        this.scale.set((Vector3fc)bone.scale);
        this.bend = bone.bend;
        return this;
    }

    public PlayerAnimBone copyOtherBoneIfNotDisabled(PlayerAnimBone bone) {
        if (bone instanceof ToggleablePlayerAnimBone) {
            ToggleablePlayerAnimBone toggleableBone = (ToggleablePlayerAnimBone)bone;
            if (toggleableBone.isPositionXEnabled()) {
                this.position.x = bone.position.x;
            }
            if (toggleableBone.isPositionYEnabled()) {
                this.position.y = bone.position.y;
            }
            if (toggleableBone.isPositionZEnabled()) {
                this.position.z = bone.position.z;
            }
            if (toggleableBone.isRotXEnabled()) {
                this.rotation.x = bone.rotation.x;
            }
            if (toggleableBone.isRotYEnabled()) {
                this.rotation.y = bone.rotation.y;
            }
            if (toggleableBone.isRotZEnabled()) {
                this.rotation.z = bone.rotation.z;
            }
            if (toggleableBone.isScaleXEnabled()) {
                this.scale.x = bone.scale.x;
            }
            if (toggleableBone.isScaleYEnabled()) {
                this.scale.y = bone.scale.y;
            }
            if (toggleableBone.isScaleZEnabled()) {
                this.scale.z = bone.scale.z;
            }
            if (toggleableBone.isBendEnabled()) {
                this.bend = bone.bend;
            }
            return this;
        }
        return this.copyOtherBone(bone);
    }

    @ApiStatus.Internal
    public void beginOrEndTickLerp(AdvancedPlayerAnimBone bone, float animTime, Animation animation) {
        if (bone.positionXEnabled) {
            this.position.x = this.beginOrEndTickLerp(this.position.x, bone.position.x, bone.positionXTransitionLength, animTime, animation, TransformType.POSITION, Axis.X);
        }
        if (bone.positionYEnabled) {
            this.position.y = this.beginOrEndTickLerp(this.position.y, bone.position.y, bone.positionYTransitionLength, animTime, animation, TransformType.POSITION, Axis.Y);
        }
        if (bone.positionZEnabled) {
            this.position.z = this.beginOrEndTickLerp(this.position.z, bone.position.z, bone.positionZTransitionLength, animTime, animation, TransformType.POSITION, Axis.Z);
        }
        if (bone.rotXEnabled) {
            this.rotation.x = this.beginOrEndTickLerp(this.rotation.x, bone.rotation.x, bone.rotXTransitionLength, animTime, animation, TransformType.ROTATION, Axis.X);
        }
        if (bone.rotYEnabled) {
            this.rotation.y = this.beginOrEndTickLerp(this.rotation.y, bone.rotation.y, bone.rotYTransitionLength, animTime, animation, TransformType.ROTATION, Axis.Y);
        }
        if (bone.rotZEnabled) {
            this.rotation.z = this.beginOrEndTickLerp(this.rotation.z, bone.rotation.z, bone.rotZTransitionLength, animTime, animation, TransformType.ROTATION, Axis.Z);
        }
        if (bone.scaleXEnabled) {
            this.scale.x = this.beginOrEndTickLerp(this.scale.x, bone.scale.x, bone.scaleXTransitionLength, animTime, animation, TransformType.SCALE, Axis.X);
        }
        if (bone.scaleYEnabled) {
            this.scale.y = this.beginOrEndTickLerp(this.scale.y, bone.scale.y, bone.scaleYTransitionLength, animTime, animation, TransformType.SCALE, Axis.Y);
        }
        if (bone.scaleZEnabled) {
            this.scale.z = this.beginOrEndTickLerp(this.scale.z, bone.scale.z, bone.scaleZTransitionLength, animTime, animation, TransformType.SCALE, Axis.Z);
        }
        if (bone.bendEnabled) {
            this.bend = this.beginOrEndTickLerp(this.bend, bone.bend, bone.bendTransitionLength, animTime, animation, TransformType.BEND, Axis.Y);
        }
    }

    private float beginOrEndTickLerp(float startValue, float endValue, Float transitionLength, float animTime, Animation animation, TransformType type, Axis axis) {
        EasingType easingType = EasingType.EASE_IN_OUT_SINE;
        if (animation != null) {
            float temp = startValue;
            startValue = endValue;
            endValue = temp;
            if (transitionLength == null) {
                transitionLength = Float.valueOf(animation.length() - ((Float)animation.data().getRaw("endTick")).floatValue());
            }
            if (animation.data().has("easeBeforeKeyframe") && !((Boolean)animation.data().getRaw("easeBeforeKeyframe")).booleanValue()) {
                List<Keyframe> keyFrames;
                KeyframeStack keyframeStack;
                BoneAnimation boneAnimation = animation.getBone(this.getName());
                if (boneAnimation == null) {
                    v0 = null;
                } else {
                    switch (type) {
                        default: {
                            throw new MatchException(null, null);
                        }
                        case BEND: {
                            List<Keyframe> bendKeyFrames = boneAnimation.bendKeyFrames();
                            if (!bendKeyFrames.isEmpty()) {
                                easingType = bendKeyFrames.getLast().easingType();
                            }
                            v0 = null;
                            break;
                        }
                        case ROTATION: {
                            v0 = boneAnimation.rotationKeyFrames();
                            break;
                        }
                        case SCALE: {
                            v0 = boneAnimation.scaleKeyFrames();
                            break;
                        }
                        case POSITION: {
                            v0 = keyframeStack = boneAnimation.positionKeyFrames();
                        }
                    }
                }
                if (keyframeStack != null && !(keyFrames = keyframeStack.getKeyFramesForAxis(axis)).isEmpty()) {
                    easingType = keyFrames.getLast().easingType();
                }
            }
            if (easingType == EasingType.BEZIER || easingType == EasingType.CATMULLROM) {
                easingType = EasingType.EASE_IN_OUT_SINE;
            }
        }
        if (transitionLength == null) {
            return endValue;
        }
        return easingType.apply(startValue, endValue, animTime / transitionLength.floatValue());
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        return this.hashCode() == obj.hashCode();
    }

    public int hashCode() {
        return this.getName().hashCode();
    }
}


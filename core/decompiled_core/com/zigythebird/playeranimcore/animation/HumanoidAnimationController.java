/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.joml.Matrix4f
 *  team.unnamed.mocha.MochaEngine
 */
package com.zigythebird.playeranimcore.animation;

import com.zigythebird.playeranimcore.animation.AnimationController;
import com.zigythebird.playeranimcore.animation.AnimationData;
import com.zigythebird.playeranimcore.bones.AdvancedPlayerAnimBone;
import com.zigythebird.playeranimcore.bones.PlayerAnimBone;
import com.zigythebird.playeranimcore.math.Vec3f;
import com.zigythebird.playeranimcore.util.MatrixUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import team.unnamed.mocha.MochaEngine;

public class HumanoidAnimationController
extends AnimationController {
    public static final Map<String, Vec3f> BONE_POSITIONS = Map.ofEntries(Map.entry("right_item", new Vec3f(6.0f, 12.0f, -2.0f)), Map.entry("left_item", new Vec3f(-6.0f, 12.0f, -2.0f)), Map.entry("right_arm", new Vec3f(5.0f, 22.0f, 0.0f)), Map.entry("left_arm", new Vec3f(-5.0f, 22.0f, 0.0f)), Map.entry("left_leg", new Vec3f(-2.0f, 12.0f, 0.0f)), Map.entry("right_leg", new Vec3f(2.0f, 12.0f, 0.0f)), Map.entry("torso", new Vec3f(0.0f, 24.0f, 0.0f)), Map.entry("head", new Vec3f(0.0f, 24.0f, 0.0f)), Map.entry("body", new Vec3f(0.0f, 12.0f, 0.0f)), Map.entry("cape", new Vec3f(0.0f, 24.0f, 2.0f)), Map.entry("elytra", new Vec3f(0.0f, 24.0f, 2.0f)));
    protected List<String> top_bones;
    private float torsoBend;
    private int torsoBendSign;

    public HumanoidAnimationController(AnimationController.AnimationStateHandler animationHandler, Function<AnimationController, MochaEngine<AnimationController>> molangRuntime) {
        this(animationHandler, BONE_POSITIONS, molangRuntime);
    }

    public HumanoidAnimationController(AnimationController.AnimationStateHandler animationHandler, Map<String, Vec3f> bonePositions, Function<AnimationController, MochaEngine<AnimationController>> molangRuntime) {
        super(animationHandler, bonePositions, molangRuntime);
    }

    @Override
    public void registerBones() {
        this.top_bones = new ArrayList<String>();
        this.registerPlayerAnimBone("body");
        this.registerTopPlayerAnimBone("right_arm");
        this.registerTopPlayerAnimBone("left_arm");
        this.registerPlayerAnimBone("right_leg");
        this.registerPlayerAnimBone("left_leg");
        this.registerTopPlayerAnimBone("head");
        this.registerPlayerAnimBone("torso");
        this.registerPlayerAnimBone("right_item");
        this.registerPlayerAnimBone("left_item");
        this.registerTopPlayerAnimBone("cape");
        this.registerPlayerAnimBone("elytra");
    }

    public void registerTopPlayerAnimBone(String name) {
        this.top_bones.add(name);
        this.registerPlayerAnimBone(name);
    }

    @Override
    public void process(AnimationData state) {
        super.process(state);
        this.torsoBend = ((AdvancedPlayerAnimBone)this.bones.get((Object)"torso")).bend;
        float absBend = Math.abs(this.torsoBend);
        this.torsoBendSign = (double)absBend > 0.001 && this.currentAnimation != null && this.currentAnimation.animation().data().getNullable("applyBendToOtherBones") == Boolean.TRUE ? (int)Math.signum(this.torsoBend) : 0;
    }

    @Override
    public PlayerAnimBone get3DTransformRaw(@NotNull PlayerAnimBone bone) {
        bone = super.get3DTransformRaw(bone);
        String name = bone.getName();
        if (this.torsoBendSign != 0 && this.top_bones.contains(name)) {
            Matrix4f matrix4f = new Matrix4f();
            matrix4f.translate(0.0f, 18.0f, 0.0f);
            matrix4f.rotateX(this.torsoBend);
            matrix4f.translate(0.0f, -18.0f, 0.0f);
            MatrixUtil.applyMatrixToBone(bone, matrix4f, this.getBonePosition(name));
        }
        return bone;
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  team.unnamed.mocha.parser.ast.Expression
 *  team.unnamed.mocha.parser.ast.FloatExpression
 *  team.unnamed.mocha.runtime.IsConstantExpression
 *  team.unnamed.mocha.util.ExprBytesUtils
 *  team.unnamed.mocha.util.network.ProtocolUtils
 *  team.unnamed.mocha.util.network.VarIntUtils
 */
package com.zigythebird.playeranimcore.network;

import com.zigythebird.playeranimcore.animation.Animation;
import com.zigythebird.playeranimcore.animation.ExtraAnimationData;
import com.zigythebird.playeranimcore.animation.keyframe.BoneAnimation;
import com.zigythebird.playeranimcore.animation.keyframe.Keyframe;
import com.zigythebird.playeranimcore.animation.keyframe.KeyframeStack;
import com.zigythebird.playeranimcore.animation.keyframe.event.data.CustomInstructionKeyframeData;
import com.zigythebird.playeranimcore.animation.keyframe.event.data.KeyFrameData;
import com.zigythebird.playeranimcore.animation.keyframe.event.data.ParticleKeyframeData;
import com.zigythebird.playeranimcore.animation.keyframe.event.data.SoundKeyframeData;
import com.zigythebird.playeranimcore.easing.EasingType;
import com.zigythebird.playeranimcore.enums.AnimationFormat;
import com.zigythebird.playeranimcore.enums.TransformType;
import com.zigythebird.playeranimcore.loading.PlayerAnimatorLoader;
import com.zigythebird.playeranimcore.math.Vec3f;
import com.zigythebird.playeranimcore.molang.MolangLoader;
import com.zigythebird.playeranimcore.network.AnimationBinaryV6;
import com.zigythebird.playeranimcore.network.LegacyAnimationBinary;
import com.zigythebird.playeranimcore.network.NetworkUtils;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import team.unnamed.mocha.parser.ast.Expression;
import team.unnamed.mocha.parser.ast.FloatExpression;
import team.unnamed.mocha.runtime.IsConstantExpression;
import team.unnamed.mocha.util.ExprBytesUtils;
import team.unnamed.mocha.util.network.ProtocolUtils;
import team.unnamed.mocha.util.network.VarIntUtils;

public final class AnimationBinary {
    public static int getCurrentVersion() {
        return 6;
    }

    public static void write(ByteBuf buf, Animation animation) {
        AnimationBinary.write(buf, AnimationBinary.getCurrentVersion(), animation);
    }

    public static void write(ByteBuf buf, int version, Animation animation) {
        if (version >= 6) {
            AnimationBinaryV6.write(buf, version, animation);
            return;
        }
        Map<String, Object> data = animation.data().data();
        boolean applyBendToOtherBones = (Boolean)data.getOrDefault("applyBendToOtherBones", false);
        if (version < 3 && applyBendToOtherBones && animation.boneAnimations().containsKey("torso") && !animation.boneAnimations().get("torso").bendKeyFrames().isEmpty()) {
            applyBendToOtherBones = false;
        }
        buf.writeFloat(animation.length());
        boolean shouldPlayAgain = animation.loopType().shouldPlayAgain(null, animation);
        buf.writeBoolean(shouldPlayAgain);
        if (shouldPlayAgain) {
            if (animation.loopType() == Animation.LoopType.HOLD_ON_LAST_FRAME) {
                buf.writeBoolean(true);
            } else {
                buf.writeBoolean(false);
                buf.writeFloat(animation.loopType().restartFromTick(null, animation));
            }
        }
        buf.writeByte((int)((AnimationFormat)((Object)data.getOrDefault((Object)"format", (Object)((Object)AnimationFormat.GECKOLIB)))).id);
        buf.writeFloat(((Float)data.getOrDefault("beginTick", Float.valueOf(Float.NaN))).floatValue());
        buf.writeFloat(((Float)data.getOrDefault("endTick", Float.valueOf(Float.NaN))).floatValue());
        if (version > 1) {
            buf.writeBoolean(applyBendToOtherBones);
            buf.writeBoolean(((Boolean)data.getOrDefault("easeBeforeKeyframe", true)).booleanValue());
        }
        NetworkUtils.writeUuid(buf, animation.uuid());
        VarIntUtils.writeVarInt((ByteBuf)buf, (int)animation.boneAnimations().size());
        for (Map.Entry<String, BoneAnimation> entry : animation.boneAnimations().entrySet()) {
            ProtocolUtils.writeString((ByteBuf)buf, (String)entry.getKey());
            AnimationBinary.writeBoneAnimation(buf, entry.getValue(), version < 4 && entry.getKey().equals("body"), version < 5 && LegacyAnimationBinary.ITEM_BONE.test(entry.getKey()));
        }
        AnimationBinary.writeEventKeyframes(buf, animation.keyFrames());
        NetworkUtils.writeMap(buf, animation.bones(), ProtocolUtils::writeString, NetworkUtils::writeVec3f);
        NetworkUtils.writeMap(buf, animation.parents(), ProtocolUtils::writeString, ProtocolUtils::writeString);
    }

    public static void writeBoneAnimation(ByteBuf buf, BoneAnimation bone, boolean isBody, boolean isItem) {
        AnimationBinary.writeKeyframeStack(buf, bone.rotationKeyFrames(), isBody, false, TransformType.ROTATION);
        AnimationBinary.writeKeyframeStack(buf, bone.positionKeyFrames(), isBody, isItem, TransformType.POSITION);
        AnimationBinary.writeKeyframeStack(buf, bone.scaleKeyFrames(), false, false, TransformType.SCALE);
        ProtocolUtils.writeList((ByteBuf)buf, bone.bendKeyFrames(), AnimationBinary::writeKeyframe);
    }

    public static void writeKeyframeStack(ByteBuf buf, KeyframeStack stack, boolean isBody, boolean isItem, TransformType type) {
        ProtocolUtils.writeList((ByteBuf)buf, isBody ? AnimationBinary.negateKeyframes(stack.xKeyframes()) : stack.xKeyframes(), AnimationBinary::writeKeyframe);
        ProtocolUtils.writeList((ByteBuf)buf, isItem || isBody && type == TransformType.ROTATION ? AnimationBinary.negateKeyframes(stack.yKeyframes()) : stack.yKeyframes(), AnimationBinary::writeKeyframe);
        ProtocolUtils.writeList((ByteBuf)buf, stack.zKeyframes(), AnimationBinary::writeKeyframe);
    }

    public static void writeKeyframe(Keyframe keyframe, ByteBuf buf) {
        buf.writeFloat(keyframe.length());
        ExprBytesUtils.writeExpressions(keyframe.endValue(), (ByteBuf)buf);
        buf.writeByte((int)keyframe.easingType().id);
        ProtocolUtils.writeList((ByteBuf)buf, keyframe.easingArgs(), ExprBytesUtils::writeExpressions);
    }

    static void writeEventKeyframes(ByteBuf buf, Animation.Keyframes keyFrames) {
        VarIntUtils.writeVarInt((ByteBuf)buf, (int)keyFrames.sounds().length);
        for (SoundKeyframeData soundKeyframeData : keyFrames.sounds()) {
            buf.writeFloat(soundKeyframeData.getStartTick());
            ProtocolUtils.writeString((ByteBuf)buf, (String)soundKeyframeData.getSound());
        }
        VarIntUtils.writeVarInt((ByteBuf)buf, (int)keyFrames.particles().length);
        for (KeyFrameData keyFrameData : keyFrames.particles()) {
            buf.writeFloat(keyFrameData.getStartTick());
            ProtocolUtils.writeString((ByteBuf)buf, (String)((ParticleKeyframeData)keyFrameData).getEffect());
            ProtocolUtils.writeString((ByteBuf)buf, (String)((ParticleKeyframeData)keyFrameData).getLocator());
            ProtocolUtils.writeString((ByteBuf)buf, (String)((ParticleKeyframeData)keyFrameData).script());
        }
        VarIntUtils.writeVarInt((ByteBuf)buf, (int)keyFrames.customInstructions().length);
        for (KeyFrameData keyFrameData : keyFrames.customInstructions()) {
            buf.writeFloat(keyFrameData.getStartTick());
            ProtocolUtils.writeString((ByteBuf)buf, (String)((CustomInstructionKeyframeData)keyFrameData).getInstructions());
        }
    }

    public static Animation read(ByteBuf buf) {
        return AnimationBinary.read(buf, AnimationBinary.getCurrentVersion());
    }

    public static Animation read(ByteBuf buf, int version) {
        if (version >= 6) {
            return AnimationBinaryV6.read(buf, version);
        }
        float length = buf.readFloat();
        Animation.LoopType loopType = Animation.LoopType.PLAY_ONCE;
        if (buf.readBoolean()) {
            loopType = buf.readBoolean() ? Animation.LoopType.HOLD_ON_LAST_FRAME : Animation.LoopType.returnToTickLoop(buf.readFloat());
        }
        ExtraAnimationData data = new ExtraAnimationData();
        AnimationFormat format = AnimationFormat.fromId(buf.readByte());
        data.put("format", (Object)format);
        float beginTick = buf.readFloat();
        float endTick = buf.readFloat();
        if (!Float.isNaN(beginTick)) {
            data.put("beginTick", Float.valueOf(beginTick));
        }
        if (!Float.isNaN(endTick)) {
            data.put("endTick", Float.valueOf(endTick));
        }
        if (version > 1) {
            boolean applyBendToOtherBones = buf.readBoolean();
            boolean easeBefore = buf.readBoolean();
            if (applyBendToOtherBones) {
                data.put("applyBendToOtherBones", true);
            }
            if (!easeBefore) {
                data.put("easeBeforeKeyframe", false);
            }
        } else {
            data.put("applyBendToOtherBones", true);
        }
        data.put("uuid", NetworkUtils.readUuid(buf));
        Map<String, BoneAnimation> boneAnimations = NetworkUtils.readMap(buf, ProtocolUtils::readString, buf1 -> AnimationBinary.readBoneAnimation(buf1, format == AnimationFormat.PLAYER_ANIMATOR));
        if (version < 4 && boneAnimations.containsKey("body")) {
            BoneAnimation body = boneAnimations.get("body");
            body.positionKeyFrames().xKeyframes().replaceAll(AnimationBinary::negateKeyframeExpressions);
            body.rotationKeyFrames().xKeyframes().replaceAll(AnimationBinary::negateKeyframeExpressions);
            body.rotationKeyFrames().yKeyframes().replaceAll(AnimationBinary::negateKeyframeExpressions);
        }
        if (version < 5) {
            if (boneAnimations.containsKey("right_item")) {
                boneAnimations.get("right_item").positionKeyFrames().yKeyframes().replaceAll(AnimationBinary::negateKeyframeExpressions);
            }
            if (boneAnimations.containsKey("left_item")) {
                boneAnimations.get("left_item").positionKeyFrames().yKeyframes().replaceAll(AnimationBinary::negateKeyframeExpressions);
            }
        }
        Animation.Keyframes keyFrames = AnimationBinary.readEventKeyframes(buf);
        Map<String, Vec3f> pivotBones = NetworkUtils.readMap(buf, ProtocolUtils::readString, NetworkUtils::readVec3f);
        Map<String, String> parents = NetworkUtils.readMap(buf, ProtocolUtils::readString, ProtocolUtils::readString);
        return new Animation(data, length, loopType, boneAnimations, keyFrames, pivotBones, parents);
    }

    public static BoneAnimation readBoneAnimation(ByteBuf buf, boolean shouldStartFromDefault) {
        KeyframeStack rotationKeyFrames = AnimationBinary.readKeyframeStack(buf, shouldStartFromDefault, false);
        KeyframeStack positionKeyFrames = AnimationBinary.readKeyframeStack(buf, shouldStartFromDefault, false);
        KeyframeStack scaleKeyFrames = AnimationBinary.readKeyframeStack(buf, shouldStartFromDefault, true);
        List<Keyframe> bendKeyFrames = AnimationBinary.readKeyframeList(buf, shouldStartFromDefault, false);
        return new BoneAnimation(rotationKeyFrames, positionKeyFrames, scaleKeyFrames, bendKeyFrames);
    }

    public static KeyframeStack readKeyframeStack(ByteBuf buf, boolean shouldStartFromDefault, boolean isScale) {
        List<Keyframe> xKeyframes = AnimationBinary.readKeyframeList(buf, shouldStartFromDefault, isScale);
        List<Keyframe> yKeyframes = AnimationBinary.readKeyframeList(buf, shouldStartFromDefault, isScale);
        List<Keyframe> zKeyframes = AnimationBinary.readKeyframeList(buf, shouldStartFromDefault, isScale);
        return new KeyframeStack(xKeyframes, yKeyframes, zKeyframes);
    }

    public static List<Keyframe> readKeyframeList(ByteBuf buf, boolean shouldStartFromDefault, boolean isScale) {
        int count = VarIntUtils.readVarInt((ByteBuf)buf);
        ArrayList<Keyframe> list = new ArrayList<Keyframe>(count);
        for (int i = 0; i < count; ++i) {
            float length = buf.readFloat();
            List<Expression> endValue = ExprBytesUtils.readExpressions((ByteBuf)buf);
            List<Expression> startValue = list.isEmpty() ? (shouldStartFromDefault ? (isScale ? PlayerAnimatorLoader.ONE : PlayerAnimatorLoader.ZERO) : endValue) : ((Keyframe)list.getLast()).endValue();
            EasingType easingType = EasingType.fromId(buf.readByte());
            List easingArgs = ProtocolUtils.readList((ByteBuf)buf, ExprBytesUtils::readExpressions);
            list.add(new Keyframe(length, startValue, endValue, easingType, easingArgs));
        }
        return list;
    }

    static Animation.Keyframes readEventKeyframes(ByteBuf buf) {
        int soundCount = VarIntUtils.readVarInt((ByteBuf)buf);
        SoundKeyframeData[] sounds = new SoundKeyframeData[soundCount];
        for (int i = 0; i < soundCount; ++i) {
            float startTick = buf.readFloat();
            String sound = ProtocolUtils.readString((ByteBuf)buf);
            sounds[i] = new SoundKeyframeData(Float.valueOf(startTick), sound);
        }
        int particleCount = VarIntUtils.readVarInt((ByteBuf)buf);
        ParticleKeyframeData[] particles = new ParticleKeyframeData[particleCount];
        for (int i = 0; i < particleCount; ++i) {
            float startTick = buf.readFloat();
            String effect = ProtocolUtils.readString((ByteBuf)buf);
            String locator = ProtocolUtils.readString((ByteBuf)buf);
            String script = ProtocolUtils.readString((ByteBuf)buf);
            particles[i] = new ParticleKeyframeData(startTick, effect, locator, script);
        }
        int customInstructionCount = VarIntUtils.readVarInt((ByteBuf)buf);
        CustomInstructionKeyframeData[] customInstructions = new CustomInstructionKeyframeData[customInstructionCount];
        for (int i = 0; i < customInstructionCount; ++i) {
            float startTick = buf.readFloat();
            String instructions = ProtocolUtils.readString((ByteBuf)buf);
            customInstructions[i] = new CustomInstructionKeyframeData(startTick, instructions);
        }
        return new Animation.Keyframes(sounds, particles, customInstructions);
    }

    private static List<Keyframe> negateKeyframes(List<Keyframe> keyframes) {
        keyframes = new ArrayList<Keyframe>(keyframes);
        keyframes.replaceAll(AnimationBinary::negateKeyframeExpressions);
        return keyframes;
    }

    private static Keyframe negateKeyframeExpressions(Keyframe keyframe) {
        keyframe = new Keyframe(keyframe.length(), new ArrayList<Expression>(keyframe.startValue()), new ArrayList<Expression>(keyframe.endValue()), keyframe.easingType(), keyframe.easingArgs());
        AnimationBinary.negateKeyframeExpressions(keyframe.startValue());
        AnimationBinary.negateKeyframeExpressions(keyframe.endValue());
        return keyframe;
    }

    private static void negateKeyframeExpressions(List<Expression> expressions) {
        if (expressions.size() == 1 && IsConstantExpression.test((Expression)expressions.getFirst())) {
            expressions.set(0, (Expression)FloatExpression.of((float)(-MolangLoader.MOCHA_ENGINE.eval(expressions))));
        }
    }
}


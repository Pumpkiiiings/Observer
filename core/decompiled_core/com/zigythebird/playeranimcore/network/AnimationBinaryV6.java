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
import com.zigythebird.playeranimcore.easing.EasingType;
import com.zigythebird.playeranimcore.enums.AnimationFormat;
import com.zigythebird.playeranimcore.loading.PlayerAnimatorLoader;
import com.zigythebird.playeranimcore.math.Vec3f;
import com.zigythebird.playeranimcore.molang.MolangLoader;
import com.zigythebird.playeranimcore.network.AnimationBinary;
import com.zigythebird.playeranimcore.network.BoneChannel;
import com.zigythebird.playeranimcore.network.HeaderFlag;
import com.zigythebird.playeranimcore.network.KeyframeFlag;
import com.zigythebird.playeranimcore.network.NetworkUtils;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import team.unnamed.mocha.parser.ast.Expression;
import team.unnamed.mocha.parser.ast.FloatExpression;
import team.unnamed.mocha.runtime.IsConstantExpression;
import team.unnamed.mocha.util.ExprBytesUtils;
import team.unnamed.mocha.util.network.ProtocolUtils;
import team.unnamed.mocha.util.network.VarIntUtils;

final class AnimationBinaryV6 {
    AnimationBinaryV6() {
    }

    static void write(ByteBuf buf, int version, Animation animation) {
        Map<String, Object> data = animation.data().data();
        AnimationBinaryV6.writeHeader(buf, animation, data);
        VarIntUtils.writeVarInt((ByteBuf)buf, (int)animation.boneAnimations().size());
        for (Map.Entry<String, BoneAnimation> entry : animation.boneAnimations().entrySet()) {
            ProtocolUtils.writeString((ByteBuf)buf, (String)entry.getKey());
            AnimationBinaryV6.writeBoneAnimation(buf, entry.getValue(), version);
        }
        AnimationBinary.writeEventKeyframes(buf, animation.keyFrames());
        NetworkUtils.writeMap(buf, animation.bones(), ProtocolUtils::writeString, NetworkUtils::writeVec3f);
        NetworkUtils.writeMap(buf, animation.parents(), ProtocolUtils::writeString, ProtocolUtils::writeString);
    }

    static Animation read(ByteBuf buf, int version) {
        ExtraAnimationData data = new ExtraAnimationData();
        int flags = VarIntUtils.readVarInt((ByteBuf)buf);
        boolean shouldPlayAgain = HeaderFlag.SHOULD_PLAY_AGAIN.test(flags);
        boolean isHoldOnLastFrame = HeaderFlag.HOLD_ON_LAST_FRAME.test(flags);
        AnimationFormat format = HeaderFlag.PLAYER_ANIMATOR.test(flags) ? AnimationFormat.PLAYER_ANIMATOR : AnimationFormat.GECKOLIB;
        data.put("format", (Object)format);
        if (HeaderFlag.APPLY_BEND.test(flags)) {
            data.put("applyBendToOtherBones", true);
        }
        if (!HeaderFlag.EASE_BEFORE.test(flags)) {
            data.put("easeBeforeKeyframe", false);
        }
        float length = buf.readFloat();
        Animation.LoopType loopType = shouldPlayAgain ? (isHoldOnLastFrame ? Animation.LoopType.HOLD_ON_LAST_FRAME : Animation.LoopType.returnToTickLoop(buf.readFloat())) : Animation.LoopType.PLAY_ONCE;
        if (HeaderFlag.HAS_BEGIN_TICK.test(flags)) {
            data.put("beginTick", Float.valueOf(buf.readFloat()));
        }
        if (HeaderFlag.HAS_END_TICK.test(flags)) {
            data.put("endTick", Float.valueOf(buf.readFloat()));
        }
        data.put("uuid", NetworkUtils.readUuid(buf));
        boolean isPlayerAnimator = format == AnimationFormat.PLAYER_ANIMATOR;
        Map<String, BoneAnimation> boneAnimations = NetworkUtils.readMap(buf, ProtocolUtils::readString, buf1 -> AnimationBinaryV6.readBoneAnimation(buf1, isPlayerAnimator, version));
        Animation.Keyframes keyFrames = AnimationBinary.readEventKeyframes(buf);
        Map<String, Vec3f> pivotBones = NetworkUtils.readMap(buf, ProtocolUtils::readString, NetworkUtils::readVec3f);
        Map<String, String> parents = NetworkUtils.readMap(buf, ProtocolUtils::readString, ProtocolUtils::readString);
        return new Animation(data, length, loopType, boneAnimations, keyFrames, pivotBones, parents);
    }

    private static void writeHeader(ByteBuf buf, Animation animation, Map<String, Object> data) {
        boolean shouldPlayAgain = animation.loopType().shouldPlayAgain(null, animation);
        boolean isHoldOnLastFrame = animation.loopType() == Animation.LoopType.HOLD_ON_LAST_FRAME;
        AnimationFormat format = (AnimationFormat)((Object)data.getOrDefault("format", (Object)AnimationFormat.GECKOLIB));
        boolean applyBend = (Boolean)data.getOrDefault("applyBendToOtherBones", false);
        boolean easeBefore = (Boolean)data.getOrDefault("easeBeforeKeyframe", true);
        float beginTick = ((Float)data.getOrDefault("beginTick", Float.valueOf(Float.NaN))).floatValue();
        float endTick = ((Float)data.getOrDefault("endTick", Float.valueOf(Float.NaN))).floatValue();
        int flags = 0;
        flags = HeaderFlag.SHOULD_PLAY_AGAIN.set(flags, shouldPlayAgain);
        flags = HeaderFlag.HOLD_ON_LAST_FRAME.set(flags, isHoldOnLastFrame);
        flags = HeaderFlag.PLAYER_ANIMATOR.set(flags, format == AnimationFormat.PLAYER_ANIMATOR);
        flags = HeaderFlag.APPLY_BEND.set(flags, applyBend);
        flags = HeaderFlag.EASE_BEFORE.set(flags, easeBefore);
        flags = HeaderFlag.HAS_BEGIN_TICK.set(flags, !Float.isNaN(beginTick));
        flags = HeaderFlag.HAS_END_TICK.set(flags, !Float.isNaN(endTick));
        VarIntUtils.writeVarInt((ByteBuf)buf, (int)flags);
        buf.writeFloat(animation.length());
        if (shouldPlayAgain && !isHoldOnLastFrame) {
            buf.writeFloat(animation.loopType().restartFromTick(null, animation));
        }
        if (HeaderFlag.HAS_BEGIN_TICK.test(flags)) {
            buf.writeFloat(beginTick);
        }
        if (HeaderFlag.HAS_END_TICK.test(flags)) {
            buf.writeFloat(endTick);
        }
        NetworkUtils.writeUuid(buf, animation.uuid());
    }

    private static void writeBoneAnimation(ByteBuf buf, BoneAnimation bone, int version) {
        int presenceFlags = 0;
        for (BoneChannel ch : BoneChannel.VALUES) {
            if (ch.getKeyframes(bone).isEmpty()) continue;
            presenceFlags |= ch.mask;
        }
        VarIntUtils.writeVarInt((ByteBuf)buf, (int)presenceFlags);
        for (BoneChannel ch : BoneChannel.VALUES) {
            if ((presenceFlags & ch.mask) == 0) continue;
            AnimationBinaryV6.writeKeyframeList(buf, ch.getKeyframes(bone), version);
        }
    }

    private static BoneAnimation readBoneAnimation(ByteBuf buf, boolean shouldStartFromDefault, int version) {
        int presenceFlags = VarIntUtils.readVarInt((ByteBuf)buf);
        List[] lists = new List[BoneChannel.VALUES.length];
        for (BoneChannel ch : BoneChannel.VALUES) {
            lists[ch.ordinal()] = (presenceFlags & ch.mask) != 0 ? AnimationBinaryV6.readKeyframeList(buf, shouldStartFromDefault, ch.isScale, version) : new ArrayList(0);
        }
        return new BoneAnimation(new KeyframeStack(lists[0], lists[1], lists[2]), new KeyframeStack(lists[3], lists[4], lists[5]), new KeyframeStack(lists[6], lists[7], lists[8]), lists[9]);
    }

    private static void writeKeyframeList(ByteBuf buf, List<Keyframe> keyframes, int version) {
        VarIntUtils.writeVarInt((ByteBuf)buf, (int)keyframes.size());
        for (Keyframe keyframe : keyframes) {
            AnimationBinaryV6.writeKeyframe(keyframe, buf, version);
        }
    }

    private static void writeKeyframe(Keyframe keyframe, ByteBuf buf, int version) {
        List<Expression> endValue = keyframe.endValue();
        boolean isConstant = endValue.size() == 1 && IsConstantExpression.test((Expression)endValue.getFirst());
        boolean hasEasingArgs = false;
        for (List<Expression> inner : keyframe.easingArgs()) {
            if (inner.isEmpty()) continue;
            hasEasingArgs = true;
            break;
        }
        int flags = 0;
        if (isConstant) {
            flags |= KeyframeFlag.IS_CONSTANT.mask;
        }
        if (hasEasingArgs) {
            flags |= KeyframeFlag.HAS_EASING_ARGS.mask;
        }
        if (keyframe.length() == 0.0f) {
            flags |= KeyframeFlag.LENGTH_ZERO.mask;
        } else if (keyframe.length() == 1.0f) {
            flags |= KeyframeFlag.LENGTH_ONE.mask;
        }
        VarIntUtils.writeVarInt((ByteBuf)buf, (int)KeyframeFlag.pack(keyframe.easingType().id, flags, version));
        if (isConstant) {
            buf.writeFloat(MolangLoader.MOCHA_ENGINE.eval(endValue));
        } else {
            ExprBytesUtils.writeExpressions(endValue, (ByteBuf)buf);
        }
        if ((flags & (KeyframeFlag.LENGTH_ZERO.mask | KeyframeFlag.LENGTH_ONE.mask)) == 0) {
            buf.writeFloat(keyframe.length());
        }
        if (hasEasingArgs) {
            ProtocolUtils.writeList((ByteBuf)buf, keyframe.easingArgs(), ExprBytesUtils::writeExpressions);
        }
    }

    private static List<Keyframe> readKeyframeList(ByteBuf buf, boolean shouldStartFromDefault, boolean isScale, int version) {
        int count = VarIntUtils.readVarInt((ByteBuf)buf);
        ArrayList<Keyframe> list = new ArrayList<Keyframe>(count);
        for (int i = 0; i < count; ++i) {
            int combined = VarIntUtils.readVarInt((ByteBuf)buf);
            int easingId = KeyframeFlag.unpackEasing(combined, version);
            int flags = KeyframeFlag.unpackFlags(combined, version);
            boolean isConstant = (flags & KeyframeFlag.IS_CONSTANT.mask) != 0;
            boolean hasEasingArgs = (flags & KeyframeFlag.HAS_EASING_ARGS.mask) != 0;
            List<FloatExpression> endValue = isConstant ? List.of(FloatExpression.of((float)buf.readFloat())) : ExprBytesUtils.readExpressions((ByteBuf)buf);
            float length = (flags & KeyframeFlag.LENGTH_ZERO.mask) != 0 ? 0.0f : ((flags & KeyframeFlag.LENGTH_ONE.mask) != 0 ? 1.0f : buf.readFloat());
            List<FloatExpression> startValue = list.isEmpty() ? (shouldStartFromDefault ? (isScale ? PlayerAnimatorLoader.ONE : PlayerAnimatorLoader.ZERO) : endValue) : ((Keyframe)list.getLast()).endValue();
            EasingType easingType = EasingType.fromId((byte)easingId);
            List<Object> easingArgs = hasEasingArgs ? ProtocolUtils.readList((ByteBuf)buf, ExprBytesUtils::readExpressions) : (shouldStartFromDefault && i > 0 ? Collections.singletonList(new ArrayList(0)) : new ArrayList(0));
            list.add(new Keyframe(length, startValue, endValue, easingType, easingArgs));
        }
        return list;
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  team.unnamed.mocha.parser.ast.Expression
 *  team.unnamed.mocha.parser.ast.FloatExpression
 */
package com.zigythebird.playeranimcore.network;

import com.zigythebird.playeranimcore.animation.Animation;
import com.zigythebird.playeranimcore.animation.ExtraAnimationData;
import com.zigythebird.playeranimcore.animation.keyframe.BoneAnimation;
import com.zigythebird.playeranimcore.animation.keyframe.Keyframe;
import com.zigythebird.playeranimcore.easing.EasingType;
import com.zigythebird.playeranimcore.enums.AnimationFormat;
import com.zigythebird.playeranimcore.loading.PlayerAnimatorLoader;
import com.zigythebird.playeranimcore.loading.UniversalAnimLoader;
import com.zigythebird.playeranimcore.math.Vec3f;
import com.zigythebird.playeranimcore.molang.MolangLoader;
import com.zigythebird.playeranimcore.network.NetworkUtils;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import team.unnamed.mocha.parser.ast.Expression;
import team.unnamed.mocha.parser.ast.FloatExpression;

public final class LegacyAnimationBinary {
    public static final Predicate<String> BEND_BONE = name -> !name.equals("head") && !name.equals("left_item") && !name.equals("right_item");
    public static final Predicate<String> ITEM_BONE = name -> name.equals("left_item") || name.equals("right_item");

    public static void write(Animation animation, ByteBuf buf, int version) {
        buf.writeInt(animation.data().get("beginTick").orElse(Float.valueOf(0.0f)).intValue());
        int endTick = animation.data().get("endTick").orElse(Float.valueOf(animation.length())).intValue();
        buf.writeInt(endTick);
        buf.writeInt((int)animation.length());
        if (animation.loopType() == Animation.LoopType.HOLD_ON_LAST_FRAME) {
            LegacyAnimationBinary.putBoolean(buf, true);
            buf.writeInt(endTick);
        } else {
            LegacyAnimationBinary.putBoolean(buf, animation.loopType().shouldPlayAgain(null, animation));
            buf.writeInt((int)animation.loopType().restartFromTick(null, animation) + 1);
        }
        boolean easeBefore = animation.data().get("easeBeforeKeyframe").orElse(animation.data().data().getOrDefault("format", (Object)AnimationFormat.GECKOLIB) == AnimationFormat.GECKOLIB);
        LegacyAnimationBinary.putBoolean(buf, easeBefore);
        LegacyAnimationBinary.putBoolean(buf, false);
        buf.writeByte((int)LegacyAnimationBinary.keyframeSize(version));
        if (version >= 2) {
            buf.writeInt(animation.boneAnimations().size());
            for (Map.Entry<String, BoneAnimation> part : animation.boneAnimations().entrySet()) {
                LegacyAnimationBinary.putString(buf, UniversalAnimLoader.restorePlayerBoneName(part.getKey()));
                LegacyAnimationBinary.writePart(buf, part.getKey(), part.getValue(), version, easeBefore);
            }
        } else {
            LegacyAnimationBinary.writePart(buf, "head", animation.getBone("head"), version, easeBefore);
            LegacyAnimationBinary.writePart(buf, "body", animation.getBone("body"), version, easeBefore);
            LegacyAnimationBinary.writePart(buf, "right_arm", animation.getBone("right_arm"), version, easeBefore);
            LegacyAnimationBinary.writePart(buf, "left_arm", animation.getBone("left_arm"), version, easeBefore);
            LegacyAnimationBinary.writePart(buf, "right_leg", animation.getBone("right_leg"), version, easeBefore);
            LegacyAnimationBinary.writePart(buf, "left_leg", animation.getBone("left_leg"), version, easeBefore);
        }
        NetworkUtils.writeUuid(buf, animation.uuid());
    }

    public static void write(Animation animation, ByteBuf buf) {
        LegacyAnimationBinary.write(animation, buf, LegacyAnimationBinary.getCurrentVersion());
    }

    private static void writePart(ByteBuf buf, String name, BoneAnimation part, int version, boolean easeBefore) {
        if (part == null) {
            int i = 6;
            if (BEND_BONE.test(name)) {
                i += 2;
            }
            if (version >= 3) {
                i += 3;
            }
            while (i > 0) {
                if (version >= 2) {
                    LegacyAnimationBinary.putBoolean(buf, false);
                    buf.writeInt(0);
                } else {
                    buf.writeInt(-1);
                }
                --i;
            }
            return;
        }
        Vec3f def = PlayerAnimatorLoader.getDefaultValues(name);
        boolean isItem = ITEM_BONE.test(name);
        boolean isBody = name.equals("body");
        boolean isCape = name.equals("cape");
        LegacyAnimationBinary.writeKeyframes(buf, part.positionKeyFrames().xKeyframes(), def.x(), version, easeBefore, isBody, isItem || isCape || isBody);
        LegacyAnimationBinary.writeKeyframes(buf, isItem ? part.positionKeyFrames().zKeyframes() : part.positionKeyFrames().yKeyframes(), def.y(), version, easeBefore, isBody, !isBody);
        LegacyAnimationBinary.writeKeyframes(buf, isItem ? part.positionKeyFrames().yKeyframes() : part.positionKeyFrames().zKeyframes(), def.z(), version, easeBefore, isBody, isCape);
        LegacyAnimationBinary.writeKeyframes(buf, part.rotationKeyFrames().xKeyframes(), version, easeBefore, isItem || isCape || isBody);
        LegacyAnimationBinary.writeKeyframes(buf, isItem ? part.rotationKeyFrames().zKeyframes() : part.rotationKeyFrames().yKeyframes(), version, easeBefore, isItem || isBody);
        LegacyAnimationBinary.writeKeyframes(buf, isItem ? part.rotationKeyFrames().yKeyframes() : part.rotationKeyFrames().zKeyframes(), version, easeBefore, isItem || isCape);
        if (BEND_BONE.test(name)) {
            if (version >= 2) {
                LegacyAnimationBinary.putBoolean(buf, false);
                buf.writeInt(0);
            } else {
                buf.writeInt(-1);
            }
            LegacyAnimationBinary.writeKeyframes(buf, part.bendKeyFrames(), version, easeBefore, false);
        }
        if (version >= 3) {
            LegacyAnimationBinary.writeKeyframes(buf, part.scaleKeyFrames().xKeyframes(), version, easeBefore, false);
            LegacyAnimationBinary.writeKeyframes(buf, part.scaleKeyFrames().yKeyframes(), version, easeBefore, false);
            LegacyAnimationBinary.writeKeyframes(buf, part.scaleKeyFrames().zKeyframes(), version, easeBefore, false);
        }
    }

    private static void writeKeyframes(ByteBuf buf, List<Keyframe> part, int version, boolean easeBefore, boolean negate) {
        LegacyAnimationBinary.writeKeyframes(buf, part, 0.0f, version, easeBefore, false, negate);
    }

    private static void writeKeyframes(ByteBuf buf, List<Keyframe> part, float def, int version, boolean easeBefore, boolean div, boolean negate) {
        if (version >= 2) {
            LegacyAnimationBinary.putBoolean(buf, !part.isEmpty());
        }
        int keyframeCount = part.size();
        if (!easeBefore) {
            --keyframeCount;
        }
        buf.writeInt(keyframeCount);
        if (keyframeCount <= 0) {
            return;
        }
        float tickAccumulator = 0.0f;
        for (int i = 0; i < keyframeCount; ++i) {
            EasingType easingToWrite;
            Keyframe move = part.get(i);
            buf.writeInt((int)Math.floor(tickAccumulator += move.length()));
            buf.writeFloat((MolangLoader.MOCHA_ENGINE.eval(move.endValue()) * (float)(negate ? -1 : 1) + def) / (div ? 16.0f : 1.0f));
            List<Object> easingArgsToWrite = Collections.emptyList();
            if (easeBefore) {
                easingToWrite = move.easingType();
                if (move.easingArgs() != null && !move.easingArgs().isEmpty() && !move.easingArgs().getFirst().isEmpty()) {
                    easingArgsToWrite = move.easingArgs().getFirst();
                }
            } else {
                Keyframe nextMove = part.get(i + 1);
                easingToWrite = nextMove.easingType();
                if (nextMove.easingArgs() != null && !nextMove.easingArgs().isEmpty() && !nextMove.easingArgs().getFirst().isEmpty()) {
                    easingArgsToWrite = nextMove.easingArgs().getFirst();
                }
            }
            buf.writeByte((int)easingToWrite.id);
            if (version < 4) continue;
            if (easingArgsToWrite != null && !easingArgsToWrite.isEmpty()) {
                buf.writeFloat(MolangLoader.MOCHA_ENGINE.eval(easingArgsToWrite));
                continue;
            }
            buf.writeFloat(Float.NaN);
        }
    }

    public static Animation read(ByteBuf buf) throws IOException {
        return LegacyAnimationBinary.read(buf, LegacyAnimationBinary.getCurrentVersion());
    }

    public static Animation read(ByteBuf buf, int version) throws IOException {
        ExtraAnimationData data = new ExtraAnimationData();
        int beginTick = buf.readInt();
        data.put("beginTick", Float.valueOf(beginTick));
        int endTick = Math.max(buf.readInt(), beginTick + 1);
        if (endTick <= 0) {
            throw new IOException("endTick must be bigger than 0");
        }
        data.put("endTick", Float.valueOf(endTick));
        int stopTick = buf.readInt();
        boolean isLooped = LegacyAnimationBinary.getBoolean(buf);
        int returnTick = Math.max(0, buf.readInt() - 1);
        Animation.LoopType loopType = Animation.LoopType.PLAY_ONCE;
        if (isLooped) {
            if (returnTick > endTick) {
                throw new IOException("The returnTick has to be a non-negative value smaller than the endTick value");
            }
            loopType = returnTick == 0 ? Animation.LoopType.LOOP : Animation.LoopType.returnToTickLoop(returnTick);
        }
        if (loopType == Animation.LoopType.PLAY_ONCE) {
            endTick = stopTick <= endTick ? endTick + 3 : stopTick;
        }
        boolean easeBefore = LegacyAnimationBinary.getBoolean(buf);
        data.put("easeBeforeKeyframe", easeBefore);
        LegacyAnimationBinary.getBoolean(buf);
        byte keyframeSize = buf.readByte();
        if (keyframeSize <= 0) {
            throw new IOException("keyframe size must be greater than 0, current: " + keyframeSize);
        }
        HashMap<String, BoneAnimation> boneAnimations = new HashMap<String, BoneAnimation>();
        if (version >= 2) {
            int count = buf.readInt();
            for (int i = 0; i < count; ++i) {
                String name2 = UniversalAnimLoader.getCorrectPlayerBoneName(LegacyAnimationBinary.getString(buf));
                boneAnimations.put(name2, LegacyAnimationBinary.readPart(buf, name2, new BoneAnimation(), version, keyframeSize, easeBefore));
            }
        } else {
            boneAnimations.put("head", LegacyAnimationBinary.readPart(buf, "head", new BoneAnimation(), version, keyframeSize, easeBefore));
            boneAnimations.put("body", LegacyAnimationBinary.readPart(buf, "body", new BoneAnimation(), version, keyframeSize, easeBefore));
            boneAnimations.put("right_arm", LegacyAnimationBinary.readPart(buf, "right_arm", new BoneAnimation(), version, keyframeSize, easeBefore));
            boneAnimations.put("left_arm", LegacyAnimationBinary.readPart(buf, "left_arm", new BoneAnimation(), version, keyframeSize, easeBefore));
            boneAnimations.put("right_leg", LegacyAnimationBinary.readPart(buf, "right_leg", new BoneAnimation(), version, keyframeSize, easeBefore));
            boneAnimations.put("left_leg", LegacyAnimationBinary.readPart(buf, "left_leg", new BoneAnimation(), version, keyframeSize, easeBefore));
        }
        boneAnimations.values().removeIf(bone -> !bone.hasKeyframes());
        BoneAnimation body = (BoneAnimation)boneAnimations.get("body");
        if (body != null && !body.bendKeyFrames().isEmpty()) {
            BoneAnimation torso = boneAnimations.computeIfAbsent("torso", name -> new BoneAnimation());
            torso.bendKeyFrames().addAll(body.bendKeyFrames());
            body.bendKeyFrames().clear();
            data.put("applyBendToOtherBones", true);
        }
        data.put("uuid", NetworkUtils.readUuid(buf));
        data.put("format", (Object)AnimationFormat.PLAYER_ANIMATOR);
        return new Animation(data, endTick, loopType, boneAnimations, UniversalAnimLoader.NO_KEYFRAMES, new HashMap<String, Vec3f>(), new HashMap<String, String>());
    }

    private static BoneAnimation readPart(ByteBuf buf, String name, BoneAnimation part, int version, int keyframeSize, boolean easeBefore) {
        Vec3f def = PlayerAnimatorLoader.getDefaultValues(name);
        boolean isBody = name.equals("body");
        boolean isItem = ITEM_BONE.test(name);
        boolean isCape = name.equals("cape");
        LegacyAnimationBinary.readKeyframes(buf, part.positionKeyFrames().xKeyframes(), def.x(), version, keyframeSize, isBody, isItem || isCape || isBody, easeBefore, PlayerAnimatorLoader.ZERO);
        LegacyAnimationBinary.readKeyframes(buf, part.positionKeyFrames().yKeyframes(), def.y(), version, keyframeSize, isBody, !isBody, easeBefore, PlayerAnimatorLoader.ZERO);
        LegacyAnimationBinary.readKeyframes(buf, part.positionKeyFrames().zKeyframes(), def.z(), version, keyframeSize, isBody, isCape, easeBefore, PlayerAnimatorLoader.ZERO);
        LegacyAnimationBinary.readKeyframes(buf, part.rotationKeyFrames().xKeyframes(), version, keyframeSize, isItem || isCape || isBody, easeBefore, PlayerAnimatorLoader.ZERO);
        LegacyAnimationBinary.readKeyframes(buf, part.rotationKeyFrames().yKeyframes(), version, keyframeSize, isItem || isBody, easeBefore, PlayerAnimatorLoader.ZERO);
        LegacyAnimationBinary.readKeyframes(buf, part.rotationKeyFrames().zKeyframes(), version, keyframeSize, isItem || isCape, easeBefore, PlayerAnimatorLoader.ZERO);
        if (BEND_BONE.test(name)) {
            LegacyAnimationBinary.readKeyframes(buf, new ArrayList<Keyframe>(), version, keyframeSize, false, easeBefore, PlayerAnimatorLoader.ZERO);
            LegacyAnimationBinary.readKeyframes(buf, part.bendKeyFrames(), version, keyframeSize, false, easeBefore, PlayerAnimatorLoader.ZERO);
        }
        if (version >= 3) {
            LegacyAnimationBinary.readKeyframes(buf, part.scaleKeyFrames().xKeyframes(), version, keyframeSize, false, easeBefore, PlayerAnimatorLoader.ONE);
            LegacyAnimationBinary.readKeyframes(buf, part.scaleKeyFrames().yKeyframes(), version, keyframeSize, false, easeBefore, PlayerAnimatorLoader.ONE);
            LegacyAnimationBinary.readKeyframes(buf, part.scaleKeyFrames().zKeyframes(), version, keyframeSize, false, easeBefore, PlayerAnimatorLoader.ONE);
        }
        if (!easeBefore) {
            PlayerAnimatorLoader.correctEasings(part.positionKeyFrames());
            PlayerAnimatorLoader.correctEasings(part.rotationKeyFrames());
            PlayerAnimatorLoader.correctEasings(part.scaleKeyFrames());
            PlayerAnimatorLoader.correctEasings(part.bendKeyFrames());
        }
        if (isItem) {
            PlayerAnimatorLoader.swapTheZYAxis(part.positionKeyFrames());
            PlayerAnimatorLoader.swapTheZYAxis(part.rotationKeyFrames());
        }
        return part;
    }

    private static void readKeyframes(ByteBuf buf, List<Keyframe> part, int version, int keyframeSize, boolean negate, boolean easeBefore, List<Expression> fallback) {
        LegacyAnimationBinary.readKeyframes(buf, part, 0.0f, version, keyframeSize, false, negate, easeBefore, fallback);
    }

    private static void readKeyframes(ByteBuf buf, List<Keyframe> part, float def, int version, int keyframeSize, boolean mul, boolean negate, boolean easeBefore, List<Expression> fallback) {
        int length;
        boolean enabled;
        if (version >= 2) {
            enabled = LegacyAnimationBinary.getBoolean(buf);
            length = buf.readInt();
        } else {
            length = buf.readInt();
            boolean bl = enabled = length >= 0;
        }
        if (!enabled) {
            if (length > 0) {
                buf.readerIndex(buf.readerIndex() + length * keyframeSize);
            }
            part.clear();
            return;
        }
        int lastTick = 0;
        for (int i = 0; i < length; ++i) {
            Keyframe prevKeyframe = part.isEmpty() ? null : part.getLast();
            int currentPos = buf.readerIndex();
            int tick = buf.readInt();
            float keyframeLength = (float)tick - (float)lastTick;
            lastTick = tick;
            List<FloatExpression> expression = Collections.singletonList(FloatExpression.of((float)((buf.readFloat() - def) * (float)(mul ? 16 : 1) * (float)(negate ? -1 : 1))));
            EasingType easingType = EasingType.fromId(buf.readByte());
            Float easingArg = null;
            if (version >= 4 && Float.isNaN((easingArg = Float.valueOf(buf.readFloat())).floatValue())) {
                easingArg = null;
            }
            List<Expression> startValue = prevKeyframe != null ? prevKeyframe.endValue() : (easeBefore ? expression : fallback);
            part.add(new Keyframe(keyframeLength, startValue, expression, easingType, easingArg == null ? Collections.singletonList(Collections.emptyList()) : Collections.singletonList(Collections.singletonList(FloatExpression.of((float)easingArg.floatValue())))));
            buf.readerIndex(currentPos + keyframeSize);
        }
    }

    public static int getCurrentVersion() {
        return 4;
    }

    public static int calculateSize(Animation animation) {
        return LegacyAnimationBinary.calculateSize(animation, LegacyAnimationBinary.getCurrentVersion());
    }

    public static int calculateSize(Animation animation, int version) {
        int size = 36;
        boolean easeBefore = animation.data().get("easeBeforeKeyframe").orElse(animation.data().data().getOrDefault("format", (Object)AnimationFormat.GECKOLIB) == AnimationFormat.GECKOLIB);
        if (version < 2) {
            size += LegacyAnimationBinary.partSize(animation.getBone("head"), false, version, easeBefore);
            size += LegacyAnimationBinary.partSize(animation.getBone("body"), true, version, easeBefore);
            size += LegacyAnimationBinary.partSize(animation.getBone("right_arm"), true, version, easeBefore);
            size += LegacyAnimationBinary.partSize(animation.getBone("left_arm"), true, version, easeBefore);
            size += LegacyAnimationBinary.partSize(animation.getBone("right_leg"), true, version, easeBefore);
            size += LegacyAnimationBinary.partSize(animation.getBone("left_leg"), true, version, easeBefore);
        } else {
            size += 4;
            for (Map.Entry<String, BoneAnimation> entry : animation.boneAnimations().entrySet()) {
                size += LegacyAnimationBinary.stringSize(UniversalAnimLoader.restorePlayerBoneName(entry.getKey())) + LegacyAnimationBinary.partSize(entry.getValue(), BEND_BONE.test(entry.getKey()), version, easeBefore);
            }
        }
        return size;
    }

    private static int stringSize(String str) {
        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
        return bytes.length + 4;
    }

    private static int partSize(BoneAnimation part, boolean bendable, int version, boolean easeBefore) {
        if (part == null) {
            int i = 6;
            if (bendable) {
                i += 2;
            }
            if (version >= 3) {
                i += 3;
            }
            return i * (version >= 2 ? 5 : 4);
        }
        int size = 0;
        size += LegacyAnimationBinary.axisSize(part.positionKeyFrames().xKeyframes(), version, easeBefore);
        size += LegacyAnimationBinary.axisSize(part.positionKeyFrames().yKeyframes(), version, easeBefore);
        size += LegacyAnimationBinary.axisSize(part.positionKeyFrames().zKeyframes(), version, easeBefore);
        size += LegacyAnimationBinary.axisSize(part.rotationKeyFrames().xKeyframes(), version, easeBefore);
        size += LegacyAnimationBinary.axisSize(part.rotationKeyFrames().yKeyframes(), version, easeBefore);
        size += LegacyAnimationBinary.axisSize(part.rotationKeyFrames().zKeyframes(), version, easeBefore);
        if (bendable) {
            size += version >= 2 ? 5 : 4;
            size += LegacyAnimationBinary.axisSize(part.bendKeyFrames(), version, easeBefore);
        }
        if (version >= 3) {
            size += LegacyAnimationBinary.axisSize(part.scaleKeyFrames().xKeyframes(), version, easeBefore);
            size += LegacyAnimationBinary.axisSize(part.scaleKeyFrames().yKeyframes(), version, easeBefore);
            size += LegacyAnimationBinary.axisSize(part.scaleKeyFrames().zKeyframes(), version, easeBefore);
        }
        return size;
    }

    private static int axisSize(List<Keyframe> axis, int version, boolean easeBefore) {
        return Math.max(0, axis.size() - (easeBefore ? 0 : 1)) * LegacyAnimationBinary.keyframeSize(version) + (version >= 2 ? 5 : 4);
    }

    private static byte keyframeSize(int version) {
        return version < 4 ? (byte)9 : 13;
    }

    public static void putBoolean(ByteBuf byteBuffer, boolean bl) {
        byteBuffer.writeByte((int)((byte)(bl ? 1 : 0)));
    }

    public static boolean getBoolean(ByteBuf buf) {
        return buf.readByte() != 0;
    }

    public static void putString(ByteBuf buf, String str) {
        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
        buf.writeInt(bytes.length);
        buf.writeBytes(bytes);
    }

    public static String getString(ByteBuf buf) {
        int len = buf.readInt();
        byte[] bytes = new byte[len];
        buf.readBytes(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }
}


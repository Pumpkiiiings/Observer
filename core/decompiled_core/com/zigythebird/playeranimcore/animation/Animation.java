/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonPrimitive
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.zigythebird.playeranimcore.animation;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.zigythebird.playeranimcore.animation.AnimationController;
import com.zigythebird.playeranimcore.animation.ExtraAnimationData;
import com.zigythebird.playeranimcore.animation.keyframe.BoneAnimation;
import com.zigythebird.playeranimcore.animation.keyframe.event.data.CustomInstructionKeyframeData;
import com.zigythebird.playeranimcore.animation.keyframe.event.data.ParticleKeyframeData;
import com.zigythebird.playeranimcore.animation.keyframe.event.data.SoundKeyframeData;
import com.zigythebird.playeranimcore.loading.UniversalAnimLoader;
import com.zigythebird.playeranimcore.math.Vec3f;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record Animation(ExtraAnimationData data, float length, LoopType loopType, Map<String, BoneAnimation> boneAnimations, Keyframes keyFrames, Map<String, Vec3f> bones, Map<String, String> parents) implements Supplier<UUID>
{
    static Animation generateWaitAnimation(float length) {
        return new Animation(new ExtraAnimationData("name", "internal.wait"), length, LoopType.PLAY_ONCE, Collections.emptyMap(), UniversalAnimLoader.NO_KEYFRAMES, new HashMap<String, Vec3f>(), new HashMap<String, String>());
    }

    public boolean isPlayingAt(float tick) {
        return this.loopType.shouldPlayAgain(null, this) || tick < this.length() && tick > 0.0f;
    }

    @Nullable
    public BoneAnimation getBone(String id) {
        return this.boneAnimations.get(id);
    }

    public Optional<BoneAnimation> getBoneOptional(String id) {
        return Optional.ofNullable(this.getBone(id));
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Animation)) {
            return false;
        }
        Animation animation = (Animation)o;
        return Float.compare(this.length, animation.length) == 0 && Objects.equals(this.keyFrames, animation.keyFrames) && Objects.equals(this.bones, animation.bones) && Objects.equals(this.parents, animation.parents) && Objects.equals(this.boneAnimations, animation.boneAnimations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Float.valueOf(this.length), this.boneAnimations, this.keyFrames, this.bones, this.parents);
    }

    private UUID generateUuid() {
        return Animation.generateUuid(Float.floatToIntBits(this.length), this.boneAnimations.hashCode(), this.keyFrames.hashCode(), this.bones.hashCode(), this.parents.hashCode());
    }

    private static UUID generateUuid(int ... hashes) {
        long mostSigBits = 17L;
        long leastSigBits = 31L;
        for (int hash : hashes) {
            mostSigBits = 31L * mostSigBits + (long)hash;
            leastSigBits = 37L * leastSigBits + (long)hash;
        }
        return new UUID(mostSigBits, leastSigBits);
    }

    public UUID uuid() {
        if (!this.data().has("uuid")) {
            this.data().put("uuid", this.generateUuid());
        } else {
            Object object = this.data().getRaw("uuid");
            if (object instanceof String) {
                String str = (String)object;
                this.data().put("uuid", UUID.fromString(str));
            }
        }
        return (UUID)this.data().get("uuid").orElseThrow();
    }

    @Override
    @NotNull
    public String toString() {
        return "Animation{data=" + String.valueOf(this.data) + ", length=" + this.length + "}";
    }

    @Override
    public UUID get() {
        return this.uuid();
    }

    @NotNull
    public String getNameOrId() {
        return Objects.requireNonNullElseGet(this.data().name(), () -> this.uuid().toString());
    }

    @FunctionalInterface
    public static interface LoopType {
        public static final Map<String, LoopType> LOOP_TYPES = new ConcurrentHashMap<String, LoopType>(4);
        public static final LoopType DEFAULT = new LoopType(){

            @Override
            public boolean shouldPlayAgain(@Nullable AnimationController controller, Animation currentAnimation) {
                return currentAnimation.loopType().shouldPlayAgain(controller, currentAnimation);
            }

            @Override
            public float restartFromTick(@Nullable AnimationController controller, Animation currentAnimation) {
                return currentAnimation.loopType().restartFromTick(controller, currentAnimation);
            }
        };
        public static final LoopType PLAY_ONCE = LoopType.register("play_once", LoopType.register("false", (controller, currentAnimation) -> false));
        public static final LoopType HOLD_ON_LAST_FRAME = LoopType.register("hold_on_last_frame", (controller, currentAnimation) -> {
            if (controller != null) {
                controller.pause();
            }
            return true;
        });
        public static final LoopType LOOP = LoopType.register("loop", LoopType.register("true", (controller, currentAnimation) -> true));

        public boolean shouldPlayAgain(@Nullable AnimationController var1, Animation var2);

        default public float restartFromTick(@Nullable AnimationController controller, Animation currentAnimation) {
            return 0.0f;
        }

        public static LoopType returnToTickLoop(final float tick) {
            return new LoopType(){

                @Override
                public boolean shouldPlayAgain(@Nullable AnimationController controller, Animation currentAnimation) {
                    return true;
                }

                @Override
                public float restartFromTick(@Nullable AnimationController controller, Animation currentAnimation) {
                    return tick;
                }
            };
        }

        public static LoopType fromJson(JsonElement json) {
            if (json == null || !json.isJsonPrimitive()) {
                return PLAY_ONCE;
            }
            JsonPrimitive primitive = json.getAsJsonPrimitive();
            if (primitive.isBoolean()) {
                return primitive.getAsBoolean() ? LOOP : PLAY_ONCE;
            }
            if (primitive.isString()) {
                return LoopType.fromString(primitive.getAsString());
            }
            return PLAY_ONCE;
        }

        public static LoopType fromString(String name) {
            return LOOP_TYPES.getOrDefault(name, PLAY_ONCE);
        }

        public static LoopType register(String name, LoopType loopType) {
            LOOP_TYPES.put(name, loopType);
            return loopType;
        }
    }

    public record Keyframes(SoundKeyframeData[] sounds, ParticleKeyframeData[] particles, CustomInstructionKeyframeData[] customInstructions) {
        @Override
        public int hashCode() {
            return Objects.hash(Arrays.hashCode(this.sounds), Arrays.hashCode(this.particles), Arrays.hashCode(this.customInstructions));
        }
    }
}


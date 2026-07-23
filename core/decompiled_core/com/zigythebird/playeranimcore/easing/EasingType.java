/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonPrimitive
 *  it.unimi.dsi.fastutil.floats.Float2FloatFunction
 *  org.jetbrains.annotations.Nullable
 *  team.unnamed.mocha.MochaEngine
 *  team.unnamed.mocha.parser.ast.Expression
 *  team.unnamed.mocha.runtime.standard.MochaMath
 */
package com.zigythebird.playeranimcore.easing;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.zigythebird.playeranimcore.easing.BezierEasing;
import com.zigythebird.playeranimcore.easing.CatmullRomEasing;
import com.zigythebird.playeranimcore.easing.EasingTypeTransformer;
import com.zigythebird.playeranimcore.math.MathHelper;
import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.Nullable;
import team.unnamed.mocha.MochaEngine;
import team.unnamed.mocha.parser.ast.Expression;
import team.unnamed.mocha.runtime.standard.MochaMath;

public enum EasingType implements EasingTypeTransformer
{
    LINEAR(0, "linear", value -> EasingType.easeIn(EasingType::linear)),
    CONSTANT(1, "constant", value -> value1 -> 0.0f),
    STEP(37, "step", value -> EasingType.easeIn(EasingType.step(value))),
    EASE_IN_SINE(6, "easeinsine", value -> EasingType.easeIn(EasingType::sine)),
    EASE_OUT_SINE(7, "easeoutsine", value -> EasingType.easeOut(EasingType::sine)),
    EASE_IN_OUT_SINE(8, "easeinoutsine", value -> EasingType.easeInOut(EasingType::sine)),
    EASE_IN_QUAD(12, "easeinquad", value -> EasingType.easeIn(EasingType::quadratic)),
    EASE_OUT_QUAD(13, "easeoutquad", value -> EasingType.easeOut(EasingType::quadratic)),
    EASE_IN_OUT_QUAD(14, "easeinoutquad", value -> EasingType.easeInOut(EasingType::quadratic)),
    EASE_IN_CUBIC(9, "easeincubic", value -> EasingType.easeIn(EasingType::cubic)),
    EASE_OUT_CUBIC(10, "easeoutcubic", value -> EasingType.easeOut(EasingType::cubic)),
    EASE_IN_OUT_CUBIC(11, "easeinoutcubic", value -> EasingType.easeInOut(EasingType::cubic)),
    EASE_IN_QUART(15, "easeinquart", value -> EasingType.easeIn(EasingType.pow(4.0f))),
    EASE_OUT_QUART(16, "easeoutquart", value -> EasingType.easeOut(EasingType.pow(4.0f))),
    EASE_IN_OUT_QUART(17, "easeinoutquart", value -> EasingType.easeInOut(EasingType.pow(4.0f))),
    EASE_IN_QUINT(18, "easeinquint", value -> EasingType.easeIn(EasingType.pow(5.0f))),
    EASE_OUT_QUINT(19, "easeoutquint", value -> EasingType.easeOut(EasingType.pow(5.0f))),
    EASE_IN_OUT_QUINT(20, "easeinoutquint", value -> EasingType.easeInOut(EasingType.pow(5.0f))),
    EASE_IN_EXPO(21, "easeinexpo", value -> EasingType.easeIn(EasingType::exp)),
    EASE_OUT_EXPO(22, "easeoutexpo", value -> EasingType.easeOut(EasingType::exp)),
    EASE_IN_OUT_EXPO(23, "easeinoutexpo", value -> EasingType.easeInOut(EasingType::exp)),
    EASE_IN_CIRC(24, "easeincirc", value -> EasingType.easeIn(EasingType::circle)),
    EASE_OUT_CIRC(25, "easeoutcirc", value -> EasingType.easeOut(EasingType::circle)),
    EASE_IN_OUT_CIRC(26, "easeinoutcirc", value -> EasingType.easeInOut(EasingType::circle)),
    EASE_IN_BACK(27, "easeinback", value -> EasingType.easeIn(EasingType.back(value))),
    EASE_OUT_BACK(28, "easeoutback", value -> EasingType.easeOut(EasingType.back(value))),
    EASE_IN_OUT_BACK(29, "easeinoutback", value -> EasingType.easeInOut(EasingType.back(value))),
    EASE_IN_ELASTIC(30, "easeinelastic", value -> EasingType.easeIn(EasingType.elastic(value))),
    EASE_OUT_ELASTIC(31, "easeoutelastic", value -> EasingType.easeOut(EasingType.elastic(value))),
    EASE_IN_OUT_ELASTIC(32, "easeinoutelastic", value -> EasingType.easeInOut(EasingType.elastic(value))),
    EASE_IN_BOUNCE(33, "easeinbounce", value -> EasingType.easeIn(EasingType.bounce(value))),
    EASE_OUT_BOUNCE(34, "easeoutbounce", value -> EasingType.easeOut(EasingType.bounce(value))),
    EASE_IN_OUT_BOUNCE(35, "easeinoutbounce", value -> EasingType.easeInOut(EasingType.bounce(value))),
    CATMULLROM(36, "catmullrom", new CatmullRomEasing()),
    BEZIER(38, "bezier", new BezierEasing());

    public final byte id;
    public final String name;
    private final EasingTypeTransformer transformer;
    private static final Map<String, EasingType> BY_NAME;
    private static final Map<Byte, EasingType> BY_ID;

    private EasingType(int id, String name, EasingTypeTransformer transformer) {
        this.id = (byte)id;
        this.name = name;
        this.transformer = transformer;
    }

    @Override
    public Float2FloatFunction buildTransformer(@Nullable Float value) {
        return this.transformer.buildTransformer(value);
    }

    @Override
    public float apply(MochaEngine<?> env, float startValue, float endValue, float transitionLength, float lerpValue, @Nullable List<List<Expression>> easingArgs) {
        return this.transformer.apply(env, startValue, endValue, transitionLength, lerpValue, easingArgs);
    }

    public static float lerpWithOverride(MochaEngine<?> env, float startValue, float endValue, float transitionLength, float lerpValue, @Nullable List<List<Expression>> easingArgs, EasingType easingType, @Nullable EasingType override) {
        EasingType easing = override != null ? override : easingType;
        return easing.apply(env, startValue, endValue, transitionLength, lerpValue, easingArgs);
    }

    @Override
    public float apply(float startValue, float endValue, float lerpValue) {
        return this.transformer.apply(startValue, endValue, lerpValue);
    }

    @Override
    public float apply(float startValue, float endValue, @Nullable Float easingValue, float lerpValue) {
        return this.transformer.apply(startValue, endValue, lerpValue);
    }

    public static EasingType fromJson(JsonElement json) {
        JsonPrimitive primitive;
        if (!(json instanceof JsonPrimitive) || !(primitive = (JsonPrimitive)json).isString()) {
            return LINEAR;
        }
        return EasingType.fromString(primitive.getAsString().toLowerCase(Locale.ROOT));
    }

    public static EasingType fromString(String name) {
        return BY_NAME.getOrDefault(name.toLowerCase(Locale.ROOT), LINEAR);
    }

    public static Float2FloatFunction easeIn(Float2FloatFunction function) {
        return function;
    }

    public static Float2FloatFunction easeOut(Float2FloatFunction function) {
        return time -> 1.0f - ((Float)function.apply((Object)Float.valueOf(1.0f - time))).floatValue();
    }

    public static Float2FloatFunction easeInOut(Float2FloatFunction function) {
        return time -> {
            if ((double)time < 0.5) {
                return ((Float)function.apply((Object)Float.valueOf(time * 2.0f))).floatValue() / 2.0f;
            }
            return 1.0f - ((Float)function.apply((Object)Float.valueOf((1.0f - time) * 2.0f))).floatValue() / 2.0f;
        };
    }

    public static float linear(float n) {
        return n;
    }

    public static float quadratic(float n) {
        return n * n;
    }

    public static float cubic(float n) {
        return n * n * n;
    }

    public static float sine(float n) {
        return 1.0f - MathHelper.cos(n * (float)Math.PI / 2.0f);
    }

    public static float circle(float n) {
        return 1.0f - MochaMath.sqrt((float)(1.0f - n * n));
    }

    public static float exp(float n) {
        return MochaMath.pow((float)2.0f, (float)(10.0f * (n - 1.0f)));
    }

    public static Float2FloatFunction elastic(Float n) {
        float n2 = n == null ? 1.0f : n.floatValue();
        return t -> 1.0f - MochaMath.pow((float)MathHelper.cos(t * (float)Math.PI / 2.0f), (float)3.0f) * MathHelper.cos(t * n2 * (float)Math.PI);
    }

    public static Float2FloatFunction bounce(Float n) {
        float n2 = n == null ? 0.5f : n.floatValue();
        Float2FloatFunction one = x -> 7.5625f * x * x;
        Float2FloatFunction two = x -> 30.25f * n2 * MochaMath.pow((float)(x - 0.54545456f), (float)2.0f) + 1.0f - n2;
        Float2FloatFunction three = x -> 121.0f * n2 * n2 * MochaMath.pow((float)(x - 0.8181818f), (float)2.0f) + 1.0f - n2 * n2;
        Float2FloatFunction four = x -> 484.0f * n2 * n2 * n2 * MochaMath.pow((float)(x - 0.95454544f), (float)2.0f) + 1.0f - n2 * n2 * n2;
        return t -> Math.min(Math.min(((Float)one.apply((Object)Float.valueOf(t))).floatValue(), ((Float)two.apply((Object)Float.valueOf(t))).floatValue()), Math.min(((Float)three.apply((Object)Float.valueOf(t))).floatValue(), ((Float)four.apply((Object)Float.valueOf(t))).floatValue()));
    }

    public static Float2FloatFunction back(Float n) {
        float n2 = n == null ? 1.70158f : n.floatValue() * 1.70158f;
        return t -> t * t * ((n2 + 1.0f) * t - n2);
    }

    public static Float2FloatFunction pow(float n) {
        return t -> MochaMath.pow((float)t, (float)n);
    }

    public static Float2FloatFunction step(Float n) {
        float n2;
        float f = n2 = n == null ? 2.0f : n.floatValue();
        if (n2 < 2.0f) {
            throw new IllegalArgumentException("Steps must be >= 2, got: " + n2);
        }
        int steps = (int)n2;
        return t -> {
            float f;
            float result = 0.0f;
            if (t < 0.0f) {
                return result;
            }
            float stepLength = 1.0f / (float)steps;
            result = (float)(steps - 1) * stepLength;
            if (t > f) {
                return result;
            }
            int leftBorderIndex = 0;
            int rightBorderIndex = steps - 1;
            while (rightBorderIndex - leftBorderIndex != 1) {
                int testIndex = leftBorderIndex + (rightBorderIndex - leftBorderIndex) / 2;
                if (t >= (float)testIndex * stepLength) {
                    leftBorderIndex = testIndex;
                    continue;
                }
                rightBorderIndex = testIndex;
            }
            return (float)leftBorderIndex * stepLength;
        };
    }

    public static EasingType fromId(byte id) {
        return BY_ID.getOrDefault(id, LINEAR);
    }

    static {
        BY_NAME = new ConcurrentHashMap<String, EasingType>(64);
        BY_ID = new ConcurrentHashMap<Byte, EasingType>(64);
        for (EasingType type : EasingType.values()) {
            BY_NAME.putIfAbsent(type.name.toLowerCase(Locale.ROOT), type);
            BY_ID.putIfAbsent(type.id, type);
        }
    }
}


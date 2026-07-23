/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.floats.Float2FloatFunction
 *  org.jetbrains.annotations.Nullable
 *  team.unnamed.mocha.MochaEngine
 *  team.unnamed.mocha.parser.ast.Expression
 *  team.unnamed.mocha.runtime.standard.MochaMath
 */
package com.zigythebird.playeranimcore.easing;

import com.zigythebird.playeranimcore.easing.EasingType;
import com.zigythebird.playeranimcore.easing.EasingTypeTransformer;
import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import java.util.List;
import org.jetbrains.annotations.Nullable;
import team.unnamed.mocha.MochaEngine;
import team.unnamed.mocha.parser.ast.Expression;
import team.unnamed.mocha.runtime.standard.MochaMath;

public class CatmullRomEasing
implements EasingTypeTransformer {
    public static float getPointOnSpline(float delta, float p0, float p1, float p2, float p3) {
        return 0.5f * (2.0f * p1 + (p2 - p0) * delta + (2.0f * p0 - 5.0f * p1 + 4.0f * p2 - p3) * delta * delta + (3.0f * p1 - p0 - 3.0f * p2 + p3) * delta * delta * delta);
    }

    @Override
    public Float2FloatFunction buildTransformer(Float value) {
        return EasingType.easeIn(EasingType::linear);
    }

    @Override
    public float apply(MochaEngine<?> env, float startValue, float endValue, float transitionLength, float lerpValue, @Nullable List<List<Expression>> easingArgs) {
        if (lerpValue >= 1.0f) {
            return endValue;
        }
        if (Float.isNaN(lerpValue)) {
            return startValue;
        }
        if (easingArgs == null || easingArgs.size() < 2) {
            return MochaMath.lerp((float)startValue, (float)endValue, (float)((Float)this.buildTransformer(null).apply((Object)Float.valueOf(lerpValue))).floatValue());
        }
        return CatmullRomEasing.getPointOnSpline(lerpValue, env.eval(easingArgs.get(0)), startValue, endValue, env.eval(easingArgs.get(1)));
    }
}


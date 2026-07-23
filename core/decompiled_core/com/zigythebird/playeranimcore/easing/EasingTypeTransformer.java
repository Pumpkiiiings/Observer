/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.floats.Float2FloatFunction
 *  org.jetbrains.annotations.Nullable
 *  team.unnamed.mocha.MochaEngine
 *  team.unnamed.mocha.parser.ast.Expression
 *  team.unnamed.mocha.runtime.standard.MochaMath
 *  team.unnamed.mocha.runtime.value.ObjectValue$FloatFunction3
 */
package com.zigythebird.playeranimcore.easing;

import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import java.util.List;
import org.jetbrains.annotations.Nullable;
import team.unnamed.mocha.MochaEngine;
import team.unnamed.mocha.parser.ast.Expression;
import team.unnamed.mocha.runtime.standard.MochaMath;
import team.unnamed.mocha.runtime.value.ObjectValue;

@FunctionalInterface
public interface EasingTypeTransformer
extends ObjectValue.FloatFunction3 {
    public Float2FloatFunction buildTransformer(@Nullable Float var1);

    default public float apply(MochaEngine<?> env, float startValue, float endValue, float transitionLength, float lerpValue, @Nullable List<List<Expression>> easingArgs) {
        if (lerpValue >= 1.0f) {
            return endValue;
        }
        if (Float.isNaN(lerpValue)) {
            return startValue;
        }
        Float easingVariable = null;
        if (easingArgs != null && !easingArgs.isEmpty()) {
            easingVariable = Float.valueOf(env.eval(easingArgs.getFirst()));
        }
        return this.apply(startValue, endValue, easingVariable, lerpValue);
    }

    default public float apply(float startValue, float endValue, float lerpValue) {
        return this.apply(startValue, endValue, null, lerpValue);
    }

    default public float apply(float startValue, float endValue, @Nullable Float easingValue, float lerpValue) {
        return MochaMath.lerp((float)startValue, (float)endValue, (float)((Float)this.buildTransformer(easingValue).apply((Object)Float.valueOf(lerpValue))).floatValue());
    }
}


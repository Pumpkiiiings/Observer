/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.floats.Float2FloatFunction
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Vector2f
 *  team.unnamed.mocha.MochaEngine
 *  team.unnamed.mocha.parser.ast.Expression
 *  team.unnamed.mocha.runtime.standard.MochaMath
 */
package com.zigythebird.playeranimcore.easing;

import com.zigythebird.playeranimcore.easing.EasingType;
import com.zigythebird.playeranimcore.easing.EasingTypeTransformer;
import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2f;
import team.unnamed.mocha.MochaEngine;
import team.unnamed.mocha.parser.ast.Expression;
import team.unnamed.mocha.runtime.standard.MochaMath;

public class BezierEasing
implements EasingTypeTransformer {
    @Override
    public Float2FloatFunction buildTransformer(@Nullable Float value) {
        return EasingType.easeIn(EasingType::linear);
    }

    @Override
    public float apply(MochaEngine<?> env, float startValue, float endValue, float transitionLength, float lerpValue, @Nullable List<List<Expression>> easingArgs) {
        Vector2f point;
        float unclamped;
        float rightTime;
        float rightValue;
        if (lerpValue >= 1.0f) {
            return endValue;
        }
        if (Float.isNaN(lerpValue) || lerpValue == 0.0f) {
            return startValue;
        }
        if (easingArgs == null || easingArgs.isEmpty()) {
            return MochaMath.lerp((float)startValue, (float)endValue, (float)((Float)this.buildTransformer(null).apply((Object)Float.valueOf(lerpValue))).floatValue());
        }
        float leftValue = env.eval(easingArgs.getFirst());
        float leftTime = env.eval(easingArgs.get(1));
        if (easingArgs.size() > 3) {
            rightValue = env.eval(easingArgs.get(2));
            rightTime = env.eval(easingArgs.get(3));
        } else {
            rightValue = 0.0f;
            rightTime = 0.1f;
        }
        float time_handle_before = rightTime / (transitionLength /= 20.0f);
        float time_handle_after = leftTime / transitionLength;
        if (time_handle_before > 1.0f || time_handle_before < 0.0f) {
            unclamped = time_handle_before;
            time_handle_before = Math.clamp(time_handle_before, 0.0f, 1.0f);
            rightValue /= 1.0f + Math.abs(time_handle_before - unclamped);
        }
        if (time_handle_after > 0.0f || time_handle_after < -1.0f) {
            unclamped = time_handle_after;
            time_handle_after = Math.clamp(time_handle_after, -1.0f, 0.0f);
            leftValue /= 1.0f + Math.abs(time_handle_after - unclamped);
        }
        Vector2f P0 = new Vector2f(0.0f, startValue);
        Vector2f P1 = new Vector2f(time_handle_before, startValue + rightValue);
        Vector2f P2 = new Vector2f(time_handle_after + 1.0f, endValue + leftValue);
        Vector2f P3 = new Vector2f(1.0f, endValue);
        ArrayList<Vector2f> points = new ArrayList<Vector2f>();
        int divisions = 200;
        for (int d = 0; d <= 200; ++d) {
            float t = (float)d / 200.0f;
            points.add(new Vector2f(this.CubicBezier(t, P0.x, P1.x, P2.x, P3.x), this.CubicBezier(t, P0.y, P1.y, P2.y, P3.y)));
        }
        Vector2f closest = new Vector2f();
        float closest_diff = Float.POSITIVE_INFINITY;
        for (Vector2f point2 : points) {
            float diff = Math.abs(point2.x - lerpValue);
            if (!(diff < closest_diff)) continue;
            closest_diff = diff;
            closest = point2;
        }
        Vector2f second_closest = new Vector2f();
        closest_diff = Float.POSITIVE_INFINITY;
        Iterator iterator = points.iterator();
        while (iterator.hasNext() && (point = (Vector2f)iterator.next()) != closest) {
            float diff = Math.abs(point.x - lerpValue);
            if (!(diff < closest_diff)) continue;
            closest_diff = diff;
            second_closest = point;
        }
        return MochaMath.lerp((float)closest.y, (float)second_closest.y, (float)Math.clamp(MochaMath.lerp((float)closest.x, (float)second_closest.x, (float)lerpValue), 0.0f, 1.0f));
    }

    float CubicBezierP0(float t, float p) {
        float k = 1.0f - t;
        return k * k * k * p;
    }

    float CubicBezierP1(float t, float p) {
        float k = 1.0f - t;
        return 3.0f * k * k * t * p;
    }

    float CubicBezierP2(float t, float p) {
        return 3.0f * (1.0f - t) * t * t * p;
    }

    float CubicBezierP3(float t, float p) {
        return t * t * t * p;
    }

    float CubicBezier(float t, float p0, float p1, float p2, float p3) {
        return this.CubicBezierP0(t, p0) + this.CubicBezierP1(t, p1) + this.CubicBezierP2(t, p2) + this.CubicBezierP3(t, p3);
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 */
package com.zigythebird.playeranimcore.animation.keyframe;

import com.zigythebird.playeranimcore.animation.keyframe.Keyframe;
import com.zigythebird.playeranimcore.enums.Axis;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.Objects;

public record KeyframeStack(List<Keyframe> xKeyframes, List<Keyframe> yKeyframes, List<Keyframe> zKeyframes) {
    public KeyframeStack() {
        this((List<Keyframe>)new ObjectArrayList(), (List<Keyframe>)new ObjectArrayList(), (List<Keyframe>)new ObjectArrayList());
    }

    public static KeyframeStack from(KeyframeStack otherStack) {
        return new KeyframeStack(otherStack.xKeyframes, otherStack.yKeyframes, otherStack.zKeyframes);
    }

    public float getLastKeyframeTime() {
        return Math.max(this.getLastXAxisKeyframeTime(), Math.max(this.getLastYAxisKeyframeTime(), this.getLastZAxisKeyframeTime()));
    }

    public float getLastXAxisKeyframeTime() {
        return Keyframe.getLastKeyframeTime(this.xKeyframes);
    }

    public float getLastYAxisKeyframeTime() {
        return Keyframe.getLastKeyframeTime(this.yKeyframes);
    }

    public float getLastZAxisKeyframeTime() {
        return Keyframe.getLastKeyframeTime(this.zKeyframes);
    }

    public List<Keyframe> getKeyFramesForAxis(Axis axis) {
        return switch (axis) {
            default -> throw new MatchException(null, null);
            case Axis.X -> this.xKeyframes();
            case Axis.Y -> this.yKeyframes();
            case Axis.Z -> this.zKeyframes();
        };
    }

    public boolean hasKeyframes() {
        return !this.xKeyframes().isEmpty() || !this.yKeyframes().isEmpty() || !this.zKeyframes().isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof KeyframeStack)) {
            return false;
        }
        KeyframeStack that = (KeyframeStack)o;
        return Objects.equals(this.xKeyframes, that.xKeyframes) && Objects.equals(this.yKeyframes, that.yKeyframes) && Objects.equals(this.zKeyframes, that.zKeyframes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.xKeyframes, this.yKeyframes, this.zKeyframes);
    }
}


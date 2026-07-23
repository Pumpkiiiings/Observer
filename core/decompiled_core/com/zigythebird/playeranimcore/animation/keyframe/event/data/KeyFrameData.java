/*
 * Decompiled with CFR 0.152.
 */
package com.zigythebird.playeranimcore.animation.keyframe.event.data;

import java.util.Objects;

public abstract class KeyFrameData {
    private final float startTick;

    public KeyFrameData(float startTick) {
        this.startTick = startTick;
    }

    public float getStartTick() {
        return this.startTick;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        return this.hashCode() == obj.hashCode();
    }

    public int hashCode() {
        return Objects.hashCode(Float.valueOf(this.startTick));
    }
}


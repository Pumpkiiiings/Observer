/*
 * Decompiled with CFR 0.152.
 */
package com.zigythebird.playeranimcore.animation.keyframe.event.data;

import com.zigythebird.playeranimcore.animation.keyframe.event.data.KeyFrameData;
import java.util.Objects;

public class SoundKeyframeData
extends KeyFrameData {
    private final String sound;

    public SoundKeyframeData(Float startTick, String sound) {
        super(startTick.floatValue());
        this.sound = sound;
    }

    public String getSound() {
        return this.sound;
    }

    @Override
    public int hashCode() {
        return Objects.hash(Float.valueOf(this.getStartTick()), this.sound);
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package com.zigythebird.playeranimcore.animation.keyframe.event.data;

import com.zigythebird.playeranimcore.animation.keyframe.event.data.KeyFrameData;
import java.util.Objects;

public class CustomInstructionKeyframeData
extends KeyFrameData {
    private final String instructions;

    public CustomInstructionKeyframeData(float startTick, String instructions) {
        super(startTick);
        this.instructions = instructions;
    }

    public String getInstructions() {
        return this.instructions;
    }

    @Override
    public int hashCode() {
        return Objects.hash(Float.valueOf(this.getStartTick()), this.instructions);
    }
}


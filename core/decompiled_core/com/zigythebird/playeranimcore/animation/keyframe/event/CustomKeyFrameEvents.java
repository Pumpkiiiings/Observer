/*
 * Decompiled with CFR 0.152.
 */
package com.zigythebird.playeranimcore.animation.keyframe.event;

import com.zigythebird.playeranimcore.animation.AnimationController;
import com.zigythebird.playeranimcore.animation.AnimationData;
import com.zigythebird.playeranimcore.animation.keyframe.event.data.CustomInstructionKeyframeData;
import com.zigythebird.playeranimcore.animation.keyframe.event.data.KeyFrameData;
import com.zigythebird.playeranimcore.animation.keyframe.event.data.ParticleKeyframeData;
import com.zigythebird.playeranimcore.animation.keyframe.event.data.SoundKeyframeData;
import com.zigythebird.playeranimcore.event.Event;
import com.zigythebird.playeranimcore.event.EventResult;
import java.util.Set;

public class CustomKeyFrameEvents {
    public static final Event<CustomKeyFrameHandler<CustomInstructionKeyframeData>> CUSTOM_INSTRUCTION_KEYFRAME_EVENT = new Event<CustomKeyFrameHandler>(listeners -> (animationTick, controller, eventKeyFrame, animationData) -> {
        for (CustomKeyFrameHandler listener : listeners) {
            EventResult result = listener.handle(animationTick, controller, eventKeyFrame, animationData);
            if (result != EventResult.FAIL) continue;
            return result;
        }
        return EventResult.PASS;
    });
    public static final Event<CustomKeyFrameHandler<ParticleKeyframeData>> PARTICLE_KEYFRAME_EVENT = new Event<CustomKeyFrameHandler>(listeners -> (animationTick, controller, eventKeyFrame, animationData) -> {
        for (CustomKeyFrameHandler listener : listeners) {
            EventResult result = listener.handle(animationTick, controller, eventKeyFrame, animationData);
            if (result != EventResult.FAIL) continue;
            return result;
        }
        return EventResult.PASS;
    });
    public static final Event<CustomKeyFrameHandler<SoundKeyframeData>> SOUND_KEYFRAME_EVENT = new Event<CustomKeyFrameHandler>(listeners -> (animationTick, controller, eventKeyFrame, animationData) -> {
        for (CustomKeyFrameHandler listener : listeners) {
            EventResult result = listener.handle(animationTick, controller, eventKeyFrame, animationData);
            if (result != EventResult.FAIL) continue;
            return result;
        }
        return EventResult.PASS;
    });
    public static final Event<ResetKeyFramesHandler> RESET_KEYFRAMES_EVENT = new Event<ResetKeyFramesHandler>(listeners -> (controller, eventKeyFrames) -> {
        for (ResetKeyFramesHandler listener : listeners) {
            listener.handle(controller, eventKeyFrames);
        }
    });

    @FunctionalInterface
    public static interface ResetKeyFramesHandler {
        public void handle(AnimationController var1, Set<KeyFrameData> var2);
    }

    @FunctionalInterface
    public static interface CustomKeyFrameHandler<T extends KeyFrameData> {
        public EventResult handle(float var1, AnimationController var2, T var3, AnimationData var4);
    }
}


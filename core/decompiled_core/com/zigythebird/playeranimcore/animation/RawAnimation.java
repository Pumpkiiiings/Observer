/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  org.jetbrains.annotations.Nullable
 */
package com.zigythebird.playeranimcore.animation;

import com.zigythebird.playeranimcore.animation.Animation;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.jetbrains.annotations.Nullable;

public final class RawAnimation {
    private final List<Stage> animationList;

    private RawAnimation() {
        this((List<Stage>)new ObjectArrayList());
    }

    private RawAnimation(List<Stage> animationList) {
        this.animationList = animationList;
    }

    public static RawAnimation begin() {
        return new RawAnimation();
    }

    public RawAnimation thenPlay(Animation animation) {
        return this.then(animation, Animation.LoopType.DEFAULT);
    }

    public RawAnimation thenLoop(Animation animation) {
        return this.then(animation, Animation.LoopType.LOOP);
    }

    public RawAnimation thenWait(int ticks) {
        this.animationList.add(new Stage(Animation.generateWaitAnimation(ticks), Animation.LoopType.PLAY_ONCE));
        return this;
    }

    public RawAnimation thenPlayAndHold(Animation animation) {
        return this.then(animation, Animation.LoopType.HOLD_ON_LAST_FRAME);
    }

    public RawAnimation thenPlayXTimes(Animation animation, int playCount) {
        for (int i = 0; i < playCount; ++i) {
            this.then(animation, i == playCount - 1 ? Animation.LoopType.DEFAULT : Animation.LoopType.PLAY_ONCE);
        }
        return this;
    }

    public RawAnimation then(Animation animation, Animation.LoopType loopType) {
        this.animationList.add(new Stage(animation, loopType));
        return this;
    }

    public List<Stage> getAnimationStages() {
        return this.animationList;
    }

    public static RawAnimation copyOf(RawAnimation other) {
        RawAnimation newInstance = RawAnimation.begin();
        newInstance.animationList.addAll(other.animationList);
        return newInstance;
    }

    public String toString() {
        return "RawAnimation{" + this.animationList.stream().map(Stage::toString).collect(Collectors.joining(" -> ")) + "}";
    }

    public int getStageCount() {
        return this.animationList.size();
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
        return Objects.hash(this.animationList);
    }

    public record Stage(@Nullable Animation animation, Animation.LoopType loopType) {
        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || this.getClass() != obj.getClass()) {
                return false;
            }
            return this.hashCode() == obj.hashCode();
        }

        @Override
        public String toString() {
            return this.animation == null ? "Invalid animation stage." : this.animation.toString();
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.animation, this.loopType);
        }
    }
}


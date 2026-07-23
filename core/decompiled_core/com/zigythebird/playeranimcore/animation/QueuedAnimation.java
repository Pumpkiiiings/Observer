/*
 * Decompiled with CFR 0.152.
 */
package com.zigythebird.playeranimcore.animation;

import com.zigythebird.playeranimcore.animation.Animation;

public record QueuedAnimation(Animation animation, Animation.LoopType loopType) {
    public boolean hasBeginTick() {
        return this.animation.data().has("beginTick");
    }

    public boolean hasEndTick() {
        return !this.animation.loopType().shouldPlayAgain(null, this.animation) && this.animation.data().has("endTick");
    }

    public boolean isDisableAxisIfNotModified() {
        return this.animation.data().isDisableAxisIfNotModified();
    }

    public boolean isAnimationPlayerAnimatorFormat() {
        return this.animation.data().isAnimationPlayerAnimatorFormat();
    }
}


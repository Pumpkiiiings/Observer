/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.Pair
 *  org.jetbrains.annotations.NotNull
 */
package com.zigythebird.playeranimcore.animation.layered;

import com.zigythebird.playeranimcore.animation.AnimationData;
import com.zigythebird.playeranimcore.animation.layered.IAnimation;
import com.zigythebird.playeranimcore.api.firstPerson.FirstPersonConfiguration;
import com.zigythebird.playeranimcore.api.firstPerson.FirstPersonMode;
import com.zigythebird.playeranimcore.bones.PlayerAnimBone;
import it.unimi.dsi.fastutil.Pair;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class AnimationStack
implements IAnimation {
    protected final List<Pair<Integer, IAnimation>> layers = new ArrayList<Pair<Integer, IAnimation>>();

    public List<Pair<Integer, IAnimation>> getLayers() {
        return this.layers;
    }

    @Override
    public boolean isActive() {
        for (Pair<Integer, IAnimation> layer : this.layers) {
            if (!((IAnimation)layer.right()).isActive()) continue;
            return true;
        }
        return false;
    }

    @Override
    public void tick(AnimationData state) {
        for (Pair<Integer, IAnimation> layer : this.layers) {
            ((IAnimation)layer.right()).tick(state);
        }
    }

    @Override
    public void get3DTransform(@NotNull PlayerAnimBone bone) {
        for (Pair<Integer, IAnimation> layer : this.layers) {
            if (!((IAnimation)layer.right()).isActive()) continue;
            ((IAnimation)layer.right()).get3DTransform(bone);
        }
    }

    @Override
    public void setupAnim(AnimationData state) {
        for (Pair<Integer, IAnimation> layer : this.layers) {
            if (!((IAnimation)layer.right()).isActive()) continue;
            ((IAnimation)layer.right()).setupAnim(state);
        }
    }

    public void addAnimLayer(int priority, IAnimation layer) {
        int search;
        for (search = 0; this.layers.size() > search && (Integer)this.layers.get(search).left() < priority; ++search) {
        }
        this.layers.add(search, (Pair<Integer, IAnimation>)Pair.of((Object)priority, (Object)layer));
    }

    public boolean removeLayer(IAnimation layer) {
        return this.layers.removeIf(integerIAnimationPair -> integerIAnimationPair.right() == layer);
    }

    public boolean removeLayer(int layerLevel) {
        return this.layers.removeIf(integerIAnimationPair -> (Integer)integerIAnimationPair.left() == layerLevel);
    }

    @Override
    @NotNull
    public FirstPersonMode getFirstPersonMode() {
        int i = this.layers.size();
        while (i > 0) {
            FirstPersonMode mode;
            Pair<Integer, IAnimation> layer;
            if (!((IAnimation)(layer = this.layers.get(--i)).right()).isActive() || (mode = ((IAnimation)layer.right()).getFirstPersonMode()) == FirstPersonMode.NONE) continue;
            return mode;
        }
        return FirstPersonMode.NONE;
    }

    @Override
    @NotNull
    public FirstPersonConfiguration getFirstPersonConfiguration() {
        int i = this.layers.size();
        while (i > 0) {
            FirstPersonMode mode;
            Pair<Integer, IAnimation> layer;
            if (!((IAnimation)(layer = this.layers.get(--i)).right()).isActive() || (mode = ((IAnimation)layer.right()).getFirstPersonMode()) == FirstPersonMode.NONE) continue;
            return ((IAnimation)layer.right()).getFirstPersonConfiguration();
        }
        return IAnimation.super.getFirstPersonConfiguration();
    }

    @Override
    public int getFirstPersonTransitionLength() {
        for (int i = this.layers.size() - 1; i >= 0; --i) {
            int transitionLength;
            IAnimation layer = (IAnimation)this.layers.get(i).right();
            if (!layer.isActive() || layer.getFirstPersonMode() != FirstPersonMode.THIRD_PERSON_MODEL || (transitionLength = layer.getFirstPersonTransitionLength()) <= 0) continue;
            return transitionLength;
        }
        return 0;
    }

    @Override
    public boolean isFirstPersonFollowsCamera() {
        for (int i = this.layers.size() - 1; i >= 0; --i) {
            IAnimation layer = (IAnimation)this.layers.get(i).right();
            if (!layer.isActive()) continue;
            return layer.isFirstPersonFollowsCamera();
        }
        return IAnimation.super.isFirstPersonFollowsCamera();
    }

    public int getPriority() {
        int priority = 0;
        for (int i = this.layers.size() - 1; i >= 0; --i) {
            Pair<Integer, IAnimation> layer = this.layers.get(i);
            if (!((IAnimation)layer.right()).isActive()) continue;
            priority = (Integer)layer.left();
            break;
        }
        return priority;
    }

    public String toString() {
        return "AnimationStack{layers=" + String.valueOf(this.layers) + "}";
    }
}


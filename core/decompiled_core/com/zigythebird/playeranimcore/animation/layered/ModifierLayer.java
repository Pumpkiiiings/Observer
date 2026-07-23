/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.zigythebird.playeranimcore.animation.layered;

import com.zigythebird.playeranimcore.animation.AnimationData;
import com.zigythebird.playeranimcore.animation.layered.IAnimation;
import com.zigythebird.playeranimcore.animation.layered.modifier.AbstractFadeModifier;
import com.zigythebird.playeranimcore.animation.layered.modifier.AbstractModifier;
import com.zigythebird.playeranimcore.api.firstPerson.FirstPersonConfiguration;
import com.zigythebird.playeranimcore.api.firstPerson.FirstPersonMode;
import com.zigythebird.playeranimcore.bones.PlayerAnimBone;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ModifierLayer<T extends IAnimation>
implements IAnimation {
    protected final List<AbstractModifier> modifiers = new ArrayList<AbstractModifier>();
    @Nullable
    T animation;

    public ModifierLayer(@Nullable T animation, AbstractModifier ... modifiers) {
        this.animation = animation;
        Collections.addAll(this.modifiers, modifiers);
    }

    public ModifierLayer() {
        this(null, new AbstractModifier[0]);
    }

    @Nullable
    public T getAnimation() {
        return this.animation;
    }

    @Override
    public void tick(AnimationData state) {
        for (int i = 0; i < this.modifiers.size(); ++i) {
            if (!this.modifiers.get(i).canRemove()) continue;
            this.removeModifier(i--);
        }
        if (this.modifiers.size() > 0) {
            this.modifiers.get(0).tick(state);
        } else if (this.animation != null) {
            this.animation.tick(state);
        }
    }

    public void addModifier(@NotNull AbstractModifier modifier, int idx) {
        modifier.setHost(this);
        this.modifiers.add(idx, modifier);
        this.linkModifiers();
    }

    public void addModifierBefore(@NotNull AbstractModifier modifier) {
        this.addModifier(modifier, 0);
    }

    public void addModifierLast(@NotNull AbstractModifier modifier) {
        this.addModifier(modifier, this.modifiers.size());
    }

    public void removeModifier(int idx) {
        this.modifiers.remove(idx);
        this.linkModifiers();
    }

    public void removeAllModifiers() {
        this.modifiers.clear();
    }

    public int getModifierCount() {
        return this.modifiers.size();
    }

    @Nullable
    public AbstractModifier getModifier(int idx) {
        try {
            return this.modifiers.get(idx);
        }
        catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    public boolean removeModifierIf(Predicate<? super AbstractModifier> predicate) {
        return this.modifiers.removeIf(predicate);
    }

    public void setAnimation(@Nullable T animation) {
        this.animation = animation;
        this.linkModifiers();
    }

    public void replaceAnimationWithFade(@NotNull AbstractFadeModifier fadeModifier, @Nullable T newAnimation) {
        this.replaceAnimationWithFade(fadeModifier, newAnimation, true);
    }

    public void replaceAnimationWithFade(@NotNull AbstractFadeModifier fadeModifier, @Nullable T newAnimation, boolean fadeFromNothing) {
        if (fadeFromNothing || this.getAnimation() != null && this.getAnimation().isActive()) {
            fadeModifier.setTransitionAnimation((IAnimation)this.getAnimation());
            this.addModifierLast(fadeModifier);
        }
        this.setAnimation(newAnimation);
    }

    public int size() {
        return this.modifiers.size();
    }

    protected void linkModifiers() {
        Iterator<AbstractModifier> modifierIterator = this.modifiers.iterator();
        if (modifierIterator.hasNext()) {
            AbstractModifier tmp = modifierIterator.next();
            while (modifierIterator.hasNext()) {
                AbstractModifier tmp2 = modifierIterator.next();
                tmp.setAnim(tmp2);
                tmp = tmp2;
            }
            tmp.setAnim(this.animation);
        }
    }

    @Override
    public boolean isActive() {
        if (!this.modifiers.isEmpty()) {
            return this.modifiers.get(0).isActive();
        }
        if (this.animation != null) {
            return this.animation.isActive();
        }
        return false;
    }

    @Override
    public void get3DTransform(@NotNull PlayerAnimBone bone) {
        if (!this.modifiers.isEmpty()) {
            this.modifiers.getFirst().get3DTransform(bone);
        } else if (this.animation != null) {
            this.animation.get3DTransform(bone);
        }
    }

    @Override
    public void setupAnim(AnimationData state) {
        if (!this.modifiers.isEmpty()) {
            this.modifiers.get(0).setupAnim(state);
        } else if (this.animation != null) {
            this.animation.setupAnim(state);
        }
    }

    @Override
    @NotNull
    public FirstPersonMode getFirstPersonMode() {
        if (!this.modifiers.isEmpty()) {
            return this.modifiers.get(0).getFirstPersonMode();
        }
        if (this.animation != null) {
            return this.animation.getFirstPersonMode();
        }
        return IAnimation.super.getFirstPersonMode();
    }

    @Override
    @NotNull
    public FirstPersonConfiguration getFirstPersonConfiguration() {
        if (!this.modifiers.isEmpty()) {
            return this.modifiers.get(0).getFirstPersonConfiguration();
        }
        if (this.animation != null) {
            return this.animation.getFirstPersonConfiguration();
        }
        return IAnimation.super.getFirstPersonConfiguration();
    }

    public String toString() {
        return "ModifierLayer{modifiers=" + String.valueOf(this.modifiers) + ", animation=" + String.valueOf(this.animation) + "}";
    }
}


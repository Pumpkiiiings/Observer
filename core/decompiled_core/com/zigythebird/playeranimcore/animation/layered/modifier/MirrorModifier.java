/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package com.zigythebird.playeranimcore.animation.layered.modifier;

import com.zigythebird.playeranimcore.animation.layered.modifier.AbstractModifier;
import com.zigythebird.playeranimcore.api.firstPerson.FirstPersonConfiguration;
import com.zigythebird.playeranimcore.bones.PlayerAnimBone;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

public class MirrorModifier
extends AbstractModifier {
    public static final Map<String, String> mirrorMap;
    public boolean enabled = true;

    @Override
    public void get3DTransform(@NotNull PlayerAnimBone bone) {
        if (!this.enabled) {
            super.get3DTransform(bone);
            return;
        }
        String modelName = bone.getName();
        if (mirrorMap.containsKey(modelName)) {
            modelName = mirrorMap.get(modelName);
        }
        this.transformBone(bone);
        PlayerAnimBone newBone = new PlayerAnimBone(modelName);
        newBone.copyOtherBone(bone);
        super.get3DTransform(newBone);
        this.transformBone(newBone);
        bone.copyOtherBone(newBone);
    }

    @Override
    @NotNull
    public FirstPersonConfiguration getFirstPersonConfiguration() {
        FirstPersonConfiguration configuration = super.getFirstPersonConfiguration();
        if (!this.enabled) {
            return configuration;
        }
        return new FirstPersonConfiguration().setShowLeftArm(configuration.isShowRightArm()).setShowRightArm(configuration.isShowLeftArm()).setShowLeftItem(configuration.isShowRightItem()).setShowRightItem(configuration.isShowLeftItem());
    }

    protected void transformBone(PlayerAnimBone bone) {
        bone.position.x *= -1.0f;
        bone.rotation.y *= -1.0f;
        bone.rotation.z *= -1.0f;
    }

    @Override
    public String toString() {
        return "MirrorModifier{anim=" + String.valueOf(this.anim) + ", enabled=" + this.enabled + "}";
    }

    static {
        HashMap<String, String> partMap = new HashMap<String, String>();
        partMap.put("left_arm", "right_arm");
        partMap.put("left_leg", "right_leg");
        partMap.put("left_item", "right_item");
        partMap.put("right_arm", "left_arm");
        partMap.put("right_leg", "left_leg");
        partMap.put("right_item", "left_item");
        mirrorMap = Collections.unmodifiableMap(partMap);
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 */
package com.zigythebird.playeranimcore.util;

import com.google.gson.JsonObject;
import com.zigythebird.playeranimcore.PlayerAnimLib;

public class ParticleEffectUtils {
    public static String parseIdentifier(String raw) {
        return ParticleEffectUtils.getIdentifier((JsonObject)PlayerAnimLib.GSON.fromJson(raw, JsonObject.class));
    }

    public static String getIdentifier(JsonObject obj) {
        return obj.getAsJsonObject("particle_effect").getAsJsonObject("description").get("identifier").getAsString();
    }
}


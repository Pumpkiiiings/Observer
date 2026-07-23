/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.reflect.TypeToken
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.zigythebird.playeranimcore;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.zigythebird.playeranimcore.animation.Animation;
import com.zigythebird.playeranimcore.loading.AnimationLoader;
import com.zigythebird.playeranimcore.loading.KeyFrameLoader;
import com.zigythebird.playeranimcore.loading.UniversalAnimLoader;
import java.lang.reflect.Type;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlayerAnimLib {
    public static final String MOD_ID = "player_animation_library";
    public static final Logger LOGGER = LoggerFactory.getLogger((String)"player_animation_library");
    public static final Type ANIMATIONS_MAP_TYPE = new TypeToken<Map<String, Animation>>(){}.getType();
    public static final Gson GSON = new GsonBuilder().registerTypeAdapter(Animation.Keyframes.class, (Object)new KeyFrameLoader()).registerTypeAdapter(Animation.class, (Object)new AnimationLoader()).registerTypeAdapter(ANIMATIONS_MAP_TYPE, (Object)new UniversalAnimLoader()).disableHtmlEscaping().create();
}


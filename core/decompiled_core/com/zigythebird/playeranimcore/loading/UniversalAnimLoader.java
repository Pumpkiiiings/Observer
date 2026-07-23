/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonDeserializer
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  org.jetbrains.annotations.NotNull
 */
package com.zigythebird.playeranimcore.loading;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.zigythebird.playeranimcore.PlayerAnimLib;
import com.zigythebird.playeranimcore.animation.Animation;
import com.zigythebird.playeranimcore.animation.keyframe.event.data.CustomInstructionKeyframeData;
import com.zigythebird.playeranimcore.animation.keyframe.event.data.ParticleKeyframeData;
import com.zigythebird.playeranimcore.animation.keyframe.event.data.SoundKeyframeData;
import com.zigythebird.playeranimcore.loading.PlayerAnimatorLoader;
import com.zigythebird.playeranimcore.math.Vec3f;
import com.zigythebird.playeranimcore.util.JsonUtil;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;

public class UniversalAnimLoader
implements JsonDeserializer<Map<String, Animation>> {
    public static final Animation.Keyframes NO_KEYFRAMES = new Animation.Keyframes(new SoundKeyframeData[0], new ParticleKeyframeData[0], new CustomInstructionKeyframeData[0]);
    private static final Pattern UPPERCASE_PATTERN = Pattern.compile("([A-Z])");
    private static final Pattern UNDERSCORE_PATTERN = Pattern.compile("_(.)");

    public static Map<String, Animation> loadAnimations(InputStream resource) throws IOException {
        try (InputStreamReader reader = new InputStreamReader(resource);){
            Map<String, Animation> map = UniversalAnimLoader.loadAnimations((JsonObject)PlayerAnimLib.GSON.fromJson((Reader)reader, JsonObject.class));
            return map;
        }
    }

    public static Map<@NotNull String, Animation> loadAnimations(JsonObject json) {
        if (json.has("animations")) {
            Map animationMap = (Map)PlayerAnimLib.GSON.fromJson(json.get("animations"), PlayerAnimLib.ANIMATIONS_MAP_TYPE);
            if (json.has("parents") && json.has("model")) {
                Map<String, String> parents = UniversalAnimLoader.getParents(JsonUtil.getAsJsonObject(json, "parents", new JsonObject()));
                Map<String, Vec3f> bones = UniversalAnimLoader.getModel(JsonUtil.getAsJsonObject(json, "model", new JsonObject()));
                for (Animation animation : animationMap.values()) {
                    if (animation.bones().isEmpty()) {
                        animation.bones().putAll(bones);
                    }
                    if (!animation.parents().isEmpty()) continue;
                    animation.parents().putAll(parents);
                }
            }
            return animationMap;
        }
        Animation animation = (Animation)PlayerAnimatorLoader.GSON.fromJson((JsonElement)json, Animation.class);
        return Collections.singletonMap(animation.getNameOrId(), animation);
    }

    public Map<String, Animation> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();
        Object2ObjectOpenHashMap animations = new Object2ObjectOpenHashMap(obj.size());
        for (Map.Entry entry : obj.entrySet()) {
            try {
                Animation animation = (Animation)context.deserialize((JsonElement)((JsonElement)entry.getValue()).getAsJsonObject(), Animation.class);
                if (!animation.data().has("name")) {
                    animation.data().put("name", entry.getKey());
                }
                animations.put((String)entry.getKey(), animation);
            }
            catch (Exception ex) {
                PlayerAnimLib.LOGGER.error("Unable to parse animation: {}", entry.getKey(), (Object)ex);
            }
        }
        return animations;
    }

    public static Map<String, String> getParents(JsonObject parentsObj) {
        HashMap<String, String> parents = new HashMap<String, String>(parentsObj.size());
        for (Map.Entry entry : parentsObj.entrySet()) {
            parents.put(UniversalAnimLoader.getCorrectPlayerBoneName((String)entry.getKey()), ((JsonElement)entry.getValue()).getAsString());
        }
        return parents;
    }

    public static Map<String, Vec3f> getModel(JsonObject modelObj) {
        HashMap<String, Vec3f> bones = new HashMap<String, Vec3f>(modelObj.size());
        for (Map.Entry entry : modelObj.entrySet()) {
            JsonObject object = ((JsonElement)entry.getValue()).getAsJsonObject();
            JsonArray pivot = object.get("pivot").getAsJsonArray();
            Vec3f bone = new Vec3f(pivot.get(0).getAsFloat(), pivot.get(1).getAsFloat(), pivot.get(2).getAsFloat());
            bones.put((String)entry.getKey(), bone);
        }
        return bones;
    }

    public static String getCorrectPlayerBoneName(String name) {
        return UPPERCASE_PATTERN.matcher(name).replaceAll("_$1").toLowerCase(Locale.ROOT);
    }

    public static String restorePlayerBoneName(String name) {
        StringBuilder result = new StringBuilder();
        String lowerCase = name.toLowerCase(Locale.ROOT);
        Matcher matcher = UNDERSCORE_PATTERN.matcher(lowerCase);
        int lastEnd = 0;
        while (matcher.find()) {
            result.append(lowerCase, lastEnd, matcher.start());
            result.append(Character.toUpperCase(matcher.group(1).charAt(0)));
            lastEnd = matcher.end();
        }
        result.append(lowerCase.substring(lastEnd));
        return result.toString();
    }
}


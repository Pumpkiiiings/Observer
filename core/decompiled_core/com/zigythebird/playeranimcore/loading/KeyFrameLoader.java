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
 *  com.google.gson.JsonPrimitive
 */
package com.zigythebird.playeranimcore.loading;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.zigythebird.playeranimcore.PlayerAnimLib;
import com.zigythebird.playeranimcore.animation.Animation;
import com.zigythebird.playeranimcore.animation.keyframe.event.data.CustomInstructionKeyframeData;
import com.zigythebird.playeranimcore.animation.keyframe.event.data.ParticleKeyframeData;
import com.zigythebird.playeranimcore.animation.keyframe.event.data.SoundKeyframeData;
import com.zigythebird.playeranimcore.util.JsonUtil;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class KeyFrameLoader
implements JsonDeserializer<Animation.Keyframes> {
    public Animation.Keyframes deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext ctx) throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();
        SoundKeyframeData[] sounds = KeyFrameLoader.buildSoundFrameData(obj);
        ParticleKeyframeData[] particles = KeyFrameLoader.buildParticleFrameData(obj);
        CustomInstructionKeyframeData[] customInstructions = KeyFrameLoader.buildCustomFrameData(obj);
        return new Animation.Keyframes(sounds, particles, customInstructions);
    }

    private static SoundKeyframeData[] buildSoundFrameData(JsonObject rootObj) {
        JsonObject soundsObj = JsonUtil.getAsJsonObject(rootObj, "sound_effects", new JsonObject());
        SoundKeyframeData[] sounds = new SoundKeyframeData[soundsObj.size()];
        int index = 0;
        for (Map.Entry entry : soundsObj.entrySet()) {
            sounds[index] = new SoundKeyframeData(Float.valueOf(Float.parseFloat((String)entry.getKey()) * 20.0f), JsonUtil.getAsString(((JsonElement)entry.getValue()).getAsJsonObject(), "effect"));
            ++index;
        }
        return sounds;
    }

    private static ParticleKeyframeData[] buildParticleFrameData(JsonObject rootObj) {
        JsonObject particlesObj = JsonUtil.getAsJsonObject(rootObj, "particle_effects", new JsonObject());
        ParticleKeyframeData[] particles = new ParticleKeyframeData[particlesObj.size()];
        int index = 0;
        for (Map.Entry entry : particlesObj.entrySet()) {
            JsonObject obj = ((JsonElement)entry.getValue()).getAsJsonObject();
            String effect = JsonUtil.getAsString(obj, "effect", "");
            String locator = JsonUtil.getAsString(obj, "locator", "");
            String script = JsonUtil.getAsString(obj, "pre_effect_script", "");
            particles[index] = new ParticleKeyframeData(Float.parseFloat((String)entry.getKey()) * 20.0f, effect, locator, script);
            ++index;
        }
        return particles;
    }

    private static CustomInstructionKeyframeData[] buildCustomFrameData(JsonObject rootObj) {
        JsonObject customInstructionsObj = JsonUtil.getAsJsonObject(rootObj, "timeline", new JsonObject());
        CustomInstructionKeyframeData[] customInstructions = new CustomInstructionKeyframeData[customInstructionsObj.size()];
        int index = 0;
        for (Map.Entry entry : customInstructionsObj.entrySet()) {
            String instructions = "";
            Object v = entry.getValue();
            if (v instanceof JsonArray) {
                JsonArray array = (JsonArray)v;
                instructions = ((List)PlayerAnimLib.GSON.fromJson((JsonElement)array, List.class)).toString();
            } else {
                v = entry.getValue();
                if (v instanceof JsonPrimitive) {
                    JsonPrimitive primitive = (JsonPrimitive)v;
                    instructions = primitive.getAsString();
                }
            }
            customInstructions[index] = new CustomInstructionKeyframeData(Float.parseFloat((String)entry.getKey()) * 20.0f, instructions);
            ++index;
        }
        return customInstructions;
    }
}


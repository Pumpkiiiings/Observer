/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonPrimitive
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.zigythebird.playeranimcore.animation;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.zigythebird.playeranimcore.enums.AnimationFormat;
import java.lang.runtime.SwitchBootstraps;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record ExtraAnimationData(Map<String, Object> data) {
    public static final String NAME_KEY = "name";
    public static final String UUID_KEY = "uuid";
    public static final String FORMAT_KEY = "format";
    public static final String BEGIN_TICK_KEY = "beginTick";
    public static final String END_TICK_KEY = "endTick";
    public static final String EASING_BEFORE_KEY = "easeBeforeKeyframe";
    public static final String APPLY_BEND_TO_OTHER_BONES_KEY = "applyBendToOtherBones";
    public static final String PARTICLE_EFFECTS_KEY = "particleEffects";
    public static final String DISABLE_AXIS_IF_NOT_MODIFIED = "disableAxisIfNotModified";

    public ExtraAnimationData(String key, Object value) {
        this(new HashMap<String, Object>(Collections.singletonMap(key, value)));
    }

    public ExtraAnimationData() {
        this(new HashMap<String, Object>(1));
    }

    @Nullable
    public String name() {
        String name;
        Object data = this.data().get(NAME_KEY);
        if (data instanceof JsonObject) {
            JsonObject jsonObject = (JsonObject)data;
            name = jsonObject.get("fallback").getAsString();
        } else {
            name = (String)data;
        }
        return name != null ? name.toLowerCase(Locale.ROOT).replace("\"", "").replace(" ", "_") : null;
    }

    public boolean has(String name) {
        return this.data().containsKey(name);
    }

    @Nullable
    public ByteBuffer getBinary(String name) {
        Object obj = this.getRaw(name);
        if (obj == null) {
            return null;
        }
        if (obj instanceof String) {
            String str = (String)obj;
            try {
                byte[] byArray = Base64.getDecoder().decode(str);
                obj = byArray;
                this.put(name, byArray);
            }
            catch (IllegalArgumentException e) {
                return null;
            }
        }
        if (obj instanceof byte[]) {
            byte[] bytes = (byte[])obj;
            obj = ByteBuffer.wrap(bytes).asReadOnlyBuffer();
            this.put(name, obj);
        }
        if (obj instanceof ByteBuffer) {
            ByteBuffer buffer = (ByteBuffer)obj;
            return buffer.asReadOnlyBuffer();
        }
        return null;
    }

    public Object getRaw(String name) {
        return this.data().get(name);
    }

    public <T> Optional<T> get(String key) {
        Object obj = this.getRaw(key);
        if (obj == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(obj);
        }
        catch (Throwable throwable) {
            return Optional.empty();
        }
    }

    public <T> T getNullable(String key) {
        return this.get(key).orElse(null);
    }

    public List<?> getList(String key) {
        Object obj;
        Object object = obj = this.getRaw(key);
        int n = 0;
        return switch (SwitchBootstraps.typeSwitch("typeSwitch", new Object[]{JsonArray.class, List.class}, (Object)object, n)) {
            case -1 -> Collections.emptyList();
            case 0 -> {
                JsonArray json = (JsonArray)object;
                yield json.asList();
            }
            case 1 -> {
                List list;
                yield list = (List)object;
            }
            default -> throw new ClassCastException(obj.getClass().getName());
        };
    }

    public void put(String name, Object object) {
        this.data.put(name, object);
    }

    public void fromJson(JsonObject node, boolean root) {
        for (Map.Entry entry : node.entrySet()) {
            String key = (String)entry.getKey();
            if (root && ("version".equalsIgnoreCase(key) || "emote".equalsIgnoreCase(key))) continue;
            this.data().put(key, this.getValue((JsonElement)entry.getValue()));
        }
    }

    public Object getValue(JsonElement element) {
        if (element instanceof JsonPrimitive) {
            JsonPrimitive p = (JsonPrimitive)element;
            if (p.isBoolean()) {
                return p.getAsBoolean();
            }
            if (p.isString()) {
                return p.getAsString();
            }
            if (p.isNumber()) {
                return Float.valueOf(p.getAsFloat());
            }
        }
        if (element instanceof JsonArray) {
            JsonArray array = (JsonArray)element;
            ArrayList<Object> list = new ArrayList<Object>(array.size());
            for (JsonElement element1 : array) {
                list.add(this.getValue(element1));
            }
            return list;
        }
        return element.toString();
    }

    public ExtraAnimationData copy() {
        return new ExtraAnimationData(new HashMap<String, Object>(this.data()));
    }

    public boolean isDisableAxisIfNotModified() {
        return this.get(DISABLE_AXIS_IF_NOT_MODIFIED).orElse(true);
    }

    public boolean isAnimationPlayerAnimatorFormat() {
        return this.get(FORMAT_KEY).orElse(null) == AnimationFormat.PLAYER_ANIMATOR;
    }

    @Override
    @NotNull
    public String toString() {
        return this.data.toString();
    }
}


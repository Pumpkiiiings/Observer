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
 *  it.unimi.dsi.fastutil.floats.FloatObjectPair
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  team.unnamed.mocha.parser.ast.AccessExpression
 *  team.unnamed.mocha.parser.ast.Expression
 *  team.unnamed.mocha.parser.ast.FloatExpression
 *  team.unnamed.mocha.parser.ast.IdentifierExpression
 *  team.unnamed.mocha.runtime.IsConstantExpression
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
import com.zigythebird.playeranimcore.animation.ExtraAnimationData;
import com.zigythebird.playeranimcore.animation.keyframe.BoneAnimation;
import com.zigythebird.playeranimcore.animation.keyframe.Keyframe;
import com.zigythebird.playeranimcore.animation.keyframe.KeyframeStack;
import com.zigythebird.playeranimcore.easing.EasingType;
import com.zigythebird.playeranimcore.enums.Axis;
import com.zigythebird.playeranimcore.enums.TransformType;
import com.zigythebird.playeranimcore.loading.PlayerAnimatorLoader;
import com.zigythebird.playeranimcore.loading.UniversalAnimLoader;
import com.zigythebird.playeranimcore.math.Vec3f;
import com.zigythebird.playeranimcore.molang.MolangLoader;
import com.zigythebird.playeranimcore.util.JsonUtil;
import it.unimi.dsi.fastutil.floats.FloatObjectPair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import team.unnamed.mocha.parser.ast.AccessExpression;
import team.unnamed.mocha.parser.ast.Expression;
import team.unnamed.mocha.parser.ast.FloatExpression;
import team.unnamed.mocha.parser.ast.IdentifierExpression;
import team.unnamed.mocha.runtime.IsConstantExpression;

public class AnimationLoader
implements JsonDeserializer<Animation> {
    public Animation deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject animationObj = json.getAsJsonObject();
        float length = animationObj.has("animation_length") ? JsonUtil.getAsFloat(animationObj, "animation_length") * 20.0f : -1.0f;
        Map<String, BoneAnimation> boneAnimations = AnimationLoader.bakeBoneAnimations(JsonUtil.getAsJsonObject(animationObj, "bones", new JsonObject()));
        if (length == -1.0f) {
            length = AnimationLoader.calculateAnimationLength(boneAnimations);
        }
        Animation.LoopType loopType = AnimationLoader.readLoopType(animationObj, length);
        Animation.Keyframes keyframes = (Animation.Keyframes)context.deserialize((JsonElement)animationObj, Animation.Keyframes.class);
        Map<String, String> parents = UniversalAnimLoader.getParents(JsonUtil.getAsJsonObject(animationObj, "parents", new JsonObject()));
        Map<String, Vec3f> bones = UniversalAnimLoader.getModel(JsonUtil.getAsJsonObject(animationObj, "model", new JsonObject()));
        ExtraAnimationData extraData = new ExtraAnimationData();
        if (animationObj.has("player_animation_library")) {
            extraData.fromJson(animationObj.getAsJsonObject("player_animation_library"), false);
        }
        return new Animation(extraData, length, loopType, boneAnimations, keyframes, bones, parents);
    }

    private static Animation.LoopType readLoopType(JsonObject animationObj, float length) throws JsonParseException {
        if (animationObj.has("loopTick")) {
            float returnTick = JsonUtil.getAsFloat(animationObj, "loopTick") * 20.0f;
            if (returnTick > length || returnTick < 0.0f) {
                throw new JsonParseException("The returnTick has to be a non-negative value smaller than the endTick value");
            }
            return Animation.LoopType.returnToTickLoop(returnTick);
        }
        return Animation.LoopType.fromJson(animationObj.get("loop"));
    }

    private static Map<String, BoneAnimation> bakeBoneAnimations(JsonObject bonesObj) {
        HashMap<String, BoneAnimation> animations = new HashMap<String, BoneAnimation>(bonesObj.size());
        for (Map.Entry entry : bonesObj.entrySet()) {
            JsonObject entryObj = ((JsonElement)entry.getValue()).getAsJsonObject();
            KeyframeStack scaleFrames = AnimationLoader.buildKeyframeStack(AnimationLoader.getKeyframes(entryObj.get("scale")), TransformType.SCALE);
            KeyframeStack positionFrames = AnimationLoader.buildKeyframeStack(AnimationLoader.getKeyframes(entryObj.get("position")), TransformType.POSITION);
            KeyframeStack rotationFrames = AnimationLoader.buildKeyframeStack(AnimationLoader.getKeyframes(entryObj.get("rotation")), TransformType.ROTATION);
            KeyframeStack bendFrames = AnimationLoader.buildKeyframeStack(AnimationLoader.getKeyframes(entryObj.get("bend")), TransformType.BEND);
            animations.put(UniversalAnimLoader.getCorrectPlayerBoneName((String)entry.getKey()), new BoneAnimation(rotationFrames, positionFrames, scaleFrames, bendFrames.xKeyframes()));
        }
        return animations;
    }

    private static List<FloatObjectPair<JsonElement>> getKeyframes(JsonElement element) {
        JsonArray array;
        if (element == null) {
            return List.of();
        }
        if (element instanceof JsonPrimitive) {
            JsonPrimitive primitive = (JsonPrimitive)element;
            array = new JsonArray(3);
            array.add((JsonElement)primitive);
            array.add((JsonElement)primitive);
            array.add((JsonElement)primitive);
            element = array;
        }
        if (element instanceof JsonArray) {
            JsonArray array2 = (JsonArray)element;
            return ObjectArrayList.of((Object[])new FloatObjectPair[]{FloatObjectPair.of((float)0.0f, (Object)array2)});
        }
        if (element instanceof JsonObject) {
            JsonObject obj = (JsonObject)element;
            if (obj.has("vector")) {
                return ObjectArrayList.of((Object[])new FloatObjectPair[]{FloatObjectPair.of((float)0.0f, (Object)obj)});
            }
            if (obj.has("value")) {
                array = new JsonArray(3);
                array.add((Number)Float.valueOf(obj.get("value").getAsFloat()));
                array.add((Number)0);
                array.add((Number)0);
                obj.add("vector", (JsonElement)array);
                return ObjectArrayList.of((Object[])new FloatObjectPair[]{FloatObjectPair.of((float)0.0f, (Object)obj)});
            }
            ObjectArrayList list = new ObjectArrayList();
            for (Map.Entry entry : obj.entrySet()) {
                float timestamp = AnimationLoader.readTimestamp((String)entry.getKey());
                if (timestamp == 0.0f && !list.isEmpty()) {
                    throw new JsonParseException("Invalid keyframe data - multiple starting keyframes?" + (String)entry.getKey());
                }
                Object v = entry.getValue();
                if (v instanceof JsonObject) {
                    JsonObject entryObj = (JsonObject)v;
                    if (entryObj.has("value")) {
                        JsonArray array3 = new JsonArray(3);
                        array3.add((Number)Float.valueOf(entryObj.get("value").getAsFloat()));
                        array3.add((Number)0);
                        array3.add((Number)0);
                        entryObj.add("vector", (JsonElement)array3);
                        list.add(FloatObjectPair.of((float)timestamp, (Object)entryObj));
                    } else if (!entryObj.has("vector")) {
                        AnimationLoader.addBedrockKeyframes(timestamp, entryObj, (List<FloatObjectPair<JsonElement>>)list);
                        continue;
                    }
                }
                list.add(FloatObjectPair.of((float)timestamp, (Object)((JsonElement)entry.getValue())));
            }
            return list;
        }
        throw new JsonParseException("Invalid object type provided to getTripletObj, got: " + String.valueOf(element));
    }

    private static JsonArray extractBedrockKeyframe(JsonElement keyframe) {
        if (keyframe.isJsonArray()) {
            return keyframe.getAsJsonArray();
        }
        if (keyframe.isJsonPrimitive()) {
            JsonArray array = new JsonArray(3);
            array.add((Number)Float.valueOf(keyframe.getAsFloat()));
            array.add((Number)0);
            array.add((Number)0);
            return array;
        }
        if (!keyframe.isJsonObject()) {
            throw new JsonParseException("Invalid keyframe data - expected array or object, found " + String.valueOf(keyframe));
        }
        JsonObject keyframeObj = keyframe.getAsJsonObject();
        if (keyframeObj.has("vector")) {
            return keyframeObj.get("vector").getAsJsonArray();
        }
        if (keyframeObj.has("pre")) {
            return keyframeObj.get("pre").getAsJsonArray();
        }
        return keyframeObj.get("post").getAsJsonArray();
    }

    private static void addBedrockKeyframes(float timestamp, JsonObject keyframe, List<FloatObjectPair<JsonElement>> keyframes) {
        boolean addedFrame = false;
        if (keyframe.has("pre")) {
            addedFrame = true;
            JsonArray value = AnimationLoader.extractBedrockKeyframe(keyframe.get("pre"));
            JsonObject result = null;
            if (keyframe.has("easing")) {
                result = new JsonObject();
                result.add("vector", (JsonElement)value);
                result.add("easing", keyframe.get("easing"));
                if (keyframe.has("easingArgs")) {
                    result.add("easingArgs", keyframe.get("easingArgs"));
                }
            }
            keyframes.add((FloatObjectPair<JsonElement>)FloatObjectPair.of((float)(timestamp == 0.0f ? timestamp : timestamp - 0.001f), (Object)(result != null ? result : value)));
        }
        if (keyframe.has("post")) {
            JsonArray values = AnimationLoader.extractBedrockKeyframe(keyframe.get("post"));
            if (keyframe.has("lerp_mode")) {
                JsonObject keyframeObj = new JsonObject();
                keyframeObj.add("vector", (JsonElement)values);
                keyframeObj.add("easing", keyframe.get("lerp_mode"));
                keyframes.add((FloatObjectPair<JsonElement>)FloatObjectPair.of((float)timestamp, (Object)keyframeObj));
            } else {
                keyframes.add((FloatObjectPair<JsonElement>)FloatObjectPair.of((float)timestamp, (Object)values));
            }
            return;
        }
        if (!addedFrame) {
            throw new JsonParseException("Invalid keyframe data - expected array, found " + String.valueOf(keyframe));
        }
    }

    private static KeyframeStack buildKeyframeStack(List<FloatObjectPair<JsonElement>> entries, TransformType type) {
        if (entries.isEmpty()) {
            return new KeyframeStack();
        }
        ObjectArrayList xFrames = new ObjectArrayList();
        ObjectArrayList yFrames = new ObjectArrayList();
        ObjectArrayList zFrames = new ObjectArrayList();
        List<Expression> xPrev = null;
        List<Expression> yPrev = null;
        List<Expression> zPrev = null;
        float prevTimeX = 0.0f;
        float prevTimeY = 0.0f;
        float prevTimeZ = 0.0f;
        for (FloatObjectPair<JsonElement> entry : entries) {
            JsonObject obj;
            JsonArray array;
            JsonElement element = (JsonElement)entry.right();
            float curTime = entry.leftFloat();
            boolean isForRotation = type == TransformType.ROTATION || type == TransformType.BEND;
            FloatExpression defaultValue = type == TransformType.SCALE ? FloatExpression.ONE : FloatExpression.ZERO;
            JsonArray keyFrameVector = element instanceof JsonArray ? (array = (JsonArray)element) : JsonUtil.getAsJsonArray(element.getAsJsonObject(), "vector");
            List<Expression> xValue = MolangLoader.parseJson(isForRotation, keyFrameVector.get(0), (Expression)defaultValue);
            List<Expression> yValue = MolangLoader.parseJson(isForRotation, keyFrameVector.get(1), (Expression)defaultValue);
            List<Expression> zValue = MolangLoader.parseJson(isForRotation, keyFrameVector.get(2), (Expression)defaultValue);
            JsonObject entryObj = element instanceof JsonObject ? (obj = (JsonObject)element) : null;
            EasingType easingType = AnimationLoader.getEasingForAxis(entryObj, null, EasingType.LINEAR);
            List<List<Expression>> easingArgs = AnimationLoader.getEasingArgsForAxis(entryObj, null, (List<List<Expression>>)new ObjectArrayList());
            if (AnimationLoader.isEnabled(xValue)) {
                xFrames.add(new Keyframe((curTime - prevTimeX) * 20.0f, xPrev == null ? xValue : xPrev, xValue, AnimationLoader.getEasingForAxis(entryObj, Axis.X, easingType), AnimationLoader.getEasingArgsForAxis(entryObj, Axis.X, easingArgs)));
                xPrev = xValue;
                prevTimeX = curTime;
            }
            if (AnimationLoader.isEnabled(yValue)) {
                yFrames.add(new Keyframe((curTime - prevTimeY) * 20.0f, yPrev == null ? yValue : yPrev, yValue, AnimationLoader.getEasingForAxis(entryObj, Axis.Y, easingType), AnimationLoader.getEasingArgsForAxis(entryObj, Axis.Y, easingArgs)));
                yPrev = yValue;
                prevTimeY = curTime;
            }
            if (!AnimationLoader.isEnabled(zValue)) continue;
            zFrames.add(new Keyframe((curTime - prevTimeZ) * 20.0f, zPrev == null ? zValue : zPrev, zValue, AnimationLoader.getEasingForAxis(entryObj, Axis.Z, easingType), AnimationLoader.getEasingArgsForAxis(entryObj, Axis.Z, easingArgs)));
            zPrev = zValue;
            prevTimeZ = curTime;
        }
        return new KeyframeStack(AnimationLoader.addArgsForKeyframes((List<Keyframe>)xFrames, type), AnimationLoader.addArgsForKeyframes((List<Keyframe>)yFrames, type), AnimationLoader.addArgsForKeyframes((List<Keyframe>)zFrames, type));
    }

    private static EasingType getEasingForAxis(JsonObject entryObj, Axis axis, EasingType easingType) {
        Object memberName = "easing";
        if (axis != null) {
            memberName = (String)memberName + axis.name();
        }
        return entryObj != null && entryObj.has((String)memberName) ? EasingType.fromJson(entryObj.get((String)memberName)) : easingType;
    }

    private static List<List<Expression>> getEasingArgsForAxis(JsonObject entryObj, Axis axis, List<List<Expression>> easingArg) {
        Object memberName = "easingArgs";
        if (axis != null) {
            memberName = (String)memberName + axis.name();
        }
        return entryObj != null && entryObj.has((String)memberName) ? JsonUtil.jsonArrayToList(JsonUtil.getAsJsonArray(entryObj, (String)memberName), ele -> Collections.singletonList(FloatExpression.of((float)ele.getAsFloat()))) : easingArg;
    }

    private static List<Keyframe> addArgsForKeyframes(List<Keyframe> frames, TransformType type) {
        Keyframe frame;
        if (frames.isEmpty()) {
            return frames;
        }
        if (frames.size() == 1 && (frame = frames.getFirst()).easingType() != EasingType.LINEAR) {
            frames.set(0, new Keyframe(frame.length(), frame.startValue(), frame.endValue()));
            return frames;
        }
        for (int i = 0; i < frames.size(); ++i) {
            Keyframe frame2 = frames.get(i);
            if (frame2.easingType() == EasingType.CATMULLROM) {
                frames.set(i, new Keyframe(frame2.length(), frame2.startValue(), frame2.endValue(), frame2.easingType(), (List<List<Expression>>)ObjectArrayList.of((Object[])new List[]{i == 0 ? frame2.startValue() : frames.get(i - 1).endValue(), i + 1 >= frames.size() ? frame2.endValue() : frames.get(i + 1).endValue()})));
                continue;
            }
            if (frame2.easingType() != EasingType.BEZIER) continue;
            List<Expression> leftValue = frame2.easingArgs().getFirst();
            List<Expression> rightValue = frame2.easingArgs().get(2);
            List<Expression> rightTime = frame2.easingArgs().get(3);
            if (type == TransformType.ROTATION) {
                rightValue = AnimationLoader.toRadiansForBezier(rightValue);
                leftValue = AnimationLoader.toRadiansForBezier(leftValue);
            }
            frames.set(i, new Keyframe(frame2.length(), frame2.startValue(), frame2.endValue(), frame2.easingType(), (List<List<Expression>>)ObjectArrayList.of((Object[])new List[]{leftValue, frame2.easingArgs().get(1)})));
            if (frame2.easingArgs().size() > 4) {
                frames.get(i).easingArgs().add(frame2.easingArgs().get(4));
                frames.get(i).easingArgs().add(frame2.easingArgs().get(5));
            }
            if (frames.size() <= i + 1) continue;
            Keyframe nextKeyframe = frames.get(i + 1);
            if (nextKeyframe.easingType() != EasingType.BEZIER) {
                frames.set(i + 1, new Keyframe(nextKeyframe.length(), nextKeyframe.startValue(), nextKeyframe.endValue(), EasingType.BEZIER, (List<List<Expression>>)ObjectArrayList.of((Object[])new List[]{PlayerAnimatorLoader.ZERO, PlayerAnimatorLoader.ZERO, rightValue, rightTime})));
                continue;
            }
            nextKeyframe.easingArgs().add(rightValue);
            nextKeyframe.easingArgs().add(rightTime);
        }
        return frames;
    }

    private static boolean isEnabled(List<Expression> expressions) {
        IdentifierExpression id;
        AccessExpression access;
        Expression expression;
        if (expressions.size() == 1 && (expression = expressions.getFirst()) instanceof AccessExpression && (expression = (access = (AccessExpression)expression).object()) instanceof IdentifierExpression && "pal".equals((id = (IdentifierExpression)expression).name())) {
            return !"disabled".equals(access.property()) && !"skip".equals(access.property());
        }
        return true;
    }

    private static List<Expression> toRadiansForBezier(List<Expression> expressions) {
        if (expressions.size() == 1 && IsConstantExpression.test((Expression)expressions.getFirst())) {
            return Collections.singletonList(FloatExpression.of((double)Math.toRadians(MolangLoader.MOCHA_ENGINE.eval(expressions))));
        }
        PlayerAnimLib.LOGGER.warn("Invalid easing arguments for bezier: {}\nFor rotations bezier args can only be floats.", expressions);
        return expressions;
    }

    public static float calculateAnimationLength(Map<String, BoneAnimation> boneAnimations) {
        float length = 0.0f;
        for (BoneAnimation animation : boneAnimations.values()) {
            length = Math.max(length, animation.rotationKeyFrames().getLastKeyframeTime());
            length = Math.max(length, animation.positionKeyFrames().getLastKeyframeTime());
            length = Math.max(length, animation.scaleKeyFrames().getLastKeyframeTime());
            length = Math.max(length, Keyframe.getLastKeyframeTime(animation.bendKeyFrames()));
        }
        return length == 0.0f ? Float.MAX_VALUE : length;
    }

    private static float readTimestamp(String timestamp) {
        try {
            return Float.parseFloat(timestamp);
        }
        catch (Throwable th) {
            return 0.0f;
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  team.unnamed.mocha.MochaEngine
 *  team.unnamed.mocha.parser.ast.FloatExpression
 */
package com.zigythebird.playeranimcore.animation;

import com.zigythebird.playeranimcore.PlayerAnimLib;
import com.zigythebird.playeranimcore.animation.Animation;
import com.zigythebird.playeranimcore.animation.AnimationData;
import com.zigythebird.playeranimcore.animation.ExtraAnimationData;
import com.zigythebird.playeranimcore.animation.QueuedAnimation;
import com.zigythebird.playeranimcore.animation.RawAnimation;
import com.zigythebird.playeranimcore.animation.keyframe.BoneAnimation;
import com.zigythebird.playeranimcore.animation.keyframe.Keyframe;
import com.zigythebird.playeranimcore.animation.keyframe.KeyframeLocation;
import com.zigythebird.playeranimcore.animation.keyframe.KeyframeStack;
import com.zigythebird.playeranimcore.animation.keyframe.event.CustomKeyFrameEvents;
import com.zigythebird.playeranimcore.animation.keyframe.event.data.CustomInstructionKeyframeData;
import com.zigythebird.playeranimcore.animation.keyframe.event.data.KeyFrameData;
import com.zigythebird.playeranimcore.animation.keyframe.event.data.ParticleKeyframeData;
import com.zigythebird.playeranimcore.animation.keyframe.event.data.SoundKeyframeData;
import com.zigythebird.playeranimcore.animation.layered.AnimationContainer;
import com.zigythebird.playeranimcore.animation.layered.AnimationSnapshot;
import com.zigythebird.playeranimcore.animation.layered.IAnimation;
import com.zigythebird.playeranimcore.animation.layered.modifier.AbstractFadeModifier;
import com.zigythebird.playeranimcore.animation.layered.modifier.AbstractModifier;
import com.zigythebird.playeranimcore.animation.layered.modifier.SpeedModifier;
import com.zigythebird.playeranimcore.api.firstPerson.FirstPersonConfiguration;
import com.zigythebird.playeranimcore.api.firstPerson.FirstPersonMode;
import com.zigythebird.playeranimcore.bones.AdvancedPlayerAnimBone;
import com.zigythebird.playeranimcore.bones.PivotBone;
import com.zigythebird.playeranimcore.bones.PlayerAnimBone;
import com.zigythebird.playeranimcore.bones.ToggleablePlayerAnimBone;
import com.zigythebird.playeranimcore.easing.EasingType;
import com.zigythebird.playeranimcore.enums.PlayState;
import com.zigythebird.playeranimcore.enums.State;
import com.zigythebird.playeranimcore.enums.TransformType;
import com.zigythebird.playeranimcore.event.EventResult;
import com.zigythebird.playeranimcore.math.Vec3f;
import com.zigythebird.playeranimcore.molang.MolangLoader;
import com.zigythebird.playeranimcore.util.MatrixUtil;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.unnamed.mocha.MochaEngine;
import team.unnamed.mocha.parser.ast.FloatExpression;

public abstract class AnimationController
implements IAnimation {
    public static KeyframeLocation EMPTY_KEYFRAME_LOCATION = new KeyframeLocation(new Keyframe(0.0f), 0.0f);
    public static KeyframeLocation EMPTY_SCALE_KEYFRAME_LOCATION = new KeyframeLocation(new Keyframe(0.0f, Collections.singletonList(FloatExpression.ONE), Collections.singletonList(FloatExpression.ONE)), 0.0f);
    protected final AnimationStateHandler stateHandler;
    protected final Map<String, Vec3f> bonePositions;
    protected final Map<String, AdvancedPlayerAnimBone> bones = new Object2ObjectOpenHashMap();
    protected final Map<String, PlayerAnimBone> activeBones = new Object2ObjectOpenHashMap();
    protected final Map<String, PivotBone> pivotBones = new Object2ObjectOpenHashMap();
    protected Queue<QueuedAnimation> animationQueue = new LinkedList<QueuedAnimation>();
    protected final MochaEngine<AnimationController> molangRuntime;
    protected boolean needsAnimationReload = false;
    protected CustomKeyFrameEvents.CustomKeyFrameHandler<SoundKeyframeData> soundKeyframeHandler = null;
    protected CustomKeyFrameEvents.CustomKeyFrameHandler<ParticleKeyframeData> particleKeyframeHandler = null;
    protected CustomKeyFrameEvents.CustomKeyFrameHandler<CustomInstructionKeyframeData> customKeyframeHandler = null;
    protected RawAnimation triggeredAnimation = null;
    protected boolean handlingTriggeredAnimations = false;
    protected RawAnimation currentRawAnimation;
    protected QueuedAnimation currentAnimation;
    protected int tick;
    protected float startAnimFrom;
    protected State animationState = State.STOPPED;
    protected boolean isLoopStarted = false;
    protected Consumer<Function<String, AdvancedPlayerAnimBone>> postAnimationSetupConsumer = function -> {};
    protected Function<AnimationController, EasingType> overrideEasingTypeFunction = controller -> null;
    private final Set<KeyFrameData> executedKeyFrames = new ObjectOpenHashSet();
    protected AnimationData animationData;
    protected Function<AnimationController, FirstPersonMode> firstPersonMode = null;
    protected Function<AnimationController, FirstPersonConfiguration> firstPersonConfiguration = null;
    private boolean firstPersonFollowsCamera = false;
    private int firstPersonTransitionLength = 0;
    private final List<AbstractModifier> modifiers = new ArrayList<AbstractModifier>();
    private final InternalAnimationAccessor internalAnimationAccessor = new InternalAnimationAccessor(this);

    public AnimationController(AnimationStateHandler animationHandler, Map<String, Vec3f> bonePositions, Function<AnimationController, MochaEngine<AnimationController>> molangRuntime) {
        this.stateHandler = animationHandler;
        this.bonePositions = bonePositions;
        this.molangRuntime = molangRuntime.apply(this);
        this.registerBones();
    }

    public abstract void registerBones();

    public void setFirstPersonFollowsCamera(boolean followsCamera) {
        this.firstPersonFollowsCamera = followsCamera;
    }

    @Override
    public boolean isFirstPersonFollowsCamera() {
        return this.firstPersonFollowsCamera;
    }

    public void setFirstPersonTransitionLength(int ticks) {
        this.firstPersonTransitionLength = Math.max(0, ticks);
    }

    @Override
    public int getFirstPersonTransitionLength() {
        return this.firstPersonTransitionLength;
    }

    public AnimationController setSoundKeyframeHandler(CustomKeyFrameEvents.CustomKeyFrameHandler<SoundKeyframeData> soundHandler) {
        this.soundKeyframeHandler = soundHandler;
        return this;
    }

    public AnimationController setParticleKeyframeHandler(CustomKeyFrameEvents.CustomKeyFrameHandler<ParticleKeyframeData> particleHandler) {
        this.particleKeyframeHandler = particleHandler;
        return this;
    }

    public AnimationController setCustomInstructionKeyframeHandler(CustomKeyFrameEvents.CustomKeyFrameHandler<CustomInstructionKeyframeData> customInstructionHandler) {
        this.customKeyframeHandler = customInstructionHandler;
        return this;
    }

    public AnimationController setPostAnimationSetupConsumer(Consumer<Function<String, AdvancedPlayerAnimBone>> postAnimationSetupConsumer) {
        this.postAnimationSetupConsumer = postAnimationSetupConsumer;
        return this;
    }

    public AnimationController setOverrideEasingType(EasingType easingTypeFunction) {
        return this.setOverrideEasingTypeFunction(animatable -> easingTypeFunction);
    }

    public AnimationController setOverrideEasingTypeFunction(Function<AnimationController, EasingType> easingType) {
        this.overrideEasingTypeFunction = easingType;
        return this;
    }

    public AnimationController receiveTriggeredAnimations() {
        this.handlingTriggeredAnimations = true;
        return this;
    }

    @Nullable
    public QueuedAnimation getCurrentAnimation() {
        return this.currentAnimation;
    }

    @Nullable
    public Animation getCurrentAnimationInstance() {
        List<RawAnimation.Stage> stages;
        Animation animation;
        QueuedAnimation queuedAnimation = this.getCurrentAnimation();
        if (queuedAnimation != null && (animation = queuedAnimation.animation()) != null) {
            return animation;
        }
        RawAnimation rawAnimation = this.getTriggeredAnimation();
        if (rawAnimation != null && !(stages = rawAnimation.getAnimationStages()).isEmpty()) {
            return stages.getFirst().animation();
        }
        return null;
    }

    @Nullable
    public RawAnimation getTriggeredAnimation() {
        return this.triggeredAnimation;
    }

    @NotNull
    public State getAnimationState() {
        return this.animationState;
    }

    public boolean isLoopStarted() {
        return this.isLoopStarted;
    }

    @Override
    public boolean isActive() {
        return this.animationState.isActive();
    }

    public AnimationData getAnimationData() {
        return this.animationData;
    }

    public void forceAnimationReset() {
        this.needsAnimationReload = true;
    }

    public void stop() {
        this.animationState = State.STOPPED;
        this.resetEventKeyFrames();
    }

    public void pause() {
        this.animationState = State.PAUSED;
    }

    public void unpause() {
        if (this.animationState == State.PAUSED) {
            this.animationState = State.RUNNING;
        }
    }

    public boolean hasAnimationFinished() {
        return this.currentRawAnimation != null && this.animationState == State.STOPPED;
    }

    public RawAnimation getCurrentRawAnimation() {
        return this.currentRawAnimation;
    }

    public boolean isPlayingTriggeredAnimation() {
        return this.triggeredAnimation != null && !this.hasAnimationFinished();
    }

    protected void setAnimation(RawAnimation rawAnimation, float startAnimFrom) {
        if (rawAnimation == null || rawAnimation.getAnimationStages().isEmpty()) {
            this.stop();
            return;
        }
        if (this.needsAnimationReload || !rawAnimation.equals(this.currentRawAnimation)) {
            Queue<QueuedAnimation> animations = this.getQueuedAnimations(rawAnimation);
            if (animations != null) {
                this.animationQueue = animations;
                this.currentRawAnimation = rawAnimation;
                this.startAnimFrom = startAnimFrom;
                this.tick = 0;
                this.animationState = State.RUNNING;
                this.currentAnimation = this.animationQueue.poll();
                this.setupNewAnimation();
                this.needsAnimationReload = false;
                return;
            }
            this.stop();
        }
    }

    protected void setAnimation(RawAnimation rawAnimation) {
        this.setAnimation(rawAnimation, 0.0f);
    }

    protected Queue<QueuedAnimation> getQueuedAnimations(RawAnimation rawAnimation) {
        LinkedList<QueuedAnimation> animations = new LinkedList<QueuedAnimation>();
        for (RawAnimation.Stage stage : rawAnimation.getAnimationStages()) {
            Animation animation = stage.animation();
            if (animation == null) continue;
            animations.add(new QueuedAnimation(animation, stage.loopType()));
        }
        return animations;
    }

    public void triggerAnimation(RawAnimation newAnimation, float startAnimFrom) {
        if (newAnimation == null) {
            return;
        }
        this.stop();
        this.triggeredAnimation = newAnimation;
        this.needsAnimationReload = true;
        this.animationState = State.RUNNING;
        this.tick = 0;
        this.startAnimFrom = startAnimFrom;
    }

    public void triggerAnimation(RawAnimation newAnimation) {
        this.triggerAnimation(newAnimation, 0.0f);
    }

    public void triggerAnimation(Animation newAnimation, float startAnimFrom) {
        this.triggerAnimation(RawAnimation.begin().then(newAnimation, Animation.LoopType.DEFAULT), startAnimFrom);
    }

    public void triggerAnimation(Animation newAnimation) {
        this.triggerAnimation(RawAnimation.begin().then(newAnimation, Animation.LoopType.DEFAULT), 0.0f);
    }

    public void replaceAnimationWithFade(@NotNull AbstractFadeModifier fadeModifier, @Nullable RawAnimation newAnimation, boolean fadeFromNothing) {
        if (fadeFromNothing || this.isActive()) {
            if (this.isActive()) {
                HashMap<String, ToggleablePlayerAnimBone> snapshots = new HashMap<String, ToggleablePlayerAnimBone>();
                for (PlayerAnimBone bone : this.activeBones.values()) {
                    snapshots.put(bone.getName(), new ToggleablePlayerAnimBone(bone));
                }
                fadeModifier.setTransitionAnimation(new AnimationSnapshot(snapshots));
            }
            this.addModifierLast(fadeModifier);
        }
        this.triggerAnimation(newAnimation);
    }

    public void replaceAnimationWithFade(@NotNull AbstractFadeModifier fadeModifier, @Nullable RawAnimation newAnimation) {
        this.replaceAnimationWithFade(fadeModifier, newAnimation, true);
    }

    public void replaceAnimationWithFade(@NotNull AbstractFadeModifier fadeModifier, @Nullable Animation newAnimation, boolean fadeFromNothing) {
        this.replaceAnimationWithFade(fadeModifier, RawAnimation.begin().then(newAnimation, Animation.LoopType.DEFAULT), fadeFromNothing);
    }

    public void replaceAnimationWithFade(@NotNull AbstractFadeModifier fadeModifier, @Nullable Animation newAnimation) {
        this.replaceAnimationWithFade(fadeModifier, newAnimation, true);
    }

    public boolean stopTriggeredAnimation() {
        if (this.triggeredAnimation == null) {
            return false;
        }
        if (this.currentRawAnimation == this.triggeredAnimation) {
            this.currentAnimation = null;
            this.currentRawAnimation = null;
        }
        this.triggeredAnimation = null;
        this.needsAnimationReload = true;
        return true;
    }

    protected PlayState handleAnimation(AnimationData state) {
        if (this.triggeredAnimation != null) {
            if (this.currentRawAnimation != this.triggeredAnimation) {
                this.currentAnimation = null;
            }
            this.setAnimation(this.triggeredAnimation, this.startAnimFrom);
            if (!this.hasAnimationFinished() && !this.handlingTriggeredAnimations) {
                return PlayState.CONTINUE;
            }
            this.triggeredAnimation = null;
            this.needsAnimationReload = true;
        }
        return this.stateHandler.handle(this, state, (animation, startTick) -> {
            this.setAnimation(animation, (float)startTick - state.getPartialTick());
            return PlayState.CONTINUE;
        });
    }

    public void process(AnimationData state) {
        float adjustedTick = Math.max(0.0f, state.getPartialTick() + this.startAnimFrom + (float)this.tick);
        PlayState playState = this.handleAnimation(state);
        if (playState == PlayState.STOP || this.currentAnimation == null && this.animationQueue.isEmpty()) {
            this.animationState = State.STOPPED;
            return;
        }
        if (this.getAnimationState() == State.RUNNING) {
            this.processCurrentAnimation(adjustedTick, state);
        }
    }

    public float getAnimationSpeed() {
        float speed = 1.0f;
        for (AbstractModifier modifier : this.modifiers) {
            if (!(modifier instanceof SpeedModifier)) continue;
            SpeedModifier speedModifier = (SpeedModifier)modifier;
            speed *= speedModifier.speed;
        }
        return speed;
    }

    /*
     * WARNING - void declaration
     */
    private void processCurrentAnimation(float adjustedTick, AnimationData animationData) {
        QueuedAnimation queued = this.currentAnimation;
        if (queued == null) {
            this.animationState = State.STOPPED;
            return;
        }
        Animation animation = queued.animation();
        if (adjustedTick >= animation.length()) {
            if (queued.loopType().shouldPlayAgain(this, animation)) {
                if (this.animationState != State.PAUSED) {
                    this.tick = 0;
                    adjustedTick = this.startAnimFrom = queued.loopType().restartFromTick(this, animation);
                    this.startAnimFrom -= animationData.getPartialTick();
                    this.resetEventKeyFrames();
                    this.isLoopStarted = true;
                }
            } else {
                QueuedAnimation nextAnimation = this.animationQueue.peek();
                this.resetEventKeyFrames();
                if (nextAnimation == null) {
                    this.animationState = State.STOPPED;
                    this.currentAnimation = null;
                    for (AdvancedPlayerAnimBone advancedPlayerAnimBone : this.bones.values()) {
                        advancedPlayerAnimBone.setToInitialPose();
                    }
                    for (PlayerAnimBone playerAnimBone : this.pivotBones.values()) {
                        playerAnimBone.setToInitialPose();
                    }
                    return;
                }
                this.animationState = State.RUNNING;
                this.tick = 0;
                this.startAnimFrom = -animationData.getPartialTick();
                adjustedTick = 0.0f;
                queued = this.currentAnimation = this.animationQueue.poll();
                this.setupNewAnimation();
            }
        }
        if (queued == null) {
            return;
        }
        for (PlayerAnimBone playerAnimBone : this.bones.values()) {
            playerAnimBone.setToInitialPose();
        }
        for (PlayerAnimBone playerAnimBone : this.pivotBones.values()) {
            playerAnimBone.setToInitialPose();
        }
        for (Map.Entry entry : animation.boneAnimations().entrySet()) {
            void var7_16;
            PlayerAnimBone playerAnimBone = this.bones.getOrDefault(entry.getKey(), null);
            boolean isAdvancedBone = false;
            AdvancedPlayerAnimBone advancedBone = null;
            if (playerAnimBone == null) {
                PlayerAnimBone playerAnimBone2 = this.pivotBones.getOrDefault(entry.getKey(), null);
            } else {
                advancedBone = (AdvancedPlayerAnimBone)playerAnimBone;
                isAdvancedBone = true;
            }
            if (var7_16 == null) continue;
            BoneAnimation boneAnimation = (BoneAnimation)entry.getValue();
            KeyframeStack rotationKeyFrames = boneAnimation.rotationKeyFrames();
            KeyframeStack positionKeyFrames = boneAnimation.positionKeyFrames();
            KeyframeStack scaleKeyFrames = boneAnimation.scaleKeyFrames();
            List<Keyframe> bendKeyFrames = boneAnimation.bendKeyFrames();
            EasingType easingOverride = this.overrideEasingTypeFunction.apply(this);
            var7_16.rotation.x = this.computeAnimValue(queued, rotationKeyFrames.xKeyframes(), adjustedTick, TransformType.ROTATION, easingOverride, isAdvancedBone ? advancedBone::setRotXTransitionLength : null);
            var7_16.rotation.y = this.computeAnimValue(queued, rotationKeyFrames.yKeyframes(), adjustedTick, TransformType.ROTATION, easingOverride, isAdvancedBone ? advancedBone::setRotYTransitionLength : null);
            var7_16.rotation.z = this.computeAnimValue(queued, rotationKeyFrames.zKeyframes(), adjustedTick, TransformType.ROTATION, easingOverride, isAdvancedBone ? advancedBone::setRotZTransitionLength : null);
            var7_16.position.x = this.computeAnimValue(queued, positionKeyFrames.xKeyframes(), adjustedTick, TransformType.POSITION, easingOverride, isAdvancedBone ? advancedBone::setPositionXTransitionLength : null);
            var7_16.position.y = this.computeAnimValue(queued, positionKeyFrames.yKeyframes(), adjustedTick, TransformType.POSITION, easingOverride, isAdvancedBone ? advancedBone::setPositionYTransitionLength : null);
            var7_16.position.z = this.computeAnimValue(queued, positionKeyFrames.zKeyframes(), adjustedTick, TransformType.POSITION, easingOverride, isAdvancedBone ? advancedBone::setPositionZTransitionLength : null);
            var7_16.scale.x = this.computeAnimValue(queued, scaleKeyFrames.xKeyframes(), adjustedTick, TransformType.SCALE, easingOverride, isAdvancedBone ? advancedBone::setScaleXTransitionLength : null);
            var7_16.scale.y = this.computeAnimValue(queued, scaleKeyFrames.yKeyframes(), adjustedTick, TransformType.SCALE, easingOverride, isAdvancedBone ? advancedBone::setScaleYTransitionLength : null);
            var7_16.scale.z = this.computeAnimValue(queued, scaleKeyFrames.zKeyframes(), adjustedTick, TransformType.SCALE, easingOverride, isAdvancedBone ? advancedBone::setScaleZTransitionLength : null);
            var7_16.bend = this.computeAnimValue(queued, bendKeyFrames, adjustedTick, TransformType.BEND, easingOverride, isAdvancedBone ? advancedBone::setBendTransitionLength : null);
        }
        this.applyCustomPivotPoints();
        this.handleCustomKeyframe(animation.keyFrames().sounds(), this.soundKeyframeHandler, CustomKeyFrameEvents.SOUND_KEYFRAME_EVENT.invoker(), adjustedTick, animationData);
        this.handleCustomKeyframe(animation.keyFrames().particles(), this.particleKeyframeHandler, CustomKeyFrameEvents.PARTICLE_KEYFRAME_EVENT.invoker(), adjustedTick, animationData);
        this.handleCustomKeyframe(animation.keyFrames().customInstructions(), this.customKeyframeHandler, CustomKeyFrameEvents.CUSTOM_INSTRUCTION_KEYFRAME_EVENT.invoker(), adjustedTick, animationData);
    }

    protected void applyCustomPivotPoints() {
        if (this.currentAnimation == null) {
            return;
        }
        Map<String, String> parentsMap = this.currentAnimation.animation().parents();
        if (parentsMap.isEmpty()) {
            return;
        }
        HashSet<String> processedBones = new HashSet<String>();
        for (PlayerAnimBone playerAnimBone : this.bones.values()) {
            this.processBoneHierarchy(playerAnimBone, parentsMap, processedBones);
        }
        for (PlayerAnimBone playerAnimBone : this.pivotBones.values()) {
            this.processBoneHierarchy(playerAnimBone, parentsMap, processedBones);
        }
    }

    private void processBoneHierarchy(PlayerAnimBone bone, Map<String, String> parentsMap, Set<String> processedBones) {
        String boneName = bone.getName();
        if (processedBones.contains(boneName)) {
            return;
        }
        String parentName = parentsMap.get(boneName);
        if (parentName == null) {
            processedBones.add(boneName);
            return;
        }
        PlayerAnimBone parent = this.pivotBones.get(parentName);
        if (parent == null) {
            parent = this.bones.get(parentName);
        }
        if (parent == null) {
            PlayerAnimLib.LOGGER.error("Parent {} not found for {}", (Object)parentName, (Object)boneName);
            return;
        }
        this.processBoneHierarchy(parent, parentsMap, processedBones);
        this.activeBones.put(boneName, bone);
        MatrixUtil.applyParentsToChild(bone, Collections.singletonList(parent), this::getBonePosition);
        processedBones.add(boneName);
    }

    protected <T extends KeyFrameData> void handleCustomKeyframe(T[] keyframes, @Nullable CustomKeyFrameEvents.CustomKeyFrameHandler<T> main, CustomKeyFrameEvents.CustomKeyFrameHandler<T> event, float animationTick, AnimationData animationData) {
        for (T keyframeData : keyframes) {
            EventResult result;
            if (!(animationTick >= ((KeyFrameData)keyframeData).getStartTick()) || !this.executedKeyFrames.add((KeyFrameData)keyframeData)) continue;
            EventResult eventResult = result = main == null ? EventResult.PASS : main.handle(animationTick, this, keyframeData, animationData);
            if (result == EventResult.PASS) {
                result = event.handle(animationTick, this, keyframeData, animationData);
            }
            if (result != EventResult.FAIL) continue;
            return;
        }
    }

    public float getAnimationTime() {
        return this.getAnimationTicks() / 20.0f;
    }

    public float getAnimationTicks() {
        if (this.animationData == null) {
            return 0.0f;
        }
        return (float)this.tick + this.startAnimFrom + this.animationData.getPartialTick();
    }

    protected void setupNewAnimation() {
        this.isLoopStarted = false;
        if (this.currentAnimation == null) {
            return;
        }
        this.activeBones.clear();
        this.resetEventKeyFrames();
        for (AdvancedPlayerAnimBone advancedPlayerAnimBone : this.bones.values()) {
            advancedPlayerAnimBone.setEnabled(this.currentAnimation.animation().getBone(advancedPlayerAnimBone.getName()) != null);
        }
        for (Map.Entry entry : this.currentAnimation.animation().boneAnimations().entrySet()) {
            if (this.bones.containsKey(entry.getKey())) {
                AdvancedPlayerAnimBone bone = this.bones.get(entry.getKey());
                this.activeBones.put((String)entry.getKey(), bone);
                if (this.currentAnimation.isDisableAxisIfNotModified()) {
                    BoneAnimation boneAnimation = (BoneAnimation)entry.getValue();
                    bone.positionXEnabled = !boneAnimation.positionKeyFrames().xKeyframes().isEmpty();
                    bone.positionYEnabled = !boneAnimation.positionKeyFrames().yKeyframes().isEmpty();
                    bone.positionZEnabled = !boneAnimation.positionKeyFrames().zKeyframes().isEmpty();
                    bone.rotXEnabled = !boneAnimation.rotationKeyFrames().xKeyframes().isEmpty();
                    bone.rotYEnabled = !boneAnimation.rotationKeyFrames().yKeyframes().isEmpty();
                    bone.rotZEnabled = !boneAnimation.rotationKeyFrames().zKeyframes().isEmpty();
                    bone.scaleXEnabled = !boneAnimation.scaleKeyFrames().xKeyframes().isEmpty();
                    bone.scaleYEnabled = !boneAnimation.scaleKeyFrames().yKeyframes().isEmpty();
                    bone.scaleZEnabled = !boneAnimation.scaleKeyFrames().zKeyframes().isEmpty();
                    bone.bendEnabled = !boneAnimation.bendKeyFrames().isEmpty();
                    continue;
                }
                bone.setEnabled(true);
                continue;
            }
            if (!this.pivotBones.containsKey(entry.getKey())) continue;
            this.activeBones.put((String)entry.getKey(), this.pivotBones.get(entry.getKey()));
        }
        for (String string : this.currentAnimation.animation().parents().keySet()) {
            if (!this.bones.containsKey(string)) continue;
            this.bones.get(string).setEnabled(true);
        }
        this.pivotBones.clear();
        for (Map.Entry entry : this.currentAnimation.animation().bones().entrySet()) {
            this.pivotBones.put((String)entry.getKey(), new PivotBone((String)entry.getKey(), (Vec3f)entry.getValue()));
        }
        this.postAnimationSetupConsumer.accept(this.bones::get);
    }

    private float computeAnimValue(QueuedAnimation queued, List<Keyframe> frames, float tick, TransformType type, @Nullable EasingType easingOverride, Consumer<Float> transitionLengthSetter) {
        Animation animation = queued.animation();
        ExtraAnimationData extraData = animation.data();
        float endTick = extraData.get("endTick").orElse(Float.valueOf(animation.length() - 1.0f)).floatValue();
        KeyframeLocation location = this.getCurrentKeyFrameLocation(frames, tick, type, queued.isAnimationPlayerAnimatorFormat() && queued.loopType().shouldPlayAgain(null, animation), animation.length(), queued.loopType().restartFromTick(null, animation));
        Keyframe currentFrame = location.keyframe();
        float startValue = this.molangRuntime.eval(currentFrame.startValue());
        float endValue = this.molangRuntime.eval(currentFrame.endValue());
        if (type == TransformType.ROTATION || type == TransformType.BEND) {
            if (!MolangLoader.isConstant(currentFrame.startValue())) {
                startValue = (float)Math.toRadians(startValue);
            }
            if (!MolangLoader.isConstant(currentFrame.endValue())) {
                endValue = (float)Math.toRadians(endValue);
            }
        }
        if (transitionLengthSetter != null) {
            if (queued.hasBeginTick() && !frames.isEmpty() && currentFrame == frames.getFirst() && ((Float)extraData.get("beginTick").get()).floatValue() > tick) {
                startValue = endValue;
                transitionLengthSetter.accept(Float.valueOf(currentFrame.length()));
            } else if (queued.hasEndTick() && !frames.isEmpty() && currentFrame == frames.getLast() && endTick <= tick) {
                transitionLengthSetter.accept(Float.valueOf(animation.length() - endTick));
            } else {
                transitionLengthSetter.accept(null);
            }
        }
        float lerpValue = currentFrame.length() > 0.0f ? location.startTick() / currentFrame.length() : 0.0f;
        return EasingType.lerpWithOverride(this.molangRuntime, startValue, endValue, currentFrame.length(), lerpValue, currentFrame.easingArgs(), currentFrame.easingType(), easingOverride);
    }

    private KeyframeLocation getCurrentKeyFrameLocation(List<Keyframe> frames, float ageInTicks, TransformType type, boolean isPlayerAnimatorLoop, float animTime, float returnToTick) {
        if (frames.isEmpty()) {
            return type == TransformType.SCALE ? EMPTY_SCALE_KEYFRAME_LOCATION : EMPTY_KEYFRAME_LOCATION;
        }
        Keyframe firstFrame = returnToTick == 0.0f ? frames.getFirst() : Keyframe.getKeyframeAtTime(frames, returnToTick);
        float totalFrameTime = 0.0f;
        for (Keyframe frame : frames) {
            if (!((totalFrameTime += frame.length()) > ageInTicks)) continue;
            if (isPlayerAnimatorLoop && this.isLoopStarted() && frame == firstFrame) {
                float stopTickMinusLastKeyframe = animTime - Keyframe.getLastKeyframeTime(frames);
                return new KeyframeLocation(new Keyframe(frame.length() + stopTickMinusLastKeyframe, frames.getLast().endValue(), frame.endValue(), frame.easingType(), frame.easingArgs()), ageInTicks + stopTickMinusLastKeyframe);
            }
            return new KeyframeLocation(frame, ageInTicks - (totalFrameTime - frame.length()));
        }
        if (isPlayerAnimatorLoop) {
            return new KeyframeLocation(new Keyframe(firstFrame.length() + animTime - totalFrameTime, frames.getLast().endValue(), firstFrame.endValue(), firstFrame.easingType(), firstFrame.easingArgs()), ageInTicks - totalFrameTime);
        }
        return new KeyframeLocation(frames.getLast(), ageInTicks);
    }

    protected void resetEventKeyFrames() {
        if (!this.executedKeyFrames.isEmpty()) {
            CustomKeyFrameEvents.RESET_KEYFRAMES_EVENT.invoker().handle(this, this.executedKeyFrames);
        }
        this.executedKeyFrames.clear();
    }

    public PlayerAnimBone get3DTransformRaw(@NotNull PlayerAnimBone bone) {
        if (this.activeBones.containsKey(bone.getName())) {
            PlayerAnimBone bone1 = this.activeBones.get(bone.getName());
            QueuedAnimation queued = this.currentAnimation;
            if (queued != null && bone1 instanceof AdvancedPlayerAnimBone) {
                AdvancedPlayerAnimBone advancedBone = (AdvancedPlayerAnimBone)bone1;
                ExtraAnimationData extraData = queued.animation().data();
                if (queued.hasBeginTick() && ((Float)extraData.get("beginTick").get()).floatValue() > this.getAnimationTicks()) {
                    bone.beginOrEndTickLerp(advancedBone, this.getAnimationTicks(), null);
                } else if (queued.hasEndTick() && ((Float)extraData.get("endTick").get()).floatValue() <= this.getAnimationTicks()) {
                    bone.beginOrEndTickLerp(advancedBone, this.getAnimationTicks() - ((Float)extraData.get("endTick").get()).floatValue(), this.currentAnimation.animation());
                } else {
                    bone.copyOtherBoneIfNotDisabled(bone1);
                }
            } else {
                bone.copyOtherBoneIfNotDisabled(bone1);
            }
        }
        return bone;
    }

    @Override
    public void get3DTransform(@NotNull PlayerAnimBone bone) {
        if (!this.modifiers.isEmpty()) {
            this.modifiers.getFirst().get3DTransform(bone);
            return;
        }
        this.get3DTransformRaw(bone);
    }

    @Override
    @NotNull
    public FirstPersonMode getFirstPersonMode() {
        if (this.firstPersonMode != null) {
            return this.firstPersonMode.apply(this);
        }
        return FirstPersonMode.NONE;
    }

    @Override
    @NotNull
    public FirstPersonConfiguration getFirstPersonConfiguration() {
        if (this.firstPersonConfiguration != null) {
            return this.firstPersonConfiguration.apply(this);
        }
        return IAnimation.DEFAULT_FIRST_PERSON_CONFIG;
    }

    public AnimationController setFirstPersonMode(FirstPersonMode mode) {
        this.firstPersonMode = controller -> mode;
        return this;
    }

    public AnimationController setFirstPersonModeHandler(Function<AnimationController, FirstPersonMode> modeHandler) {
        this.firstPersonMode = modeHandler;
        return this;
    }

    public AnimationController setFirstPersonConfiguration(FirstPersonConfiguration config) {
        this.firstPersonConfiguration = controller -> config;
        return this;
    }

    public AnimationController setFirstPersonConfigurationHandler(Function<AnimationController, FirstPersonConfiguration> configHandler) {
        this.firstPersonConfiguration = configHandler;
        return this;
    }

    @Override
    public void tick(AnimationData state) {
        for (int i = 0; i < this.modifiers.size(); ++i) {
            if (!this.modifiers.get(i).canRemove()) continue;
            this.removeModifier(i--);
        }
        if (!this.modifiers.isEmpty()) {
            this.modifiers.getFirst().tick(state);
        } else {
            this.handleAnimation(state);
            if (this.animationState == State.RUNNING) {
                ++this.tick;
            }
        }
    }

    @Override
    public void setupAnim(AnimationData state) {
        this.animationData = state;
        if (!this.modifiers.isEmpty()) {
            this.modifiers.getFirst().setupAnim(state);
        } else {
            this.process(state);
        }
    }

    public Vec3f getBonePosition(String name) {
        if (this.bonePositions.containsKey(name)) {
            return this.bonePositions.get(name);
        }
        if (this.pivotBones.containsKey(name)) {
            return this.pivotBones.get(name).getPivot();
        }
        return Vec3f.ZERO;
    }

    @ApiStatus.Internal
    public List<AbstractModifier> getModifiers() {
        return this.modifiers;
    }

    public AnimationController addModifier(@NotNull AbstractModifier modifier, int idx) {
        modifier.setHost(this);
        this.modifiers.add(idx, modifier);
        this.linkModifiers();
        return this;
    }

    public AnimationController addModifierBefore(@NotNull AbstractModifier modifier) {
        this.addModifier(modifier, 0);
        return this;
    }

    public AnimationController addModifierLast(@NotNull AbstractModifier modifier) {
        this.addModifier(modifier, this.modifiers.size());
        return this;
    }

    public AnimationController removeModifier(int idx) {
        this.modifiers.remove(idx);
        this.linkModifiers();
        return this;
    }

    public AnimationController removeAllModifiers() {
        this.modifiers.clear();
        return this;
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
        boolean success = this.modifiers.removeIf(predicate);
        this.linkModifiers();
        return success;
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
            tmp.setAnim(this.internalAnimationAccessor);
        }
    }

    public AdvancedPlayerAnimBone registerPlayerAnimBone(String name) {
        return this.registerPlayerAnimBone(new AdvancedPlayerAnimBone(name));
    }

    public AdvancedPlayerAnimBone registerPlayerAnimBone(AdvancedPlayerAnimBone bone) {
        this.bones.put(bone.getName(), bone);
        return bone;
    }

    @Nullable
    public AdvancedPlayerAnimBone getBone(String name) {
        return this.bones.get(name);
    }

    public String toString() {
        return "AnimationController{currentAnimation=" + String.valueOf(this.getCurrentAnimationInstance()) + ", tick=" + this.getAnimationTicks() + ", modifiers=" + String.valueOf(this.modifiers) + "}";
    }

    private static class InternalAnimationAccessor
    extends AnimationContainer<AnimationController> {
        private InternalAnimationAccessor(AnimationController controller) {
            super(controller);
        }

        @Override
        public void tick(AnimationData state) {
            ((AnimationController)this.anim).handleAnimation(state);
            if (((AnimationController)this.anim).animationState == State.RUNNING) {
                ++((AnimationController)this.anim).tick;
            }
        }

        @Override
        public void setupAnim(AnimationData state) {
            ((AnimationController)this.anim).process(state);
        }

        @Override
        public void get3DTransform(@NotNull PlayerAnimBone bone) {
            ((AnimationController)this.anim).get3DTransformRaw(bone);
        }
    }

    @FunctionalInterface
    public static interface AnimationStateHandler {
        public PlayState handle(AnimationController var1, AnimationData var2, AnimationSetter var3);
    }

    @FunctionalInterface
    public static interface AnimationSetter {
        default public PlayState setAnimation(RawAnimation animation) {
            return this.setAnimation(animation, 0);
        }

        public PlayState setAnimation(RawAnimation var1, int var2);
    }
}


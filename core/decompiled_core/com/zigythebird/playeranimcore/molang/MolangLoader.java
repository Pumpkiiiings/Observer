/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  org.jetbrains.annotations.Contract
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  team.unnamed.mocha.MochaEngine
 *  team.unnamed.mocha.parser.MolangParser
 *  team.unnamed.mocha.parser.ParseException
 *  team.unnamed.mocha.parser.ast.Expression
 *  team.unnamed.mocha.parser.ast.FloatExpression
 *  team.unnamed.mocha.runtime.IsConstantExpression
 *  team.unnamed.mocha.runtime.value.Function
 *  team.unnamed.mocha.runtime.value.NumberValue
 *  team.unnamed.mocha.runtime.value.Value
 */
package com.zigythebird.playeranimcore.molang;

import com.google.gson.JsonElement;
import com.zigythebird.playeranimcore.PlayerAnimLib;
import com.zigythebird.playeranimcore.animation.AnimationController;
import com.zigythebird.playeranimcore.event.MolangEvent;
import com.zigythebird.playeranimcore.molang.MochaMathExtensions;
import com.zigythebird.playeranimcore.molang.QueryBinding;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.unnamed.mocha.MochaEngine;
import team.unnamed.mocha.parser.MolangParser;
import team.unnamed.mocha.parser.ParseException;
import team.unnamed.mocha.parser.ast.Expression;
import team.unnamed.mocha.parser.ast.FloatExpression;
import team.unnamed.mocha.runtime.IsConstantExpression;
import team.unnamed.mocha.runtime.value.NumberValue;
import team.unnamed.mocha.runtime.value.Value;

public class MolangLoader {
    private static final Consumer<ParseException> HANDLER = e -> PlayerAnimLib.LOGGER.warn("Failed to parse!", (Throwable)e);
    public static final MochaEngine<?> MOCHA_ENGINE = MolangLoader.createNewEngine();

    @Contract(value="_, null, _ -> new; _, !null, _ -> new")
    public static List<Expression> parseJson(boolean isForRotation, @Nullable JsonElement element, @NotNull Expression defaultValue) {
        return MolangLoader.parseJson(isForRotation, element, Collections.singletonList(defaultValue));
    }

    @Contract(value="_, null, _ -> param3; _, !null, _ -> !null")
    public static List<Expression> parseJson(boolean isForRotation, @Nullable JsonElement element, @NotNull List<Expression> defaultValue) {
        ArrayList<FloatExpression> expressions;
        if (element == null) {
            return defaultValue;
        }
        try (MolangParser parser = MolangParser.parser((String)element.getAsString());){
            ArrayList<FloatExpression> expressions1 = parser.parseAll();
            if (expressions1.size() == 1 && isForRotation && IsConstantExpression.test((Expression)((Expression)expressions1.getFirst()))) {
                expressions = new ArrayList<FloatExpression>();
                expressions.add(FloatExpression.of((double)Math.toRadians(MOCHA_ENGINE.eval(expressions1))));
            } else {
                expressions = expressions1;
            }
        }
        catch (IOException e) {
            PlayerAnimLib.LOGGER.error("Failed to compile molang '{}'!", (Object)element, (Object)e);
            return defaultValue;
        }
        return expressions;
    }

    public static MochaEngine<AnimationController> createNewEngine(AnimationController controller) {
        MochaEngine<AnimationController> engine = MolangLoader.createBaseEngine(controller);
        QueryBinding<AnimationController> queryBinding = new QueryBinding<AnimationController>(controller);
        MolangLoader.setDoubleQuery(queryBinding, "anim_time", AnimationController::getAnimationTime);
        MolangLoader.setDoubleQuery(queryBinding, "controller_speed", AnimationController::getAnimationSpeed);
        MolangEvent.MOLANG_EVENT.invoker().registerMolangQueries(controller, engine, queryBinding);
        queryBinding.block();
        engine.scope().set("query", queryBinding);
        engine.scope().set("q", queryBinding);
        return engine;
    }

    public static MochaEngine<?> createNewEngine() {
        return MolangLoader.createNewEngine(null);
    }

    public static <T> MochaEngine<T> createBaseEngine(T entity) {
        MochaEngine engine = MochaEngine.createStandard(entity);
        engine.handleParseExceptions(HANDLER);
        engine.warnOnReflectiveFunctionUsage(true);
        engine.scope().set("math", (Value)new MochaMathExtensions(engine.scope().getProperty("math")));
        return engine;
    }

    public static <T> boolean setDoubleQuery(QueryBinding<T> binding, String name, ToDoubleFunction<T> value) {
        return MolangLoader.setControllerQuery(binding, name, controller -> NumberValue.of((double)value.applyAsDouble(controller)));
    }

    public static <T> boolean setBoolQuery(QueryBinding<T> binding, String name, Function<T, Boolean> value) {
        return MolangLoader.setControllerQuery(binding, name, controller -> Value.of((boolean)((Boolean)value.apply(controller))));
    }

    public static <T> boolean setControllerQuery(QueryBinding<T> binding, String name, Function<T, Value> value) {
        return binding.set(name, (Value)((team.unnamed.mocha.runtime.value.Function)(ctx, args) -> (Value)value.apply(ctx.entity())));
    }

    public static boolean isConstant(List<Expression> expressions) {
        return expressions.stream().anyMatch(IsConstantExpression::test);
    }
}


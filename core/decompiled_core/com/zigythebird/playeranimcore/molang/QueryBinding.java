/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  team.unnamed.mocha.parser.ast.Expression
 *  team.unnamed.mocha.runtime.ExecutionContext
 *  team.unnamed.mocha.runtime.value.Function
 *  team.unnamed.mocha.runtime.value.MutableObjectBinding
 *  team.unnamed.mocha.runtime.value.ObjectProperty
 *  team.unnamed.mocha.runtime.value.Value
 */
package com.zigythebird.playeranimcore.molang;

import com.zigythebird.playeranimcore.PlayerAnimLib;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.unnamed.mocha.parser.ast.Expression;
import team.unnamed.mocha.runtime.ExecutionContext;
import team.unnamed.mocha.runtime.value.Function;
import team.unnamed.mocha.runtime.value.MutableObjectBinding;
import team.unnamed.mocha.runtime.value.ObjectProperty;
import team.unnamed.mocha.runtime.value.Value;

public class QueryBinding<T>
extends MutableObjectBinding
implements ExecutionContext<T> {
    private final T entity;

    public QueryBinding(T entity) {
        this.entity = entity;
    }

    @Nullable
    public ObjectProperty getProperty(@NotNull String name) {
        ObjectProperty property = super.getProperty(name);
        if (property == null) {
            return null;
        }
        Value value = property.value();
        if (value instanceof Function) {
            Function function = (Function)value;
            if (!property.constant()) {
                try {
                    return ObjectProperty.property((Value)Objects.requireNonNull(function.evaluate((ExecutionContext)this)), (boolean)false);
                }
                catch (Throwable th) {
                    PlayerAnimLib.LOGGER.warn("Failed to evaluate function property '{}'", (Object)name, (Object)th);
                }
            }
        }
        return property;
    }

    public T entity() {
        return this.entity;
    }

    @Nullable
    public Value eval(@NotNull Expression expression) {
        throw new UnsupportedOperationException();
    }

    @Nullable
    public Object flag() {
        throw new UnsupportedOperationException();
    }

    public void flag(@Nullable Object flag) {
        throw new UnsupportedOperationException();
    }
}


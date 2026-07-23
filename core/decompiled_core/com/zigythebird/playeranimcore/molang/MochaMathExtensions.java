/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  team.unnamed.mocha.runtime.binding.Binding
 *  team.unnamed.mocha.runtime.value.ObjectProperty
 *  team.unnamed.mocha.runtime.value.ObjectValue
 *  team.unnamed.mocha.runtime.value.Value
 *  team.unnamed.mocha.util.CaseInsensitiveStringHashMap
 */
package com.zigythebird.playeranimcore.molang;

import com.zigythebird.playeranimcore.easing.EasingType;
import java.util.Locale;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.unnamed.mocha.runtime.binding.Binding;
import team.unnamed.mocha.runtime.value.ObjectProperty;
import team.unnamed.mocha.runtime.value.ObjectValue;
import team.unnamed.mocha.runtime.value.Value;
import team.unnamed.mocha.util.CaseInsensitiveStringHashMap;

@Binding(value={"math"})
public class MochaMathExtensions
implements ObjectValue {
    private final Map<String, ObjectProperty> entries = new CaseInsensitiveStringHashMap();
    @Nullable
    private final ObjectValue mochaMath;

    public MochaMathExtensions(@Nullable ObjectProperty property) {
        this((ObjectValue)property.value());
    }

    public MochaMathExtensions(@Nullable ObjectValue mochaMath) {
        this.mochaMath = mochaMath;
        for (EasingType type : EasingType.values()) {
            String name = type.name().toLowerCase(Locale.ROOT);
            if (!name.startsWith("ease_")) continue;
            this.setFunction(name, type);
        }
    }

    public boolean set(@NotNull String name, @Nullable Value value) {
        return this.entries.put(name, ObjectProperty.property((Value)value, (boolean)false)) == null;
    }

    @Nullable
    public ObjectProperty getProperty(@NotNull String name) {
        ObjectProperty extension = this.entries.get(name);
        if (extension == null && this.mochaMath != null) {
            return this.mochaMath.getProperty(name);
        }
        return extension;
    }
}


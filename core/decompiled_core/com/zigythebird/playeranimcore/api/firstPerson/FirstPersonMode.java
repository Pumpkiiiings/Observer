/*
 * Decompiled with CFR 0.152.
 */
package com.zigythebird.playeranimcore.api.firstPerson;

public enum FirstPersonMode {
    NONE(false),
    VANILLA(true),
    THIRD_PERSON_MODEL(true),
    DISABLED(false);

    private final boolean enabled;

    public boolean isEnabled() {
        return this.enabled;
    }

    private FirstPersonMode(boolean enabled) {
        this.enabled = enabled;
    }
}


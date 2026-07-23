/*
 * Decompiled with CFR 0.152.
 */
package com.zigythebird.playeranimcore.enums;

public enum State {
    RUNNING(true),
    PAUSED(true),
    STOPPED(false);

    private final boolean isActive;

    public boolean isActive() {
        return this.isActive;
    }

    private State(boolean isActive) {
        this.isActive = isActive;
    }
}


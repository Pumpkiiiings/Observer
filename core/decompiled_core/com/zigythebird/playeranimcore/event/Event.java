/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 */
package com.zigythebird.playeranimcore.event;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;

public class Event<T> {
    private final List<T> listeners = new ObjectArrayList();
    private final Invoker<T> invoker;

    public Event(Invoker<T> invoker) {
        this.invoker = invoker;
    }

    public final T invoker() {
        return this.invoker.invoker(this.listeners);
    }

    public void register(T listener) {
        if (listener == null) {
            throw new NullPointerException("listener can not be null");
        }
        this.listeners.add(listener);
    }

    public void unregister(T listener) {
        this.listeners.remove(listener);
    }

    @FunctionalInterface
    public static interface Invoker<T> {
        public T invoker(Iterable<T> var1);
    }
}


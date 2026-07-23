/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  team.unnamed.mocha.MochaEngine
 */
package com.zigythebird.playeranimcore.event;

import com.zigythebird.playeranimcore.animation.AnimationController;
import com.zigythebird.playeranimcore.event.Event;
import com.zigythebird.playeranimcore.molang.QueryBinding;
import team.unnamed.mocha.MochaEngine;

public class MolangEvent {
    public static final Event<MolangEventInterface> MOLANG_EVENT = new Event<MolangEventInterface>(listeners -> (controller, engine, queryBinding) -> {
        for (MolangEventInterface listener : listeners) {
            listener.registerMolangQueries(controller, (MochaEngine<AnimationController>)engine, queryBinding);
        }
    });

    @FunctionalInterface
    public static interface MolangEventInterface {
        public void registerMolangQueries(AnimationController var1, MochaEngine<AnimationController> var2, QueryBinding<AnimationController> var3);
    }
}


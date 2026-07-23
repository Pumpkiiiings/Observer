/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  team.unnamed.mocha.runtime.standard.MochaMath
 */
package com.zigythebird.playeranimcore.math;

import team.unnamed.mocha.runtime.standard.MochaMath;

public class MathHelper {
    public static float cos(float a) {
        return (float)Math.cos(a);
    }

    public static float cosFromSin(float sin, float angle) {
        float cos = MochaMath.sqrt((float)(1.0f - sin * sin));
        float a = angle + 1.5707964f;
        float b = a - (float)((int)(a / ((float)Math.PI * 2))) * ((float)Math.PI * 2);
        if ((double)b < 0.0) {
            b += (float)Math.PI * 2;
        }
        return b >= (float)Math.PI ? -cos : cos;
    }

    public static boolean absEqualsOne(float r) {
        return (Float.floatToRawIntBits(r) & Integer.MAX_VALUE) == 1065353216;
    }

    public static float safeAsin(float r) {
        return r <= -1.0f ? -1.5707964f : (float)(r >= 1.0f ? 1.5707963705062866 : Math.asin(r));
    }

    public static float length(float x, float y, float z, float w) {
        return MochaMath.sqrt((float)Math.fma(x, x, Math.fma(y, y, Math.fma(z, z, w * w))));
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package com.zigythebird.playeranimcore.math;

import java.util.Objects;

public record Vec3f(float x, float y, float z) {
    public static final Vec3f ZERO = new Vec3f(0.0f, 0.0f, 0.0f);
    public static final Vec3f ONE = new Vec3f(1.0f, 1.0f, 1.0f);

    public Vec3f mul(float scalar) {
        return new Vec3f(this.x * scalar, this.y * scalar, this.z * scalar);
    }

    public Vec3f add(Vec3f other) {
        return new Vec3f(this.x + other.x, this.y + other.y, this.z + other.z);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Vec3f)) {
            return false;
        }
        Vec3f vec = (Vec3f)o;
        return Objects.equals(Float.valueOf(this.x), Float.valueOf(vec.x)) && Objects.equals(Float.valueOf(this.y), Float.valueOf(vec.y)) && Objects.equals(Float.valueOf(this.z), Float.valueOf(vec.z));
    }

    @Override
    public String toString() {
        return "Vec3f[" + this.x + "; " + this.y + "; " + this.z + "]";
    }

    @Override
    public int hashCode() {
        return Objects.hash(Float.valueOf(this.x), Float.valueOf(this.y), Float.valueOf(this.z));
    }
}


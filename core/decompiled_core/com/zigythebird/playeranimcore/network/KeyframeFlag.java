/*
 * Decompiled with CFR 0.152.
 */
package com.zigythebird.playeranimcore.network;

enum KeyframeFlag {
    IS_CONSTANT(6),
    HAS_EASING_ARGS(6),
    LENGTH_ZERO(6),
    LENGTH_ONE(6);

    final int sinceVersion;
    final int mask = 1 << this.ordinal();

    private KeyframeFlag(int sinceVersion) {
        this.sinceVersion = sinceVersion;
    }

    static int flagBitsForVersion(int version) {
        int bits = 0;
        for (KeyframeFlag flag : KeyframeFlag.values()) {
            if (flag.sinceVersion > version) continue;
            bits = flag.ordinal() + 1;
        }
        return bits;
    }

    static int pack(int easingId, int flags, int version) {
        return easingId << KeyframeFlag.flagBitsForVersion(version) | flags;
    }

    static int unpackEasing(int combined, int version) {
        return combined >>> KeyframeFlag.flagBitsForVersion(version);
    }

    static int unpackFlags(int combined, int version) {
        return combined & (1 << KeyframeFlag.flagBitsForVersion(version)) - 1;
    }

    static {
        int lastVersion = 0;
        for (KeyframeFlag flag : KeyframeFlag.values()) {
            if (flag.sinceVersion < lastVersion) {
                throw new AssertionError((Object)("KeyframeFlag." + flag.name() + " sinceVersion " + flag.sinceVersion + " is less than previous " + lastVersion + ". Flags must be ordered by version."));
            }
            lastVersion = flag.sinceVersion;
        }
    }
}


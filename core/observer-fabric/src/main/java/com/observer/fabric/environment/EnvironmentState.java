package com.observer.fabric.environment;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class EnvironmentState {
    
    // True Darkness
    public static boolean trueDarknessEnabled = false;

    // Fog Color Override
    public static boolean hasFogOverride = false;
    public static int fogR = 0;
    public static int fogG = 0;
    public static int fogB = 0;

    // Sky Color Override
    public static boolean hasSkyOverride = false;
    public static int skyR = 0;
    public static int skyG = 0;
    public static int skyB = 0;

    // Moon Color Override
    public static boolean hasMoonOverride = false;
    public static int moonR = 0;
    public static int moonG = 0;
    public static int moonB = 0;

    // Dense Fog
    public static boolean denseFogEnabled = false;
    public static float fogStart = 0f;
    public static float fogEnd = 0f;
    public static float fogAlpha = 1.0f;
    
    public static void resetAll() {
        trueDarknessEnabled = false;
        hasFogOverride = false;
        hasSkyOverride = false;
        hasMoonOverride = false;
        denseFogEnabled = false;
    }
}

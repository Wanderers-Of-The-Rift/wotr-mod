package com.wanderersoftherift.wotr.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ClientConfig {
    public static final ModConfigSpec SPEC;

    // Accessibility Settings
    public static final ModConfigSpec.BooleanValue ACCESSIBILITY_FLASHING_LIGHTS;
    public static final ModConfigSpec.BooleanValue ACCESSIBILITY_ARACHNOPHOBIA;
    public static final ModConfigSpec.BooleanValue ACCESSIBILITY_MISOPHONIA;
    public static final ModConfigSpec.BooleanValue ACCESSIBILITY_TRYPOPHOBIA; // To-be-determined if this will be used
                                                                              // at all
    public static final ModConfigSpec.BooleanValue ACCESSIBILITY_HIGH_CONTRAST;
    public static final ModConfigSpec.BooleanValue ACCESSIBILITY_REDUCED_MOTION;
    public static final ModConfigSpec.BooleanValue ACCESSIBILITY_HARD_OF_HEARING; // I am unsure what this should change
                                                                                  // currently

    // Rift Map Settings
    public static final ModConfigSpec.BooleanValue MOUSE_MODE;
    public static final ModConfigSpec.DoubleValue LERP_SPEED;

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    static {
        BUILDER.comment(" == Wotr Client Configs ==");

        // Pushing Client configs related to accessibility
        BUILDER.push(" == Accessibility == ");
        ACCESSIBILITY_FLASHING_LIGHTS = BUILDER.comment(" Whether flashing-light effects should be disabled")
                .define("accessibilityFlashingLights", false);
        ACCESSIBILITY_ARACHNOPHOBIA = BUILDER.comment(" Whether Spiders should be replaced with something else")
                .define("accessibilityArachnophobia", false);
        ACCESSIBILITY_MISOPHONIA = BUILDER.comment(" Whether certain sounds should be replaced with something else")
                .define("accessibilityMisophonia", false);
        ACCESSIBILITY_TRYPOPHOBIA = BUILDER.comment(" Whether certain textures should be replaced with something else")
                .define("accessibilityTrypophobia", false);
        ACCESSIBILITY_HIGH_CONTRAST = BUILDER
                .comment(" Whether GUI & HUD elements should be replaced with higher contrast for better visibility")
                .define("accessibilityHighContrast", false);
        ACCESSIBILITY_REDUCED_MOTION = BUILDER.comment(" Whether motion should be reduced")
                .define("accessibilityReducedMotion", false);
        ACCESSIBILITY_HARD_OF_HEARING = BUILDER.comment(" Whether certain sounds should be upped")
                .define("accessibilityHardOfHearing", false);
        BUILDER.pop();

        BUILDER.push(" == Rift Map == ");
        MOUSE_MODE = BUILDER.comment(" Whether to use the Whale mouse mode").define("mouseMode", false);
        LERP_SPEED = BUILDER.comment(" What speed the map should lerp at. 0 = Off")
                .defineInRange("lerpSpeed", 1.0, 0.0, 2.0);

        SPEC = BUILDER.build();
    }
}

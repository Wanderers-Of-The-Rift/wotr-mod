package com.wanderersoftherift.wotr.config;

import com.wanderersoftherift.wotr.gui.config.HudElementConfig;
import com.wanderersoftherift.wotr.gui.config.ScreenAnchor;
import com.wanderersoftherift.wotr.gui.config.UIOrientation;
import net.neoforged.neoforge.common.ModConfigSpec;

public class ClientConfig {
    public static final ModConfigSpec SPEC;

    // Accessibility Settings
    public static final ModConfigSpec.BooleanValue ACCESSIBILITY_FLASHING_LIGHTS;
    public static final ModConfigSpec.BooleanValue ACCESSIBILITY_ARACHNOPHOBIA;
    public static final ModConfigSpec.BooleanValue ACCESSIBILITY_MISOPHONIA;
    // To-be-determined if this will be used at all
    public static final ModConfigSpec.BooleanValue ACCESSIBILITY_TRYPOPHOBIA;
    public static final ModConfigSpec.BooleanValue ACCESSIBILITY_HIGH_CONTRAST;
    public static final ModConfigSpec.BooleanValue ACCESSIBILITY_REDUCED_MOTION;
    // I am unsure what this should change currently
    public static final ModConfigSpec.BooleanValue ACCESSIBILITY_HARD_OF_HEARING;

    // Rift Map Settings
    public static final ModConfigSpec.BooleanValue MOUSE_MODE;
    public static final ModConfigSpec.DoubleValue LERP_SPEED;

    public static final ModConfigSpec.ConfigValue<String> HUD_PRESET;

    // WotR HUD
    public static final HudElementConfig ABILITY_BAR;
    public static final HudElementConfig MANA_BAR;
    public static final HudElementConfig EFFECT_DISPLAY;
    public static final HudElementConfig OBJECTIVE;

    // Vanilla HUD
    public static final HudElementConfig HOT_BAR;
    public static final HudElementConfig EXPERIENCE_BAR;
    public static final HudElementConfig HEALTH_ARMOR;
    public static final HudElementConfig FOOD_LEVEL;
    public static final HudElementConfig AIR_LEVEL;
    public static final HudElementConfig EXPERIENCE_LEVEL;
    public static final HudElementConfig VANILLA_EFFECTS;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.comment(" == Wotr Client Configs ==");

        // Pushing Client configs related to accessibility
        builder.push(" == Accessibility == ");
        ACCESSIBILITY_FLASHING_LIGHTS = builder.comment(" Whether flashing-light effects should be disabled")
                .define("accessibilityFlashingLights", false);
        ACCESSIBILITY_ARACHNOPHOBIA = builder.comment(" Whether Spiders should be replaced with something else")
                .define("accessibilityArachnophobia", false);
        ACCESSIBILITY_MISOPHONIA = builder.comment(" Whether certain sounds should be replaced with something else")
                .define("accessibilityMisophonia", false);
        ACCESSIBILITY_TRYPOPHOBIA = builder.comment(" Whether certain textures should be replaced with something else")
                .define("accessibilityTrypophobia", false);
        ACCESSIBILITY_HIGH_CONTRAST = builder
                .comment(" Whether GUI & HUD elements should be replaced with higher contrast for better visibility")
                .define("accessibilityHighContrast", false);
        ACCESSIBILITY_REDUCED_MOTION = builder.comment(" Whether motion should be reduced")
                .define("accessibilityReducedMotion", false);
        ACCESSIBILITY_HARD_OF_HEARING = builder.comment(" Whether certain sounds should be upped")
                .define("accessibilityHardOfHearing", false);
        builder.pop();

        builder.push(" == Rift Map == ");
        MOUSE_MODE = builder.comment(" Whether to use the Whale mouse mode").define("mouseMode", false);
        LERP_SPEED = builder.comment(" What speed the map should lerp at. 0 = Off")
                .defineInRange("lerpSpeed", 1.0, 0.0, 2.0);
        builder.pop();

        builder.push(" == HUD == ");
        HUD_PRESET = builder.define("hud_preset", "wotr:default");
        builder.pop();

        ABILITY_BAR = new HudElementConfig.Builder("Ability Bar", "abilityBar").anchor(ScreenAnchor.TOP_LEFT)
                .rotates(UIOrientation.VERTICAL)
                .build(builder);
        MANA_BAR = new HudElementConfig.Builder("Mana Bar", "manaBar").anchor(ScreenAnchor.TOP_LEFT)
                .offset(25, 0)
                .rotates(UIOrientation.VERTICAL)
                .build(builder);
        EFFECT_DISPLAY = new HudElementConfig.Builder("Effect Display", "effectDisplay").anchor(ScreenAnchor.TOP_LEFT)
                .offset(31, 0)
                .rotates(UIOrientation.VERTICAL)
                .build(builder);
        OBJECTIVE = new HudElementConfig.Builder("Objective", "objective").anchor(ScreenAnchor.TOP_CENTER)
                .offset(0, 25)
                .build(builder);

        HOT_BAR = new HudElementConfig.Builder("Vanilla Hot Bar", "hotBar").anchor(ScreenAnchor.BOTTOM_CENTER)
                .offset(0, 0)
                .build(builder);
        EXPERIENCE_BAR = new HudElementConfig.Builder("Vanilla Experience Bar", "xpBar")
                .anchor(ScreenAnchor.BOTTOM_CENTER)
                .offset(0, -24)
                .rotates(UIOrientation.HORIZONTAL)
                .build(builder);
        HEALTH_ARMOR = new HudElementConfig.Builder("Vanilla Health and Armor", "healthArmor")
                .anchor(ScreenAnchor.BOTTOM_CENTER)
                .offset(-51, -30)
                .build(builder);
        FOOD_LEVEL = new HudElementConfig.Builder("Vanilla Food", "food").anchor(ScreenAnchor.BOTTOM_CENTER)
                .offset(50, -30)
                .build(builder);
        AIR_LEVEL = new HudElementConfig.Builder("Vanilla Air", "air").anchor(ScreenAnchor.BOTTOM_CENTER)
                .offset(50, -40)
                .rotates(UIOrientation.HORIZONTAL)
                .build(builder);
        EXPERIENCE_LEVEL = new HudElementConfig.Builder("Vanilla Experience Level", "xpLevel")
                .anchor(ScreenAnchor.BOTTOM_CENTER)
                .offset(0, -26)
                .build(builder);
        VANILLA_EFFECTS = new HudElementConfig.Builder("Vanilla Effects", "effects").anchor(ScreenAnchor.TOP_RIGHT)
                .build(builder);

        SPEC = builder.build();
    }
}

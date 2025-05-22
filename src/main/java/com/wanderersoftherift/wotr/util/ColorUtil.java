package com.wanderersoftherift.wotr.util;

import net.minecraft.util.Mth;

import java.util.Arrays;
import java.util.List;

/**
 * Any common color constants we use (beyond what is in {@link net.minecraft.ChatFormatting})
 */
public final class ColorUtil {
    public static final int OFF_BLACK = 0x404040;

    public static final List<String> RAINBOW = Arrays.asList(
            "#FF0000", "#FF7F00", "#FFFF00", "#00FF00", "#0000FF", "#4B0082", "#8B00FF"
    );

    public static int blendColors(int firstColor, int secondColor, float firstWeight) {
        float weight1 = Mth.clamp(firstWeight, 0F, 1F);
        float weight2 = 1F - weight1;

        int alpha1 = (firstColor >>> 24) & 0xFF;
        int red1 = (firstColor >>> 16) & 0xFF;
        int green1 = (firstColor >>> 8) & 0xFF;
        int blue1 = firstColor & 0xFF;

        int alpha2 = (secondColor >>> 24) & 0xFF;
        int red2 = (secondColor >>> 16) & 0xFF;
        int green2 = (secondColor >>> 8) & 0xFF;
        int blue2 = secondColor & 0xFF;
        int blendedAlpha = Mth.clamp(Math.round(alpha1 * weight1 + alpha2 * weight2), 0, 255);
        int blendedRed = Mth.clamp(Math.round(red1 * weight1 + red2 * weight2), 0, 255);
        int blendedGreen = Mth.clamp(Math.round(green1 * weight1 + green2 * weight2), 0, 255);
        int blendedBlue = Mth.clamp(Math.round(blue1 * weight1 + blue2 * weight2), 0, 255);

        return (blendedAlpha << 24) | (blendedRed << 16) | (blendedGreen << 8) | blendedBlue;
    }

    public static int brightenColor(int color, float factor) {
        int a = (color >>> 24) & 0xFF;
        int r = (color >>> 16) & 0xFF;
        int g = (color >>> 8) & 0xFF;
        int b = color & 0xFF;

        r = Mth.clamp(Math.round(r * factor), 0, 255);
        g = Mth.clamp(Math.round(g * factor), 0, 255);
        b = Mth.clamp(Math.round(b * factor), 0, 255);

        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}

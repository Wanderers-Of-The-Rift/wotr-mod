package com.wanderersoftherift.wotr.util;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public class ComponentUtil {

    /**
     * @param components
     * @return A single component combining the provided components, separated by newlines
     */
    public static MutableComponent joinWithNewLines(@NotNull Collection<Component> components) {
        MutableComponent result = Component.empty();
        boolean first = true;
        for (Component component : components) {
            if (!first) {
                result = result.append("\n");
            }
            result = result.append(component);
            first = false;
        }
        return result;
    }

    public static MutableComponent blendComponent(Component baseComponent, float interval, List<String> hexStrings) {
        GradientMixer colorBlender = new GradientMixer(1.0F);

        for (String hex : hexStrings) {
            colorBlender.add(TextColor.parseColor(hex).getOrThrow().getValue(), interval);
        }

        float time = (float) Minecraft.getInstance().level.getGameTime();
        int blendedColor = colorBlender.getColor(time);

        return baseComponent.copy().setStyle(baseComponent.getStyle().withColor(blendedColor));
    }

    /**
     *
     * @param base      base Component
     * @param color     color to be used
     * @param frequency how fast the wave moves across the text
     * @param amplitude how much brighter it gets at peak
     * @return a MutableComponent that brightens/darkens its color as time progresses
     */
    public static MutableComponent wavingComponent(Component base, int color, float frequency, float amplitude) {
        String text = base.getString();
        MutableComponent result = Component.empty();

        float time = (float) Minecraft.getInstance().level.getGameTime();

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            // Compute brightness factor in a wave pattern
            float wave = (float) Math.sin((time - i) * frequency) * amplitude + 1f;

            result.append(Component.literal(String.valueOf(c))
                    .withStyle(base.getStyle().withColor(ColorUtil.brightenColor(color, wave))));
        }

        return result;
    }
}

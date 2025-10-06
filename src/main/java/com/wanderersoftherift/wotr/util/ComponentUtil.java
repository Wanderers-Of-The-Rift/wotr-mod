package com.wanderersoftherift.wotr.util;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public class ComponentUtil {

    public static MutableComponent mutable(Component component) {
        if (component instanceof MutableComponent mutableComponent) {
            return mutableComponent;
        }
        return component.copy();
    }

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
        MutableComponent result = Component.empty();

        float time = (float) Minecraft.getInstance().level.getGameTime();

        var sin = Math.sin(frequency * time);
        var cos = Math.cos(frequency * time);
        for (var component : base.toFlatList()) {
            var text = component.getString();
            var baseStyle = component.getStyle();
            var baseColor = baseStyle.getColor();
            if (baseColor == null) {
                baseColor = TextColor.fromRgb(0xffffff);
            }
            for (int i = 0; i < text.length(); i++) {
                char c = text.charAt(i);

                // Compute brightness factor in a wave pattern
                cos += frequency * sin * 1.35;
                sin -= frequency * cos * 1.35;
                float wave = ((float) -sin) * amplitude + 1f;

                result.append(Component.literal(String.valueOf(c))
                        .withStyle(baseStyle.withColor(ColorUtil.brightenColor(baseColor.getValue(), wave))));
            }
        }

        return result;
    }
}

package com.wanderersoftherift.wotr.abilities.upgrade;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Indicates whether a modifier is positive or negative, and a decrease or an increase
 */
public enum ModifierIndicator implements StringRepresentable {
    NEUTRAL("neutral", null),
    POSITIVE_INCREASE("positive_increase", WanderersOfTheRift.id("textures/ability/upgrade/positive-increase.png")),
    POSITIVE_DECREASE("positive_decrease", WanderersOfTheRift.id("textures/ability/upgrade/positive-decrease.png")),
    NEGATIVE_INCREASE("negative_increase", WanderersOfTheRift.id("textures/ability/upgrade/negative-increase.png")),
    NEGATIVE_DECREASE("negative_decrease", WanderersOfTheRift.id("textures/ability/upgrade/negative-decrease.png"));

    public static final StringRepresentable.StringRepresentableCodec<ModifierIndicator> CODEC = StringRepresentable
            .fromEnum(ModifierIndicator::values);

    private final String representation;
    private final ResourceLocation icon;

    ModifierIndicator(String representation, ResourceLocation icon) {
        this.representation = representation;
        this.icon = icon;
    }

    @Override
    public @NotNull String getSerializedName() {
        return representation;
    }

    public @Nullable ResourceLocation getIcon() {
        return icon;
    }
}

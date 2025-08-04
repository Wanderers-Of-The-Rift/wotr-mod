package com.wanderersoftherift.wotr.modifier.source;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record AbilityModifierSource(UUID uuid, int index) implements ModifierSource {
    @Override
    public @NotNull String getSerializedName() {
        return "ability_" + uuid.toString() + "_" + index;
    }
}

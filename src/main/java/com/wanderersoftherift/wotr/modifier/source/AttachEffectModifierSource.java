package com.wanderersoftherift.wotr.modifier.source;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * A modifier sourced from an attach effect
 * 
 * @param uuid  The attach effect id
 * @param index The index of the modifier
 */
public record AttachEffectModifierSource(UUID uuid, int index) implements ModifierSource {
    @Override
    public @NotNull String getSerializedName() {
        return "attach_effect_" + uuid.toString() + "_" + index;
    }
}

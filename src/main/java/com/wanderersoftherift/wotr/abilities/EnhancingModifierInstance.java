package com.wanderersoftherift.wotr.abilities;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.attachment.AbilityEnhancements;
import com.wanderersoftherift.wotr.modifier.ModifierInstance;
import com.wanderersoftherift.wotr.modifier.source.ModifierSource;

public record EnhancingModifierInstance(ModifierInstance modifier, ModifierSource originalSource, int originalIndex) {

    public static final Codec<EnhancingModifierInstance> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    ModifierInstance.CODEC.fieldOf("modifier").forGetter(EnhancingModifierInstance::modifier),
                    ModifierSource.DIRECT_CODEC.fieldOf("original_source")
                            .forGetter(EnhancingModifierInstance::originalSource),
                    Codec.INT.fieldOf("effect_index").forGetter(EnhancingModifierInstance::originalIndex)
            ).apply(instance, EnhancingModifierInstance::new)
    );

    public static EnhancingModifierInstance create(
            AbilityEnhancements.AbilityEnhancementValue value,
            AbilityEnhancements.AbilityEnhancementKey key) {
        return new EnhancingModifierInstance(
                new ModifierInstance(value.modifier().modifier(), value.modifier().tier(), (float) value.roll()),
                key.source(), key.effectIndex());
    }
}

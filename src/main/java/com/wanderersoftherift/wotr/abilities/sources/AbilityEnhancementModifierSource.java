package com.wanderersoftherift.wotr.abilities.sources;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.modifier.effect.AbstractModifierEffect;
import com.wanderersoftherift.wotr.modifier.effect.EnhanceAbilityModifierEffect;
import com.wanderersoftherift.wotr.modifier.source.ModifierSource;
import com.wanderersoftherift.wotr.serialization.DualCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;

import java.util.Collections;
import java.util.List;

public record AbilityEnhancementModifierSource(ModifierSource baseSource, int effectIndex) implements ModifierSource {
    public static final DualCodec<AbilityEnhancementModifierSource> TYPE = new DualCodec<>(
            RecordCodecBuilder.mapCodec(instance -> instance.group(
                    ModifierSource.DIRECT_CODEC.fieldOf("modifier_source")
                            .forGetter(AbilityEnhancementModifierSource::baseSource),
                    Codec.INT.fieldOf("effect_index").forGetter(AbilityEnhancementModifierSource::effectIndex)
            ).apply(instance, AbilityEnhancementModifierSource::new)), StreamCodec.composite(
                    ModifierSource.STREAM_CODEC, AbilityEnhancementModifierSource::baseSource, ByteBufCodecs.INT,
                    AbilityEnhancementModifierSource::effectIndex, AbilityEnhancementModifierSource::new
            )
    );

    @Override
    public DualCodec<? extends ModifierSource> getType() {
        return TYPE;
    }

    @Override
    public List<AbstractModifierEffect> getModifierEffects(Entity entity) {
        var baseEffect = baseSource.getModifierEffects(entity).get(effectIndex);
        if (baseEffect instanceof EnhanceAbilityModifierEffect enhancing) {
            var tier = enhancing.modifier().value().getModifierTier(enhancing.tier());
            if (tier != null) {
                return tier.getModifierEffects();
            }
        }
        return Collections.emptyList();
    }

    @Override
    public String getSerializedName() {
        return "enhance_ability_" + baseSource.getSerializedName() + "_" + effectIndex;
    }
}

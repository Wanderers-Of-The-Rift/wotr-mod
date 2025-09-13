package com.wanderersoftherift.wotr.modifier.source;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.sources.AbilitySource;
import com.wanderersoftherift.wotr.modifier.effect.ModifierEffect;
import com.wanderersoftherift.wotr.serialization.DualCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record AbilityUpgradeModifierSource(AbilitySource ability, int selection) implements ModifierSource {

    public static final DualCodec<AbilityUpgradeModifierSource> TYPE = new DualCodec<>(
            RecordCodecBuilder.mapCodec(instance -> instance.group(
                    AbilitySource.DIRECT_CODEC.fieldOf("stack").forGetter(AbilityUpgradeModifierSource::ability),
                    Codec.INT.fieldOf("selection").forGetter(AbilityUpgradeModifierSource::selection)
            ).apply(instance, AbilityUpgradeModifierSource::new)), StreamCodec.composite(
                    AbilitySource.STREAM_CODEC, AbilityUpgradeModifierSource::ability, ByteBufCodecs.INT,
                    AbilityUpgradeModifierSource::selection, AbilityUpgradeModifierSource::new
            )
    );

    @Override
    public DualCodec<? extends ModifierSource> getType() {
        return TYPE;
    }

    @Override
    public @NotNull String getSerializedName() {
        return "ability_upgrade_" + ability.getSerializedName() + "_" + selection;
    }

    @Override
    public List<ModifierEffect> getModifierEffects(Entity entity) {
        return ability.upgrades(entity).get(selection).value().modifierEffects();
    }
}

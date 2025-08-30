package com.wanderersoftherift.wotr.abilities.sources;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.Ability;
import com.wanderersoftherift.wotr.abilities.upgrade.AbilityUpgradePool;
import com.wanderersoftherift.wotr.core.inventory.slot.WotrEquipmentSlot;
import com.wanderersoftherift.wotr.item.ability.AbilityModifier;
import com.wanderersoftherift.wotr.modifier.source.ModifierSource;
import com.wanderersoftherift.wotr.serialization.DualCodec;
import net.minecraft.core.Holder;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public record ModifierAbilitySource(ModifierSource base, int effectIndex) implements AbilitySource {

    public static final DualCodec<com.wanderersoftherift.wotr.abilities.sources.ModifierAbilitySource> TYPE = new DualCodec<>(
            RecordCodecBuilder.mapCodec(instance -> instance.group(
                    ModifierSource.DIRECT_CODEC.fieldOf("modifier_source")
                            .forGetter(com.wanderersoftherift.wotr.abilities.sources.ModifierAbilitySource::base),
                    Codec.INT.fieldOf("effect_index")
                            .forGetter(com.wanderersoftherift.wotr.abilities.sources.ModifierAbilitySource::effectIndex)
            ).apply(instance, com.wanderersoftherift.wotr.abilities.sources.ModifierAbilitySource::new)),
            StreamCodec.composite(
                    ModifierSource.STREAM_CODEC,
                    com.wanderersoftherift.wotr.abilities.sources.ModifierAbilitySource::base, ByteBufCodecs.INT,
                    com.wanderersoftherift.wotr.abilities.sources.ModifierAbilitySource::effectIndex,
                    com.wanderersoftherift.wotr.abilities.sources.ModifierAbilitySource::new
            )
    );

    @Override
    public Holder<Ability> getAbility(Entity entity) {
        if (base.getModifierEffects(entity).get(effectIndex) instanceof AbilityModifier ability) {
            return ability.providedAbility();
        }
        return null;
    }

    @Override
    public AbilityUpgradePool upgrades(Entity entity) {
        return AbilityUpgradePool.EMPTY; // todo how do we handle upgrades for these abilities
    }

    @Override
    public DualCodec<? extends AbilitySource> getType() {
        return TYPE;
    }

    @Override
    public @Nullable ItemStack getItem(LivingEntity entity) {
        if (base instanceof ModifierSource.ItemModifierSource itemModifierSource) {
            return itemModifierSource.getItem(entity);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public @Nullable WotrEquipmentSlot getLinkedSlot() {
        if (base instanceof ModifierSource.SlotModifierSource slotSource) {
            return slotSource.slot();
        }
        return null;
    }

    @Override
    public @NotNull String getSerializedName() {
        return "modifier_ability_" + base.getSerializedName() + "_" + effectIndex;
    }
}

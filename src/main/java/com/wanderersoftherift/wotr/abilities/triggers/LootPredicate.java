package com.wanderersoftherift.wotr.abilities.triggers;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.init.ability.WotrTrackedAbilityTriggers;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.Optional;

public record LootPredicate(Optional<HolderSet<BlockEntityType<?>>> containerTypes, Optional<ContextKeySet> context,
        Optional<ResourceLocation> lootTable) implements TriggerPredicate<LootTrigger> {

    public static final MapCodec<LootPredicate> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            RegistryCodecs.homogeneousList(Registries.BLOCK_ENTITY_TYPE)
                    .optionalFieldOf("container")
                    .forGetter(LootPredicate::containerTypes),
            LootContextParamSets.CODEC.optionalFieldOf("context").forGetter(LootPredicate::context),
            ResourceLocation.CODEC.optionalFieldOf("loot_table").forGetter(LootPredicate::lootTable)
    ).apply(instance, LootPredicate::new));

    @Override
    public Holder<TrackableTrigger.TriggerType<?>> type() {
        return WotrTrackedAbilityTriggers.LOOT.getDelegate();
    }

    @Override
    public boolean canBeHandledByClient() {
        return false;
    }

    @Override
    public boolean test(LootTrigger trigger) {

        if (context.isPresent() && !trigger.context().equals(context.get())) {
            return false;
        }
        if (lootTable.isPresent() && !trigger.lootTable().equals(lootTable.get())) {
            return false;
        }
        if (containerTypes.isPresent() && !containerTypes.get().contains(trigger.container())) {
            return false;
        }
        return true;
    }
}

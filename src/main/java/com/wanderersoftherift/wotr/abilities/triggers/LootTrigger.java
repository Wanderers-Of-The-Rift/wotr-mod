package com.wanderersoftherift.wotr.abilities.triggers;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.TrackableTrigger;
import com.wanderersoftherift.wotr.init.ability.WotrTrackedAbilityTriggers;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.Optional;

public record LootTrigger(Holder<BlockEntityType<?>> container, ContextKeySet context, ResourceLocation lootTable)
        implements TrackableTrigger {

    private static final MapCodec<LootTrigger> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BuiltInRegistries.BLOCK_ENTITY_TYPE.holderByNameCodec()
                    .fieldOf("container")
                    .forGetter(LootTrigger::container),
            LootContextParamSets.CODEC.fieldOf("context").forGetter(LootTrigger::context),
            ResourceLocation.CODEC.fieldOf("loot_table").forGetter(LootTrigger::lootTable)
    ).apply(instance, LootTrigger::new));
    public static final TriggerType<LootTrigger> TRIGGER_TYPE = new TriggerType<>(CODEC, LootPredicate.CODEC, null);

    @Override
    public TriggerType<?> type() {
        return TRIGGER_TYPE;
    }

    public record LootPredicate(Optional<HolderSet<BlockEntityType<?>>> containerTypes, Optional<ContextKeySet> context,
            Optional<ResourceLocation> lootTable) implements TriggerPredicate<LootTrigger> {

        private static final MapCodec<LootPredicate> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                RegistryCodecs.homogeneousList(Registries.BLOCK_ENTITY_TYPE)
                        .optionalFieldOf("container")
                        .forGetter(LootPredicate::containerTypes),
                LootContextParamSets.CODEC.optionalFieldOf("context").forGetter(LootPredicate::context),
                ResourceLocation.CODEC.optionalFieldOf("loot_table").forGetter(LootPredicate::lootTable)
        ).apply(instance, LootPredicate::new));

        @Override
        public Holder<TriggerType<?>> type() {
            return WotrTrackedAbilityTriggers.LOOT.getDelegate();
        }

        @Override
        public boolean canBeHandledByClient() {
            return false;
        }

        @Override
        public boolean test(LootTrigger trigger) {

            if (context.isPresent() && !trigger.context.equals(context.get())) {
                return false;
            }
            if (lootTable.isPresent() && !trigger.lootTable.equals(lootTable.get())) {
                return false;
            }
            if (containerTypes.isPresent() && !containerTypes.get().contains(trigger.container())) {
                return false;
            }
            return true;
        }
    }
}

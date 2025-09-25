package com.wanderersoftherift.wotr.abilities.triggers;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

public record LootTrigger(Holder<BlockEntityType<?>> container, ContextKeySet context, ResourceLocation lootTable)
        implements TrackableTrigger {

    private static final MapCodec<LootTrigger> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BuiltInRegistries.BLOCK_ENTITY_TYPE.holderByNameCodec()
                    .fieldOf("container")
                    .forGetter(LootTrigger::container),
            LootContextParamSets.CODEC.fieldOf("context").forGetter(LootTrigger::context),
            ResourceLocation.CODEC.fieldOf("loot_table").forGetter(LootTrigger::lootTable)
    ).apply(instance, LootTrigger::new));
    public static final TriggerType<LootTrigger> TRIGGER_TYPE = new TriggerType<>(LootPredicate.CODEC, null);

    @Override
    public TriggerType<?> type() {
        return TRIGGER_TYPE;
    }

}

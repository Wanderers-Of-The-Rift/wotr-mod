package com.wanderersoftherift.wotr.abilities.triggers;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.AbilityContext;
import com.wanderersoftherift.wotr.abilities.attachment.TargetComponent;
import com.wanderersoftherift.wotr.init.WotrDataComponentType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.phys.BlockHitResult;

import static net.minecraft.core.Direction.UP;

public record LootTrigger(BlockPos position, Holder<BlockEntityType<?>> container, ContextKeySet context,
        ResourceLocation lootTable) implements TrackableTrigger {

    private static final MapCodec<LootTrigger> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BlockPos.CODEC.fieldOf("position").forGetter(LootTrigger::position),
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

    @Override
    public void addComponents(AbilityContext context) {
        context.set(WotrDataComponentType.AbilityContextData.TRIGGER_TARGET,
                new TargetComponent(new BlockHitResult(position.getCenter(), UP, position, false)));
    }
}

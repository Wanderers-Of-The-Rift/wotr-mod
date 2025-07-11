package com.wanderersoftherift.wotr.core.guild.quest.goal;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.core.guild.quest.Goal;
import com.wanderersoftherift.wotr.core.guild.quest.GoalDefinition;
import net.minecraft.world.level.storage.loot.LootContext;

import java.util.List;

public record PoolGoalDefinition(List<GoalDefinition> entries) implements GoalDefinition {
    public static final MapCodec<PoolGoalDefinition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            GoalDefinition.DIRECT_CODEC.listOf().fieldOf("entries").forGetter(PoolGoalDefinition::entries)
    ).apply(instance, PoolGoalDefinition::new));

    @Override
    public MapCodec<? extends GoalDefinition> getCodec() {
        return CODEC;
    }

    @Override
    public Goal generateGoal(LootContext context) {
        return entries.get(context.getRandom().nextInt(entries.size())).generateGoal(context);
    }
}

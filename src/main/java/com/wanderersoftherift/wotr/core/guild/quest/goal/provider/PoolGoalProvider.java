package com.wanderersoftherift.wotr.core.guild.quest.goal.provider;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.core.guild.quest.Goal;
import com.wanderersoftherift.wotr.core.guild.quest.GoalProvider;
import net.minecraft.world.level.storage.loot.LootParams;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * A goal provider that holds a pool of possible goals, of which one is selected at random
 *
 * @param entries
 */
public record PoolGoalProvider(List<GoalProvider> entries) implements GoalProvider {
    public static final MapCodec<PoolGoalProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            GoalProvider.DIRECT_CODEC.listOf().fieldOf("entries").forGetter(PoolGoalProvider::entries)
    ).apply(instance, PoolGoalProvider::new));

    @Override
    public MapCodec<? extends GoalProvider> getCodec() {
        return CODEC;
    }

    @Override
    public @NotNull List<Goal> generateGoal(LootParams params) {
        int index = params.getLevel().getRandom().nextInt(entries.size());
        return entries.get(index).generateGoal(params);
    }
}

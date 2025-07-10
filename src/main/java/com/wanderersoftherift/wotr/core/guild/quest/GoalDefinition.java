package com.wanderersoftherift.wotr.core.guild.quest;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import net.minecraft.util.RandomSource;

import java.util.function.Function;

public interface GoalDefinition {
    Codec<GoalDefinition> DIRECT_CODEC = WotrRegistries.GOAL_DEFINITION_TYPES.byNameCodec()
            .dispatch(GoalDefinition::getCodec, Function.identity());

    /**
     * @return The codec used to serialize this goal
     */
    MapCodec<? extends GoalDefinition> getCodec();

    Goal generateGoal(RandomSource random);

}

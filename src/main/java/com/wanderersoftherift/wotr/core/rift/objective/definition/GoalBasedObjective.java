package com.wanderersoftherift.wotr.core.rift.objective.definition;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.core.goal.Goal;
import com.wanderersoftherift.wotr.core.goal.GoalProvider;
import com.wanderersoftherift.wotr.core.rift.RiftConfig;
import com.wanderersoftherift.wotr.core.rift.objective.ObjectiveType;
import com.wanderersoftherift.wotr.core.rift.objective.OngoingObjective;
import com.wanderersoftherift.wotr.core.rift.objective.ongoing.GoalBasedOngoingObjective;
import com.wanderersoftherift.wotr.core.rift.objective.ongoing.NoOngoingObjective;
import com.wanderersoftherift.wotr.init.loot.WotrLootContextParams;
import com.wanderersoftherift.wotr.init.worldgen.WotrRiftConfigDataTypes;
import com.wanderersoftherift.wotr.world.level.levelgen.jigsaw.JigsawListProcessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.List;
import java.util.Optional;

/**
 * An objective build on one or more goals that must be completed
 *
 * @param goals
 * @param processors
 */
public record GoalBasedObjective(List<GoalProvider> goals, List<JigsawListProcessor> processors)
        implements ObjectiveType {
    public static final MapCodec<GoalBasedObjective> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            GoalProvider.DIRECT_CODEC.listOf(1, Integer.MAX_VALUE)
                    .fieldOf("goals")
                    .forGetter(GoalBasedObjective::goals),
            JigsawListProcessor.CODEC.listOf()
                    .optionalFieldOf("jigsaw_processors", List.of())
                    .forGetter(GoalBasedObjective::processors))
            .apply(inst, GoalBasedObjective::new));

    @Override
    public MapCodec<? extends ObjectiveType> getCodec() {
        return CODEC;
    }

    @Override
    public OngoingObjective generate(ServerLevelAccessor level, RiftConfig config) {
        LootParams params = new LootParams.Builder(level.getLevel())
                .withParameter(WotrLootContextParams.RIFT_TIER, config.tier())
                .withParameter(WotrLootContextParams.RIFT_PARAMETERS,
                        config.getCustomData(WotrRiftConfigDataTypes.INITIAL_RIFT_PARAMETERS))
                .create(LootContextParamSets.EMPTY);
        LootContext context = new LootContext.Builder(params).create(Optional.empty());
        List<Goal> generatedGoals = goals.stream().flatMap(goal -> goal.generateGoal(context).stream()).toList();
        if (generatedGoals.isEmpty()) {
            return new NoOngoingObjective();
        }
        return new GoalBasedOngoingObjective(generatedGoals);
    }
}

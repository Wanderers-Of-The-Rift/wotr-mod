package com.wanderersoftherift.wotr.core.goal.provider;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.block.blockentity.anomaly.AnomalyTask;
import com.wanderersoftherift.wotr.core.goal.Goal;
import com.wanderersoftherift.wotr.core.goal.GoalProvider;
import com.wanderersoftherift.wotr.core.goal.type.CloseAnomalyGoal;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.util.FastWeightedList;
import net.minecraft.core.Holder;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

/**
 * Provider for close anomaly goals
 *
 * @param anomalyType A weighted list of possible types. If empty no type is required.
 * @param count       A provider for the number of anomalies that must be closed
 */
public record CloseAnomalyGoalProvider(FastWeightedList<Holder<AnomalyTask.AnomalyTaskType<?>>> anomalyType,
        NumberProvider count) implements GoalProvider {

    public static final MapCodec<CloseAnomalyGoalProvider> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    FastWeightedList.codecWithSingleAlternative(WotrRegistries.ANOMALY_TASK_TYPE.holderByNameCodec())
                            .optionalFieldOf("anomaly_type", FastWeightedList.of())
                            .forGetter(CloseAnomalyGoalProvider::anomalyType),
                    NumberProviders.CODEC.fieldOf("count").forGetter(CloseAnomalyGoalProvider::count)
            ).apply(instance, CloseAnomalyGoalProvider::new));

    @Override
    public MapCodec<? extends GoalProvider> getCodec() {
        return CODEC;
    }

    @Override
    public @NotNull List<Goal> generateGoal(LootContext context) {
        int target = count.getInt(context);
        if (target <= 0) {
            return List.of();
        }
        if (anomalyType.isEmpty()) {
            return List.of(new CloseAnomalyGoal(target, Optional.empty()));
        }
        return List.of(
                new CloseAnomalyGoal(target, Optional.of(anomalyType.random(context.getRandom())))
        );
    }
}

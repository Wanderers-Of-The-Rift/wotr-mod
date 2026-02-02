package com.wanderersoftherift.wotr.core.goal.provider;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.core.goal.Goal;
import com.wanderersoftherift.wotr.core.goal.GoalProvider;
import com.wanderersoftherift.wotr.core.goal.type.ActivateObjectiveGoal;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Provider for activate objective goals
 *
 * @param count A provider for the number of objective blocks that must be activated
 */
public record ActivateObjectiveGoalProvider(NumberProvider count) implements GoalProvider {

    public static final MapCodec<ActivateObjectiveGoalProvider> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    NumberProviders.CODEC.fieldOf("count").forGetter(ActivateObjectiveGoalProvider::count)
            ).apply(instance, ActivateObjectiveGoalProvider::new));

    @Override
    public MapCodec<? extends GoalProvider> getCodec() {
        return CODEC;
    }

    @Override
    public @NotNull List<Goal> generateGoal(LootContext context) {
        int target = count.getInt(context);
        if (target > 0) {
            return List.of(new ActivateObjectiveGoal(target));
        } else {
            return List.of();
        }
    }
}

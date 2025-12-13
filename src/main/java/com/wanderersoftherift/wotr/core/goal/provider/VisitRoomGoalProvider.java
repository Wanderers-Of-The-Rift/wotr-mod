package com.wanderersoftherift.wotr.core.goal.provider;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.core.goal.Goal;
import com.wanderersoftherift.wotr.core.goal.GoalProvider;
import com.wanderersoftherift.wotr.core.goal.type.VisitRoomGoal;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Provider for entering rift rooms
 *
 * @param count A provider for the number of rift rooms that must be entered
 */
public record VisitRoomGoalProvider(NumberProvider count) implements GoalProvider {

    public static final MapCodec<VisitRoomGoalProvider> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    NumberProviders.CODEC.fieldOf("count").forGetter(VisitRoomGoalProvider::count)
            ).apply(instance, VisitRoomGoalProvider::new));

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
        return List.of(new VisitRoomGoal(count.getInt(context)));
    }
}

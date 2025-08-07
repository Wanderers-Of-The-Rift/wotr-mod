package com.wanderersoftherift.wotr.core.quest;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import net.minecraft.world.level.storage.loot.LootParams;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Function;

/**
 * Interface for classes that provide goals. These goals may be randomly generated, or otherwise differ depending on the
 * provided context.
 */
public interface GoalProvider {
    Codec<GoalProvider> DIRECT_CODEC = WotrRegistries.GOAL_PROVIDER_TYPES.byNameCodec()
            .dispatch(GoalProvider::getCodec, Function.identity());

    /**
     * @return The codec used to serialize this goal
     */
    MapCodec<? extends GoalProvider> getCodec();

    /**
     * @param params Parameters that may affect generation
     * @return A list of the generated goals.
     */
    @NotNull List<Goal> generateGoal(LootParams params);

}

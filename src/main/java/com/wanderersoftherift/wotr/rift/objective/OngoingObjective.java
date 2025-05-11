package com.wanderersoftherift.wotr.rift.objective;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

import java.util.function.Function;

import static com.wanderersoftherift.wotr.init.ModOngoingObjectiveTypes.ONGOING_OBJECTIVE_TYPE_REGISTRY;

/**
 * OngoingObjective is the base type for tracking ongoing progress and other details for an active objective.
 */
public interface OngoingObjective {
    Codec<OngoingObjective> DIRECT_CODEC = ONGOING_OBJECTIVE_TYPE_REGISTRY.byNameCodec()
            .dispatch(OngoingObjective::getCodec, Function.identity());

    /**
     * @return The codec for this OngoingObjective
     */
    MapCodec<? extends OngoingObjective> getCodec();

    /**
     * Event handler for a living entity death
     * 
     * @param event       The event
     * @param serverLevel The level
     * @param data        The rift objective data
     * @return Whether the objective's data has changed (and should be dirty/replicated)
     */
    default boolean onLivingDeath(LivingDeathEvent event, ServerLevel serverLevel, LevelRiftObjectiveData data) {
        return false;
    }

    /**
     * @return Whether the objective is complete
     */
    boolean isComplete();

    /**
     * @return The message to display when starting the objective
     */
    Component getObjectiveStartMessage();
}

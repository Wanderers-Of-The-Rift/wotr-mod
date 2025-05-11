package com.wanderersoftherift.wotr.rift.objective;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.init.ModObjectiveTypes;
import com.wanderersoftherift.wotr.init.RegistryEvents;
import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.world.level.LevelAccessor;

import java.util.function.Function;

/**
 * Base type for all Objective types.
 */
public interface ObjectiveType {
    Codec<ObjectiveType> DIRECT_CODEC = ModObjectiveTypes.OBJECTIVE_TYPE_REGISTRY.byNameCodec()
            .dispatch(ObjectiveType::getCodec, Function.identity());
    Codec<Holder<ObjectiveType>> CODEC = RegistryFixedCodec.create(RegistryEvents.OBJECTIVE_REGISTRY);

    /**
     * @return A codec for the objective type implementation
     */
    MapCodec<? extends ObjectiveType> getCodec();

    /**
     * Generates an objective instance from this definition. This allows for randomised elements
     * 
     * @param level The level to generate the objective detail for
     * @return An ongoing objective instance from this objective definition
     */
    OngoingObjective generate(LevelAccessor level);
}

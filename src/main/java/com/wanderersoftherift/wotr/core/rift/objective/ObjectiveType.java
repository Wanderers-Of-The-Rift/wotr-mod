package com.wanderersoftherift.wotr.core.rift.objective;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.core.rift.RiftConfig;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.serialization.LaxRegistryCodec;
import com.wanderersoftherift.wotr.world.level.levelgen.jigsaw.JigsawListProcessor;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.ServerLevelAccessor;

import java.util.List;
import java.util.function.Function;

/**
 * Base type for all Objective types.
 */
public interface ObjectiveType {
    Codec<ObjectiveType> DIRECT_CODEC = WotrRegistries.OBJECTIVE_TYPES.byNameCodec()
            .dispatch(ObjectiveType::getCodec, Function.identity());
    Codec<Holder<ObjectiveType>> CODEC = LaxRegistryCodec.create(WotrRegistries.Keys.OBJECTIVES);
    StreamCodec<RegistryFriendlyByteBuf, Holder<ObjectiveType>> STREAM_CODEC = ByteBufCodecs
            .holderRegistry(WotrRegistries.Keys.OBJECTIVES);

    /**
     * @return A codec for the objective type implementation
     */
    MapCodec<? extends ObjectiveType> getCodec();

    /**
     * Generates an objective instance from this definition. This allows for randomised elements
     *
     * @param level  The level to generate the objective detail for
     * @param config
     * @return An ongoing objective instance from this objective definition
     */
    OngoingObjective generateObjective(ServerLevelAccessor level, RiftConfig config);

    /**
     * @return A list of all jigsaw processors that should be applied to the rift when this objective is applied
     */
    List<JigsawListProcessor> processors();
}

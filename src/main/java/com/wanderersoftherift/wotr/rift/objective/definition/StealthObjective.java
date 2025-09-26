package com.wanderersoftherift.wotr.rift.objective.definition;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.core.rift.RiftConfig;
import com.wanderersoftherift.wotr.core.rift.parameter.definitions.RiftParameter;
import com.wanderersoftherift.wotr.init.worldgen.WotrRiftConfigDataTypes;
import com.wanderersoftherift.wotr.rift.objective.ObjectiveType;
import com.wanderersoftherift.wotr.rift.objective.OngoingObjective;
import com.wanderersoftherift.wotr.rift.objective.ongoing.StealthOngoingObjective;
import net.minecraft.core.Holder;
import net.minecraft.world.level.ServerLevelAccessor;

/**
 * An objective to be stealthy
 */
public record StealthObjective(Holder<RiftParameter> stealthTicks) implements ObjectiveType {
    public static final MapCodec<StealthObjective> CODEC = RecordCodecBuilder.mapCodec(inst -> inst
            .group(RiftParameter.HOLDER_CODEC.fieldOf("stealth_ticks").forGetter(StealthObjective::stealthTicks))
            .apply(inst, StealthObjective::new));

    @Override
    public MapCodec<? extends ObjectiveType> getCodec() {
        return CODEC;
    }

    @Override
    public OngoingObjective generate(ServerLevelAccessor level, RiftConfig config) {
        var parameters = config.getCustomData(WotrRiftConfigDataTypes.INITIAL_RIFT_PARAMETERS);
        var key = stealthTicks.getKey().location();
        var param = parameters.getParameter(key);
        if (param == null) {
            return new StealthOngoingObjective(key, 0);
        }
        return new StealthOngoingObjective(key, (int) param.get());
    }
}

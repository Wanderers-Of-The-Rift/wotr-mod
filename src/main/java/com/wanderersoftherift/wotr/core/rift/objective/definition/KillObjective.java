package com.wanderersoftherift.wotr.core.rift.objective.definition;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.core.rift.RiftConfig;
import com.wanderersoftherift.wotr.core.rift.objective.ObjectiveType;
import com.wanderersoftherift.wotr.core.rift.objective.OngoingObjective;
import com.wanderersoftherift.wotr.core.rift.objective.ongoing.KillOngoingObjective;
import com.wanderersoftherift.wotr.core.rift.parameter.definitions.RiftParameter;
import com.wanderersoftherift.wotr.init.worldgen.WotrRiftConfigDataTypes;
import net.minecraft.core.Holder;
import net.minecraft.world.level.ServerLevelAccessor;

/**
 * A simple objective to defeat X mobs (and escape)
 */
public record KillObjective(Holder<RiftParameter> quantity) implements ObjectiveType {
    public static final MapCodec<KillObjective> CODEC = RecordCodecBuilder.mapCodec(
            inst -> inst.group(RiftParameter.HOLDER_CODEC.fieldOf("quantity").forGetter(KillObjective::quantity))
                    .apply(inst, KillObjective::new));

    @Override
    public MapCodec<? extends ObjectiveType> getCodec() {
        return CODEC;
    }

    @Override
    public OngoingObjective generate(ServerLevelAccessor level, RiftConfig config) {
        var parameters = config.getCustomData(WotrRiftConfigDataTypes.INITIAL_RIFT_PARAMETERS);
        var key = quantity.getKey();
        var param = parameters.getParameter(key);
        if (param == null) {
            return new KillOngoingObjective(key, 0);
        }
        return new KillOngoingObjective(key, (int) param.get());
    }
}

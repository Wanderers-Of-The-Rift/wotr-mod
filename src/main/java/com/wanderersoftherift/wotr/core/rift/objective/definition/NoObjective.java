package com.wanderersoftherift.wotr.core.rift.objective.definition;

import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.core.rift.RiftConfig;
import com.wanderersoftherift.wotr.core.rift.objective.ObjectiveType;
import com.wanderersoftherift.wotr.core.rift.objective.OngoingObjective;
import com.wanderersoftherift.wotr.core.rift.objective.ongoing.NoOngoingObjective;
import net.minecraft.world.level.ServerLevelAccessor;

public record NoObjective() implements ObjectiveType {
    public static final NoObjective INSTANCE = new NoObjective();
    public static final MapCodec<NoObjective> CODEC = MapCodec.unit(INSTANCE);

    @Override
    public MapCodec<? extends ObjectiveType> getCodec() {
        return CODEC;
    }

    @Override
    public OngoingObjective generate(ServerLevelAccessor level, RiftConfig config) {
        return NoOngoingObjective.INSTANCE;
    }
}

package com.wanderersoftherift.wotr.rift.objective.definition;

import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.rift.objective.ObjectiveType;
import com.wanderersoftherift.wotr.rift.objective.OngoingObjective;
import com.wanderersoftherift.wotr.rift.objective.ongoing.NoOngoingObjective;
import net.minecraft.world.level.LevelAccessor;

public record NoObjective() implements ObjectiveType {
    public static final NoObjective INSTANCE = new NoObjective();
    public static final MapCodec<NoObjective> CODEC = MapCodec.unit(INSTANCE);

    @Override
    public MapCodec<? extends ObjectiveType> getCodec() {
        return CODEC;
    }

    @Override
    public OngoingObjective generate(LevelAccessor level) {
        return NoOngoingObjective.INSTANCE;
    }
}

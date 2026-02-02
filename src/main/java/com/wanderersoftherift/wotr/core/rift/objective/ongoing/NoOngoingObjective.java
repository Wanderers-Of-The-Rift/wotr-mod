package com.wanderersoftherift.wotr.core.rift.objective.ongoing;

import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.core.rift.RiftData;
import com.wanderersoftherift.wotr.core.rift.objective.OngoingObjective;
import com.wanderersoftherift.wotr.core.rift.parameter.RiftParameterData;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;

public record NoOngoingObjective() implements OngoingObjective {
    public static final NoOngoingObjective INSTANCE = new NoOngoingObjective();
    public static final MapCodec<NoOngoingObjective> CODEC = MapCodec.unit(INSTANCE);

    @Override
    public MapCodec<? extends OngoingObjective> getCodec() {
        return CODEC;
    }

    @Override
    public boolean isComplete() {
        return true;
    }

    @Override
    public Component getObjectiveStartMessage() {
        return Component.empty();
    }

    @Override
    public void registerUpdaters(RiftParameterData data, RiftData riftData, ServerLevel serverLevel) {
    }
}

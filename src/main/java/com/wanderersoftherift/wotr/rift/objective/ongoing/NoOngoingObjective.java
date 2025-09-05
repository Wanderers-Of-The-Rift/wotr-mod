package com.wanderersoftherift.wotr.rift.objective.ongoing;

import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.rift.objective.OngoingObjective;
import net.minecraft.network.chat.Component;

public record NoOngoingObjective() implements OngoingObjective {
    public static final NoOngoingObjective INSTANCE = new NoOngoingObjective();
    public static final MapCodec<NoOngoingObjective> CODEC = MapCodec.unit(INSTANCE);

    @Override
    public MapCodec<? extends OngoingObjective> getCodec() {
        return CODEC;
    }

    @Override
    public boolean isComplete() {
        return false;
    }

    @Override
    public Component getObjectiveStartMessage() {
        return null;
    }
}

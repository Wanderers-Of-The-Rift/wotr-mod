package com.wanderersoftherift.wotr.block.blockentity.anomaly;

import com.mojang.serialization.Codec;

public record SliceAnomalyTaskState(float hitpoints) {
    public static final Codec<SliceAnomalyTaskState> CODEC = Codec.FLOAT
            .xmap(SliceAnomalyTaskState::new, SliceAnomalyTaskState::hitpoints)
            .fieldOf("hitpoints")
            .codec();
}

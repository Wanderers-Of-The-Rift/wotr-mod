package com.wanderersoftherift.wotr.block.blockentity.anomaly;

import com.mojang.serialization.Codec;

public record NeedleTaskState(int remainingStitches) {
    public static final Codec<NeedleTaskState> CODEC = Codec.INT
            .xmap(NeedleTaskState::new, NeedleTaskState::remainingStitches)
            .fieldOf("stitches")
            .codec();
}

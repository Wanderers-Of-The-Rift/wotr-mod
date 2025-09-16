package com.wanderersoftherift.wotr.block.blockentity.anomaly;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;

public record BattleMobAttachment(BlockPos blockPos) {
    public static final Codec<BattleMobAttachment> CODEC = BlockPos.CODEC.xmap(BattleMobAttachment::new,
            BattleMobAttachment::blockPos);
}

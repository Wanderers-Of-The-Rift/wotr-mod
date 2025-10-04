package com.wanderersoftherift.wotr.block.blockentity.anomaly;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;

public record DeathNotifierAttachment(BlockPos blockPos) {
    public static final Codec<DeathNotifierAttachment> CODEC = BlockPos.CODEC.xmap(DeathNotifierAttachment::new,
            DeathNotifierAttachment::blockPos);
}

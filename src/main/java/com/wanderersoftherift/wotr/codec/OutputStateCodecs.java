package com.wanderersoftherift.wotr.codec;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Function;

public class OutputStateCodecs {
    public static final Codec<BlockState> OUTPUT_STATE_CODEC = Codec.either(
            BuiltInRegistries.BLOCK.byNameCodec(),
            BlockState.CODEC
    ).xmap(either -> either.map(Block::defaultBlockState, Function.identity()), entry -> entry == entry.getBlock().defaultBlockState() ? Either.left(entry.getBlock()) : Either.right(entry));

}

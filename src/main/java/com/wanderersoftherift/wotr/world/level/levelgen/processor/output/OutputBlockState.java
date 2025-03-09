package com.wanderersoftherift.wotr.world.level.levelgen.processor.output;

import com.wanderersoftherift.wotr.init.ModOutputBlockStateTypes;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Function;

public abstract class OutputBlockState {
    public static final Codec<OutputBlockState> DIRECT_CODEC = Codec.either(
            BuiltInRegistries.BLOCK.byNameCodec(),
            ModOutputBlockStateTypes.OUTPUT_BLOCKSTATE_TYPE_REGISTRY.byNameCodec().dispatch(OutputBlockState::getCodec, Function.identity())
    ).xmap(either -> either.map(DefaultOutputBlockState::new, Function.identity()), entry -> entry instanceof DefaultOutputBlockState defaultState ? Either.left(defaultState.getBlock()) : Either.right(entry));

    public abstract MapCodec<? extends OutputBlockState> getCodec();

    public abstract BlockState convertBlockState();
}

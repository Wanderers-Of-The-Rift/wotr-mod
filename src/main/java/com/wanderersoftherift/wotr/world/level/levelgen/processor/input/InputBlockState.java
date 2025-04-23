package com.wanderersoftherift.wotr.world.level.levelgen.processor.input;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.init.ModInputBlockStateTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Function;

public abstract class InputBlockState {
    public static final Codec<InputBlockState> DIRECT_CODEC = Codec
            .<Block, InputBlockState>either(BuiltInRegistries.BLOCK.byNameCodec(),
                    ModInputBlockStateTypes.INPUT_BLOCKSTATE_TYPE_REGISTRY.byNameCodec()
                            .dispatch(InputBlockState::getCodec, Function.identity()))
            .xmap(either -> either.map(DefaultInputBlockState::new, Function.identity()),
                    entry -> entry instanceof DefaultInputBlockState defaultState
                            ? Either.<Block, InputBlockState>left(defaultState.getBlock())
                            : Either.<Block, InputBlockState>right(entry));

    public abstract MapCodec<? extends InputBlockState> getCodec();

    public abstract boolean matchesBlockstate(BlockState blockState);
}

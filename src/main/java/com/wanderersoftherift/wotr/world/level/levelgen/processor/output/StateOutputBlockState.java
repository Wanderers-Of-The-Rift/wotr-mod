package com.wanderersoftherift.wotr.world.level.levelgen.processor.output;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockState;

public class StateOutputBlockState extends OutputBlockState {

    public static final MapCodec<StateOutputBlockState> CODEC = MapCodec.assumeMapUnsafe(BlockState.CODEC)
            .xmap(StateOutputBlockState::new, StateOutputBlockState::getState);

    public BlockState state;

    public StateOutputBlockState(BlockState state) {
        this.state = state;
    }

    public BlockState getState() {
        return state;
    }

    @Override
    public MapCodec<? extends OutputBlockState> getCodec() {
        return CODEC;
    }

    @Override
    public BlockState convertBlockState() {
        return state;
    }
}

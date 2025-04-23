package com.wanderersoftherift.wotr.world.level.levelgen.processor.input;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockState;

public class StateInputBlockState extends InputBlockState {

    public static final MapCodec<StateInputBlockState> CODEC = MapCodec.assumeMapUnsafe(BlockState.CODEC)
            .xmap(StateInputBlockState::new, StateInputBlockState::getState);

    private final BlockState state;

    public StateInputBlockState(BlockState state) {
        this.state = state;
    }

    public BlockState getState() {
        return state;
    }

    @Override
    public MapCodec<? extends InputBlockState> getCodec() {
        return CODEC;
    }

    @Override
    public boolean matchesBlockstate(BlockState blockState) {
        return blockState.equals(state);
    }
}

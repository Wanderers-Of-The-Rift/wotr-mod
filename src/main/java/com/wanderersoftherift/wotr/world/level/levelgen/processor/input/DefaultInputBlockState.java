package com.wanderersoftherift.wotr.world.level.levelgen.processor.input;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class DefaultInputBlockState extends InputBlockState {

    public static final MapCodec<DefaultInputBlockState> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
            .group(BuiltInRegistries.BLOCK.byNameCodec().fieldOf("block").forGetter(DefaultInputBlockState::getBlock)
            ).apply(instance, DefaultInputBlockState::new));

    public Block block;

    public DefaultInputBlockState(Block block) {
        this.block = block;
    }

    public Block getBlock() {
        return block;
    }

    @Override
    public MapCodec<? extends InputBlockState> getCodec() {
        return CODEC;
    }

    @Override
    public boolean matchesBlockstate(BlockState blockState) {
        return blockState.is(block);
    }

}

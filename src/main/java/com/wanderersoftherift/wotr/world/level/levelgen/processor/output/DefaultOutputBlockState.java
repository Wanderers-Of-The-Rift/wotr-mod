package com.wanderersoftherift.wotr.world.level.levelgen.processor.output;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class DefaultOutputBlockState extends OutputBlockState {

    public static final MapCodec<DefaultOutputBlockState> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    BuiltInRegistries.BLOCK.byNameCodec().fieldOf("block").forGetter(DefaultOutputBlockState::getBlock)
            ).apply(instance, DefaultOutputBlockState::new)
    );

    public Block block;

    public DefaultOutputBlockState(Block block) {
        this.block = block;
    }

    public Block getBlock() {
        return block;
    }

    @Override
    public MapCodec<? extends OutputBlockState> getCodec() {
        return CODEC;
    }

    @Override
    public BlockState convertBlockState() {
        return block.defaultBlockState();
    }
}

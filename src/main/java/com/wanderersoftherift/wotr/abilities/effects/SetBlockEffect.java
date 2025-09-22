package com.wanderersoftherift.wotr.abilities.effects;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.AbilityContext;
import com.wanderersoftherift.wotr.abilities.targeting.TargetInfo;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Effect that sets (replaces) a block
 * 
 * @param block
 */
public record SetBlockEffect(BlockState block) implements AbilityEffect {

    public static final MapCodec<SetBlockEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BlockState.CODEC.fieldOf("block").forGetter(SetBlockEffect::block)
    ).apply(instance, SetBlockEffect::new));

    @Override
    public MapCodec<? extends AbilityEffect> getCodec() {
        return CODEC;
    }

    @Override
    public void apply(AbilityContext context, TargetInfo targetInfo) {
        targetInfo.targetBlockHitResults().forEach(blockHit -> {
            context.level().setBlock(blockHit.getBlockPos(), block, Block.UPDATE_ALL);
        });
    }
}

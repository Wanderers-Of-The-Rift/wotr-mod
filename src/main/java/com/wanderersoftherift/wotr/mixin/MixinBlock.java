package com.wanderersoftherift.wotr.mixin;

import com.wanderersoftherift.wotr.block.AttackableBlock;
import com.wanderersoftherift.wotr.init.WotrTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Block.class)
public class MixinBlock implements AttackableBlock {
    @Override
    public boolean isAttackable(BlockState state, BlockGetter level, BlockPos position) {
        return state.is(WotrTags.Blocks.ATTACKABLE);
    }

    @Override
    public void attack(BlockState state, LevelAccessor level, BlockPos position, float damage, DamageSource source) {
        level.destroyBlock(position, true, source.getEntity());
    }
}

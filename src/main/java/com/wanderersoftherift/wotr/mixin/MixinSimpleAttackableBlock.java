package com.wanderersoftherift.wotr.mixin;

import com.wanderersoftherift.wotr.block.AttackableBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BambooStalkBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.DecoratedPotBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.WebBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ BushBlock.class, WebBlock.class, LeavesBlock.class, BambooStalkBlock.class, DecoratedPotBlock.class })
public class MixinSimpleAttackableBlock implements AttackableBlock {
    @Override
    public boolean isAttackable(BlockState state, BlockGetter level, BlockPos position) {
        return true;
    }

    @Override
    public void attack(BlockState state, LevelAccessor level, BlockPos position, float damage, DamageSource source) {
        level.destroyBlock(position, true, source.getEntity());
    }
}

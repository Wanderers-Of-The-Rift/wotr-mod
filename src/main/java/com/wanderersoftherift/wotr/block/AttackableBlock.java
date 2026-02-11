package com.wanderersoftherift.wotr.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public interface AttackableBlock {

    static ClipContext createClipContext(Vec3 start, Vec3 end) {
        return new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE,
                CollisionContext.empty()) {
            @Override
            public VoxelShape getBlockShape(BlockState blockState, BlockGetter level, BlockPos pos) {
                if (isAttackableStatic(blockState, level, pos)) {
                    return Shapes.empty();
                }
                return super.getBlockShape(blockState, level, pos);
            }
        };
    }

    static boolean isAttackableStatic(BlockState state, BlockGetter level, BlockPos position) {
        if (state.getBlock() instanceof AttackableBlock ab && ab.isAttackable(state, level, position)) {
            return true;
        }
        return false;
    }

    boolean isAttackable(BlockState state, BlockGetter level, BlockPos position);

    void attack(BlockState state, LevelAccessor level, BlockPos position, float damage, DamageSource source);
}

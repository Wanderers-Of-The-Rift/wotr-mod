package com.wanderersoftherift.wotr.abilities.effects;

import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.abilities.AbilityContext;
import com.wanderersoftherift.wotr.abilities.targeting.TargetInfo;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public final class BreakBlockEffect implements AbilityEffect {

    public static final BreakBlockEffect INSTANCE = new BreakBlockEffect();
    public static final MapCodec<BreakBlockEffect> CODEC = MapCodec.unit(INSTANCE);

    private BreakBlockEffect() {
    }

    @Override
    public MapCodec<? extends AbilityEffect> getCodec() {
        return CODEC;
    }

    @Override
    public void apply(AbilityContext context, TargetInfo targetInfo) {
        targetInfo.targetBlocks().map(BlockHitResult::getBlockPos).forEach(pos -> {
            BlockState blockState = context.level().getBlockState(pos);
            if (blockState.canEntityDestroy(context.level(), pos, context.caster())
                    && blockState.getBlock().defaultDestroyTime() > -1) {
                context.level().destroyBlock(pos, true, context.caster());
            }
        });
    }
}

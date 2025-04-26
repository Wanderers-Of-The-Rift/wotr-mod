package com.wanderersoftherift.wotr.block;

import com.wanderersoftherift.wotr.init.ModBlocks;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.DeferredBlock;
import org.jetbrains.annotations.NotNull;

public class MobTrapBlock extends TrapBlock {
    public MobTrapBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean legalEntity(@NotNull Entity entity) {
        return entity.getType() != EntityType.PLAYER;
    }

    @Override
    public BlockState getTweak() {
        return ModBlocks.TRAP_BLOCK.get().defaultBlockState();
    }

    @Override
    public DeferredBlock getBlock() {
        return ModBlocks.MOB_TRAP_BLOCK;
    }
}

package com.wanderersoftherift.wotr.block;

import com.wanderersoftherift.wotr.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.neoforged.neoforge.registries.DeferredBlock;
import org.jetbrains.annotations.NotNull;

public class SpringBlock extends DittoBlock {
    public static final int MAX_STRENGTH = 9;
    public static final int MIN_STRENGTH = 0;
    public static final IntegerProperty STRENGTH = IntegerProperty.create("strength", MIN_STRENGTH, MAX_STRENGTH);

    public SpringBlock(Properties properties) {
        super(properties);

        this.registerDefaultState(this.defaultBlockState().setValue(STRENGTH, 5));
    }

    @Override
    public void stepOn(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull Entity entity) {
        launch(entity, state);
    }

    public void launch(@NotNull Entity entity, @NotNull BlockState state) {
        entity.resetFallDistance();
        if (entity.isShiftKeyDown()) {
            return;
        }
        double delta = state.getValue(STRENGTH) * 0.1 + 0.62;

        entity.setDeltaMovement(entity.getDeltaMovement().add(0, delta, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(STRENGTH);
    }

    @Override
    public DeferredBlock getBlock() {
        return ModBlocks.SPRING_BLOCK;
    }
}

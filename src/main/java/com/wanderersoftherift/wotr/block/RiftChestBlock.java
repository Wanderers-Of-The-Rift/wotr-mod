package com.wanderersoftherift.wotr.block;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.block.blockentity.RiftChestBlockEntity;
import com.wanderersoftherift.wotr.init.WotrBlockEntities;
import com.wanderersoftherift.wotr.util.EnumEntries;
import com.wanderersoftherift.wotr.util.VoxelShapeUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
public class RiftChestBlock extends ChestBlock {
    public static final MapCodec<RiftChestBlock> CODEC = simpleCodec(
            (properties) -> new RiftChestBlock(WotrBlockEntities.RIFT_CHEST::get, properties));

    protected static final VoxelShape DEFAULT_SHAPE = Block.box(1.0, 0.0, 1.0, 15.0, 15.0, 15.0);
    protected static final Table<ChestType, Direction, VoxelShape> SHAPES;

    static {
        VoxelShape left = Block.box(1.0, 0.0, 1.0, 16.0, 15.0, 15.0);
        VoxelShape right = Block.box(0.0, 0.0, 1.0, 15.0, 15.0, 15.0);
        SHAPES = HashBasedTable.create();
        for (Direction dir : EnumEntries.DIRECTIONS) {
            SHAPES.put(ChestType.LEFT, dir, VoxelShapeUtils.rotateHorizontal(left, dir));
            SHAPES.put(ChestType.RIGHT, dir, VoxelShapeUtils.rotateHorizontal(right, dir));
        }
    }

    public RiftChestBlock(Supplier<BlockEntityType<? extends ChestBlockEntity>> riftChest, Properties properties) {
        super(riftChest, properties);
    }

    @Override
    public @NotNull MapCodec<RiftChestBlock> codec() {
        return CODEC;
    }

    @Override
    protected @NotNull VoxelShape getShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context) {

        VoxelShape result = SHAPES.get(state.getValue(ChestBlock.TYPE), state.getValue(FACING));
        if (result == null) {
            return DEFAULT_SHAPE;
        }
        return result;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RiftChestBlockEntity(pos, state);
    }
}

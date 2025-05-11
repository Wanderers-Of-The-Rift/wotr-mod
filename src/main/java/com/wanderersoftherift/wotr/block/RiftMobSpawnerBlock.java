package com.wanderersoftherift.wotr.block;

import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.block.blockentity.RiftMobSpawnerBlockEntity;
import com.wanderersoftherift.wotr.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Spawner;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawnerState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;

import javax.annotation.Nullable;
import java.util.List;

public class RiftMobSpawnerBlock extends BaseEntityBlock {
    public static final MapCodec<RiftMobSpawnerBlock> CODEC = simpleCodec(RiftMobSpawnerBlock::new);
    public static final EnumProperty<TrialSpawnerState> STATE = BlockStateProperties.TRIAL_SPAWNER_STATE;
    public static final BooleanProperty OMINOUS = BlockStateProperties.OMINOUS;

    public RiftMobSpawnerBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(STATE, TrialSpawnerState.INACTIVE)
                .setValue(OMINOUS, Boolean.valueOf(false)));
    }

    @Override
    public MapCodec<RiftMobSpawnerBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(STATE, OMINOUS);
    }

    @Nullable @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new RiftMobSpawnerBlockEntity(blockPos, blockState);
    }

    @Nullable @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Level level,
            BlockState blockState,
            BlockEntityType<T> blockEntityType) {
        if (level instanceof ServerLevel serverlevel) {
            return createTickerHelper(
                    blockEntityType, ModBlockEntities.RIFT_MOB_SPAWNER.get(),
                    (level1, blockPos, blockState1, spawnerBlockEntity) -> spawnerBlockEntity.getTrialSpawner()
                            .tickServer(serverlevel, blockPos,
                                    blockState1.getOptionalValue(BlockStateProperties.OMINOUS).orElse(false))
            );
        } else {
            return createTickerHelper(
                    blockEntityType, ModBlockEntities.RIFT_MOB_SPAWNER.get(),
                    (level1, blockPos, blockState1, spawnerBlockEntity) -> spawnerBlockEntity.getTrialSpawner()
                            .tickClient(level1, blockPos,
                                    blockState1.getOptionalValue(BlockStateProperties.OMINOUS).orElse(false))
            );
        }
    }

    @Override
    public void appendHoverText(
            ItemStack itemStack,
            Item.TooltipContext tooltipContext,
            List<Component> tooltipComponents,
            TooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, tooltipContext, tooltipComponents, tooltipFlag);
        Spawner.appendHoverText(itemStack, tooltipComponents, "spawn_data");
    }
}

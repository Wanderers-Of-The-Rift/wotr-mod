package com.wanderersoftherift.wotr.client.render.blockentity;

import com.wanderersoftherift.wotr.block.RiftSpawnerBlock;
import com.wanderersoftherift.wotr.block.blockentity.RiftSpawnerBlockEntity;
import com.wanderersoftherift.wotr.client.model.geo.block.RiftSpawnerBlockModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class RiftSpawnerBlockEntityRenderer extends GeoBlockRenderer<RiftSpawnerBlockEntity> {

    public RiftSpawnerBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(new RiftSpawnerBlockModel());
    }

    @Override
    public boolean shouldRender(RiftSpawnerBlockEntity blockEntity, @NotNull Vec3 cameraPos) {
        BlockState state = blockEntity.getLevel().getBlockState(blockEntity.getBlockPos());
        if (state.getBlock() instanceof RiftSpawnerBlock
                && state.getValue(RiftSpawnerBlock.HALF) == DoubleBlockHalf.LOWER) {
            return super.shouldRender(blockEntity, cameraPos);
        }
        return false;
    }
}

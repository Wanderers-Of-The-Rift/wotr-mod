package com.wanderersoftherift.wotr.client.render.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.wanderersoftherift.wotr.block.blockentity.RiftMobSpawnerBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.SpawnerRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawner;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawnerData;

public class RiftMobSpawnerBlockEntityRenderer implements BlockEntityRenderer<RiftMobSpawnerBlockEntity> {
    private final EntityRenderDispatcher entityRenderer;

    public RiftMobSpawnerBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.entityRenderer = context.getEntityRenderer();
    }

    public void render(
            RiftMobSpawnerBlockEntity blockEntity,
            float partialTick,
            PoseStack stack,
            MultiBufferSource bufferSource,
            int packedLight,
            int packedOverlay) {
        Level level = blockEntity.getLevel();
        if (level != null) {
            TrialSpawner trialspawner = blockEntity.getTrialSpawner();
            TrialSpawnerData trialspawnerdata = trialspawner.getData();
            Entity entity = trialspawnerdata.getOrCreateDisplayEntity(trialspawner, level, trialspawner.getState());
            if (entity != null) {
                SpawnerRenderer.renderEntityInSpawner(
                        partialTick, stack, bufferSource, packedLight, entity, this.entityRenderer,
                        trialspawnerdata.getOSpin(), trialspawnerdata.getSpin()
                );
            }
        }
    }

    @Override
    public net.minecraft.world.phys.AABB getRenderBoundingBox(RiftMobSpawnerBlockEntity blockEntity) {
        net.minecraft.core.BlockPos pos = blockEntity.getBlockPos();
        return new net.minecraft.world.phys.AABB(pos.getX() - 1.0, pos.getY() - 1.0, pos.getZ() - 1.0, pos.getX() + 2.0,
                pos.getY() + 2.0, pos.getZ() + 2.0);
    }
}

package com.wanderersoftherift.wotr.client.render.entity.mob;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.entity.mob.RiftSkeleton;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.SkeletonRenderer;
import net.minecraft.client.renderer.entity.state.SkeletonRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Skeleton;

public class RiftSkeletonRenderer extends SkeletonRenderer {
    // Makes render for RiftZombie from variant png

    public RiftSkeletonRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public SkeletonRenderState createRenderState() {
        return new RiftSkeletonRenderState();
    }

    @Override
    public void extractRenderState(Skeleton entity, SkeletonRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        if (entity instanceof RiftSkeleton riftSkeleton && state instanceof RiftSkeletonRenderState riftState) {
            riftState.variant = riftSkeleton.getVariant();
        }
    }

    @Override
    public ResourceLocation getTextureLocation(SkeletonRenderState state) {
        String variant = "default_skeleton";
        if (state instanceof RiftSkeletonRenderState riftState) {
            variant = riftState.variant != null ? riftState.variant : "default_skeleton";
        }
        return WanderersOfTheRift.id("textures/entity/mob/" + variant + ".png");
    }
}
package com.wanderersoftherift.wotr.client.render.entity.mob;

import com.wanderersoftherift.wotr.entity.mob.RiftMobVariantData;
import com.wanderersoftherift.wotr.entity.mob.RiftSkeleton;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.AbstractSkeletonRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;

public class RiftSkeletonRenderer extends AbstractSkeletonRenderer<RiftSkeleton, RiftSkeletonRenderState> {

    public RiftSkeletonRenderer(EntityRendererProvider.Context context) {
        super(context, ModelLayers.SKELETON, ModelLayers.SKELETON_INNER_ARMOR, ModelLayers.SKELETON_OUTER_ARMOR);
    }

    @Override
    public RiftSkeletonRenderState createRenderState() {
        return new RiftSkeletonRenderState();
    }

    @Override
    public void extractRenderState(RiftSkeleton riftSkeleton, RiftSkeletonRenderState riftState, float partialTick) {
        super.extractRenderState(riftSkeleton, riftState, partialTick);
        Holder<RiftMobVariantData> varientHolder = riftSkeleton.getVariant();
        riftState.variant = varientHolder.value().texture();
    }

    @Override
    public ResourceLocation getTextureLocation(RiftSkeletonRenderState riftSkeletonRenderState) {
        return riftSkeletonRenderState.variant;
    }
}
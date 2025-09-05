package com.wanderersoftherift.wotr.client.render.entity.mob;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.entity.mob.MobVariantData;
import com.wanderersoftherift.wotr.entity.mob.RiftSkeleton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.AbstractSkeletonRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

import static com.wanderersoftherift.wotr.init.WotrRegistries.Keys.MOB_VARIANTS;

public class RiftSkeletonRenderer extends AbstractSkeletonRenderer<RiftSkeleton, RiftSkeletonRenderState> {
    // Makes render for RiftZombie from variant png

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
        riftState.variant = riftSkeleton.getVariant();
    }

    @Override
    public ResourceLocation getTextureLocation(RiftSkeletonRenderState riftSkeletonRenderState) {
        ResourceLocation variantId;
        if (riftSkeletonRenderState.variant != null) {
            variantId = riftSkeletonRenderState.variant;
        } else {
            variantId = WanderersOfTheRift.id("default_skeleton");
        }

        Registry<MobVariantData> registry = Minecraft.getInstance().level.registryAccess().lookupOrThrow(MOB_VARIANTS);

        return MobVariantData.getTextureForVariant(variantId, registry, "skeleton");
    }
}
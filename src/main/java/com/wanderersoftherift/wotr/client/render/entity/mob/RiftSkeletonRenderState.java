package com.wanderersoftherift.wotr.client.render.entity.mob;

import net.minecraft.client.renderer.entity.state.SkeletonRenderState;
import net.minecraft.resources.ResourceLocation;

public class RiftSkeletonRenderState extends SkeletonRenderState {
    // required for RiftSkeletonRenderer to extract/keep the variant from the entity
    public ResourceLocation variant;
}

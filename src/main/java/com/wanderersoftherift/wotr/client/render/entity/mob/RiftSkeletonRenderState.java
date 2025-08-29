package com.wanderersoftherift.wotr.client.render.entity.mob;

import net.minecraft.client.renderer.entity.state.SkeletonRenderState;

public class RiftSkeletonRenderState extends SkeletonRenderState {
    // required for RiftSkeletonRenderer to extract/keep the variant from the entity
    public String variant = "default_skeleton";
}

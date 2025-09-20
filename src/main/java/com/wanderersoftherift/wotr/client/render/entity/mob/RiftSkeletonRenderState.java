package com.wanderersoftherift.wotr.client.render.entity.mob;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import net.minecraft.client.renderer.entity.state.SkeletonRenderState;
import net.minecraft.resources.ResourceLocation;

public class RiftSkeletonRenderState extends SkeletonRenderState {
    public ResourceLocation variant = WanderersOfTheRift.id("skeleton/default_skeleton");
}

package com.wanderersoftherift.wotr.client.render.entity.mob;

import net.minecraft.client.renderer.entity.state.ZombieRenderState;

public class RiftZombieRenderState extends ZombieRenderState {
    // required for RiftZombieRenderer to extract/keep the variant from the entity
    public String variant = "default_zombie";
}

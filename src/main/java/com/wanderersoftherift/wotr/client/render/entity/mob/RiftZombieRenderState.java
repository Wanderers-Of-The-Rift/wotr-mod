package com.wanderersoftherift.wotr.client.render.entity.mob;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import net.minecraft.client.renderer.entity.state.ZombieRenderState;
import net.minecraft.resources.ResourceLocation;

public class RiftZombieRenderState extends ZombieRenderState {
    // required for RiftZombieRenderer to extract/keep the variant from the entity
    public ResourceLocation variant = WanderersOfTheRift.id("textures/entity/mob_variant/zombie/default_zombie.png");
}

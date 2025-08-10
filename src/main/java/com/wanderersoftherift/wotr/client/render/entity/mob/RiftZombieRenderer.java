package com.wanderersoftherift.wotr.client.render.entity.mob;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.entity.mob.RiftZombie;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ZombieRenderer;
import net.minecraft.client.renderer.entity.state.ZombieRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Zombie;

public class RiftZombieRenderer extends ZombieRenderer {
    // Makes render for RiftZombie from variant png

    public RiftZombieRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ZombieRenderState createRenderState() {
        return new RiftZombieRenderState();
    }

    @Override
    public void extractRenderState(Zombie entity, ZombieRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        if (entity instanceof RiftZombie riftZombie && state instanceof RiftZombieRenderState riftState) {
            riftState.variant = riftZombie.getVariant();
        }
    }

    @Override
    public ResourceLocation getTextureLocation(ZombieRenderState state) {
        String variant = "default_zombie";
        if (state instanceof RiftZombieRenderState riftState) {
            variant = riftState.variant != null ? riftState.variant : "default_zombie";
        }
        return WanderersOfTheRift.id("textures/entity/mob/" + variant + ".png");
    }
}
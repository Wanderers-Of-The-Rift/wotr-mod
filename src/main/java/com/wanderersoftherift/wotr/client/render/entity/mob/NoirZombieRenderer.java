package com.wanderersoftherift.wotr.client.render.entity.mob;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ZombieRenderer;
import net.minecraft.client.renderer.entity.state.ZombieRenderState;
import net.minecraft.resources.ResourceLocation;

public class NoirZombieRenderer extends ZombieRenderer {
    private static final ResourceLocation TEXTURE = WanderersOfTheRift.id("textures/entity/mob/noir_zombie.png");

    public NoirZombieRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(ZombieRenderState state) {
        return TEXTURE;
    }
}
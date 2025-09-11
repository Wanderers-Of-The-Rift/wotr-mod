package com.wanderersoftherift.wotr.client.render.entity.mob;

import com.wanderersoftherift.wotr.entity.mob.RiftMobVariantData;
import com.wanderersoftherift.wotr.entity.mob.RiftZombie;
import net.minecraft.client.model.ZombieModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.AbstractZombieRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;

public class RiftZombieRenderer
        extends AbstractZombieRenderer<RiftZombie, RiftZombieRenderState, ZombieModel<RiftZombieRenderState>> {
    // Makes render for RiftZombie from variant_zombie.png, if valid variant.json found.

    public RiftZombieRenderer(EntityRendererProvider.Context context) {
        super(context, new ZombieModel<>(context.bakeLayer(ModelLayers.ZOMBIE)),
                new ZombieModel<>(context.bakeLayer(ModelLayers.ZOMBIE_BABY)),
                new ZombieModel<>(context.bakeLayer(ModelLayers.ZOMBIE_INNER_ARMOR)),
                new ZombieModel<>(context.bakeLayer(ModelLayers.ZOMBIE_OUTER_ARMOR)),
                new ZombieModel<>(context.bakeLayer(ModelLayers.ZOMBIE_BABY_INNER_ARMOR)),
                new ZombieModel<>(context.bakeLayer(ModelLayers.ZOMBIE_BABY_OUTER_ARMOR)));
    }

    @Override
    public RiftZombieRenderState createRenderState() {
        return new RiftZombieRenderState();
    }

    @Override
    public void extractRenderState(RiftZombie riftZombie, RiftZombieRenderState riftState, float partialTick) {
        super.extractRenderState(riftZombie, riftState, partialTick);
        Holder<RiftMobVariantData> varientHolder = riftZombie.getVariant();
        riftState.variant = varientHolder.value().texture();
    }

    @Override
    public ResourceLocation getTextureLocation(RiftZombieRenderState riftZombieRenderState) {
        return riftZombieRenderState.variant;
    }
}
package com.wanderersoftherift.wotr.client.render.entity.mob;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.entity.mob.RiftMobVariantData;
import com.wanderersoftherift.wotr.entity.mob.RiftZombie;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ZombieModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.AbstractZombieRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

import static com.wanderersoftherift.wotr.init.WotrRegistries.Keys.MOB_VARIANTS;

public class RiftZombieRenderer
        extends AbstractZombieRenderer<RiftZombie, RiftZombieRenderState, ZombieModel<RiftZombieRenderState>> {
    // Makes render for RiftZombie from variant_zombie.png, otherwise uses default

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
        ResourceLocation variantId;
        if (riftZombieRenderState.variant != null) {
            variantId = riftZombieRenderState.variant;
        } else {
            variantId = WanderersOfTheRift.id("default_zombie");
        }
        Registry<RiftMobVariantData> registry = Minecraft.getInstance().level.registryAccess()
                .lookupOrThrow(MOB_VARIANTS);
        return RiftMobVariantData.getTextureForVariant(variantId, registry);
    }
}
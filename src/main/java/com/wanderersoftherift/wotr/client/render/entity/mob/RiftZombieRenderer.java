package com.wanderersoftherift.wotr.client.render.entity.mob;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.entity.mob.MobVariantData;
import com.wanderersoftherift.wotr.entity.mob.RiftZombie;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ZombieModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.AbstractZombieRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

import static com.wanderersoftherift.wotr.init.WotrRegistries.Keys.MOB_VARIANTS;

public class RiftZombieRenderer
        extends AbstractZombieRenderer<RiftZombie, RiftZombieRenderState, ZombieModel<RiftZombieRenderState>> {
    // Makes render for RiftZombie from variant png

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
        riftState.variant = riftZombie.getVariant();
    }

    @Override
    public ResourceLocation getTextureLocation(RiftZombieRenderState riftZombieRenderState) {
        ResourceLocation variantId;
        if (riftZombieRenderState.variant != null) {
            variantId = riftZombieRenderState.variant;
        } else {
            variantId = WanderersOfTheRift.id("default_zombie");
        }
        Registry<MobVariantData> registry = Minecraft.getInstance().level.registryAccess().lookupOrThrow(MOB_VARIANTS);
        return MobVariantData.getTextureForVariant(variantId, registry, "zombie");
    }
}
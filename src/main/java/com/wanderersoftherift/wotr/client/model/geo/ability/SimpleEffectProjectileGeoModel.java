package com.wanderersoftherift.wotr.client.model.geo.ability;

import com.wanderersoftherift.wotr.entity.projectile.SimpleEffectProjectile;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;

public class SimpleEffectProjectileGeoModel extends GeoModel<SimpleEffectProjectile> {

    @Override
    public ResourceLocation getModelResource(SimpleEffectProjectile animatable,
            @Nullable GeoRenderer<SimpleEffectProjectile> renderer) {
        return animatable.getRenderConfig().modelResource();
    }

    @Override
    public ResourceLocation getTextureResource(SimpleEffectProjectile animatable,
            @Nullable GeoRenderer<SimpleEffectProjectile> renderer) {
        return animatable.getRenderConfig().textureResource();
    }

    @Override
    public ResourceLocation getAnimationResource(SimpleEffectProjectile animatable) {
        return animatable.getRenderConfig().animationResource();
    }

    @Override
    public void setCustomAnimations(SimpleEffectProjectile animatable, long instanceId,
            AnimationState<SimpleEffectProjectile> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);

    }
}

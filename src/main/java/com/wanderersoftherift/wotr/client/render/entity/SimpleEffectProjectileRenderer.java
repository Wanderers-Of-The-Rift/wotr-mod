package com.wanderersoftherift.wotr.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.wanderersoftherift.wotr.client.model.geo.ability.SimpleEffectProjectileGeoModel;
import com.wanderersoftherift.wotr.entity.projectile.SimpleEffectProjectile;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

@OnlyIn(Dist.CLIENT)
public class SimpleEffectProjectileRenderer extends GeoEntityRenderer<SimpleEffectProjectile> {
    public SimpleEffectProjectileRenderer(EntityRendererProvider.Context context) {
        super(context, new SimpleEffectProjectileGeoModel());
    }

    @Override
    public void preRender(PoseStack poseStack, SimpleEffectProjectile animatable, BakedGeoModel model,
            @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender,
            float partialTick, int packedLight, int packedOverlay, int renderColor) {
        poseStack.mulPose(Axis.YP.rotationDegrees(animatable.getYRot(partialTick)));
        poseStack.mulPose(Axis.XP.rotationDegrees(-animatable.getXRot(partialTick)));
    }
}

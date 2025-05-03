package com.wanderersoftherift.wotr.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.wanderersoftherift.wotr.config.ClientConfig;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.TurtleModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.SpiderRenderer;
import net.minecraft.client.renderer.entity.state.TurtleRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Spider;
import org.jetbrains.annotations.NotNull;

/**
 * A spider renderer with an alternative renderer when arachnophobia
 * 
 * @param <T>
 */
public class AltSpiderRenderer<T extends Spider>
        extends MobRenderer<T, TurtleRenderState, EntityModel<? super TurtleRenderState>> {

    private final ResourceLocation altTexture;
    private final float altScale;

    private final SpiderRenderer<T> spiderRenderer;

    public AltSpiderRenderer(EntityRendererProvider.Context context, ResourceLocation altTexture, float scale) {
        super(context, new TurtleModel(context.bakeLayer(ModelLayers.TURTLE)), 0.8F * scale);
        this.spiderRenderer = new SpiderRenderer<>(context);
        this.altTexture = altTexture;
        this.altScale = scale;
    }

    public void render(
            @NotNull TurtleRenderState renderState,
            @NotNull PoseStack pose,
            @NotNull MultiBufferSource source,
            int packedLight) {
        if (!ClientConfig.ACCESSIBILITY_ARACHNOPHOBIA.getAsBoolean()) {
            spiderRenderer.render(renderState, pose, source, packedLight);
            return;
        }
        super.render(renderState, pose, source, packedLight);
    }

    @Override
    public @NotNull EntityModel<? super TurtleRenderState> getModel() {
        if (!ClientConfig.ACCESSIBILITY_ARACHNOPHOBIA.getAsBoolean()) {
            return spiderRenderer.getModel();
        }
        return super.getModel();
    }

    @Override
    public void extractRenderState(@NotNull T entity, @NotNull TurtleRenderState state, float partialTick) {
        spiderRenderer.extractRenderState(entity, state, partialTick);
        if (ClientConfig.ACCESSIBILITY_ARACHNOPHOBIA.getAsBoolean()) {
            state.scale = altScale;
        }
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull TurtleRenderState renderState) {
        if (!ClientConfig.ACCESSIBILITY_ARACHNOPHOBIA.getAsBoolean()) {
            return spiderRenderer.getTextureLocation(renderState);
        }
        return altTexture;

    }

    protected float getFlipDegrees() {
        if (!ClientConfig.ACCESSIBILITY_ARACHNOPHOBIA.getAsBoolean()) {
            return 180.f;
        }
        return 0.f;
    }

    @Override
    public @NotNull TurtleRenderState createRenderState() {
        return new TurtleRenderState();
    }
}

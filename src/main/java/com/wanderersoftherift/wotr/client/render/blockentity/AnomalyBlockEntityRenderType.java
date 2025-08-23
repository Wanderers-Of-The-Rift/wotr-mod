package com.wanderersoftherift.wotr.client.render.blockentity;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.wanderersoftherift.wotr.init.client.WotrShaders;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import java.util.function.BiFunction;

public final class AnomalyBlockEntityRenderType {

    public static final RenderStateShard.ShaderStateShard ANOMALY_SHADER_STATE = new RenderStateShard.ShaderStateShard(
            WotrShaders.RIFT_PORTAL);

    public static final BiFunction<ResourceLocation, ResourceLocation, RenderType> ANOMALY = Util
            .memoize((tex1, tex2) -> {
                RenderType.CompositeState state = RenderType.CompositeState.builder()
                        .setShaderState(ANOMALY_SHADER_STATE)
                        .setTextureState(new RenderStateShard.MultiTextureStateShard.Builder().add(tex1, false, false)
                                .add(tex2, false, false)
                                .build())
                        .setTransparencyState(RenderStateShard.NO_TRANSPARENCY)
                        .setCullState(RenderStateShard.NO_CULL)
                        .setLightmapState(RenderStateShard.NO_LIGHTMAP)
                        .setOverlayState(RenderStateShard.NO_OVERLAY)
                        .createCompositeState(false);
                return RenderType.create("anomaly", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 1536, true,
                        false, state);
            });

    private AnomalyBlockEntityRenderType() {
    }

    public static RenderType anomaly(ResourceLocation outerTexture, ResourceLocation innerTexture) {
        return ANOMALY.apply(outerTexture, innerTexture);
    }
}
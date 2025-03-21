package com.wanderersoftherift.wotr.client.render.entity;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.wanderersoftherift.wotr.client.ModShaders;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import java.util.function.BiFunction;

/**
 * A render type for rift portals - these are rendered with an outline texture with a black interior that gets replaced with a fixed view using a second texture
 */
public final class RiftPortalRenderType {

    public static final RenderStateShard.ShaderStateShard RIFT_PORTAL_SHADER_STATE = new RenderStateShard.ShaderStateShard(
            ModShaders.RIFT_PORTAL
    );

    public static final BiFunction<ResourceLocation, ResourceLocation, RenderType> RIFT_PORTAL = Util.memoize(
            (tex1, tex2) -> {
                RenderType.CompositeState rendertype$compositestate = RenderType.CompositeState.builder()
                        .setShaderState(RIFT_PORTAL_SHADER_STATE)
                        .setTextureState(new RenderStateShard.MultiTextureStateShard.Builder()
                                .add(tex1, false, false)
                                .add(tex2, false, false).build())
                        .setTransparencyState(RenderStateShard.NO_TRANSPARENCY)
                        .setCullState(RenderStateShard.NO_CULL)
                        .setLightmapState(RenderStateShard.NO_LIGHTMAP)
                        .setOverlayState(RenderStateShard.NO_OVERLAY).createCompositeState(false);
                return RenderType.create("rift_portal", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 1536, true, false, rendertype$compositestate);
            }
    );

    public static RenderType riftPortal(ResourceLocation outerTexture, ResourceLocation innerTexture) {
        return RIFT_PORTAL.apply(outerTexture, innerTexture);
    }

    private RiftPortalRenderType() {
    }
}

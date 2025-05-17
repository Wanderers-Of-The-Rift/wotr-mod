package com.wanderersoftherift.wotr.init.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import net.minecraft.client.renderer.ShaderDefines;
import net.minecraft.client.renderer.ShaderProgram;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;

@EventBusSubscriber(modid = WanderersOfTheRift.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class WotrShaders {
    public static final ShaderProgram RIFT_PORTAL = new ShaderProgram(WanderersOfTheRift.id("rift_portal"),
            DefaultVertexFormat.NEW_ENTITY, ShaderDefines.EMPTY);
    public static final ShaderProgram RIFT_MAPPER = new ShaderProgram(WanderersOfTheRift.id("rift_mapper"),
            DefaultVertexFormat.POSITION_TEX_COLOR, ShaderDefines.EMPTY);

    @SubscribeEvent
    public static void registerShaderPrograms(RegisterShadersEvent event) {
        event.registerShader(WotrShaders.RIFT_PORTAL);
        event.registerShader(WotrShaders.RIFT_MAPPER);
    }
}

package com.wanderersoftherift.wotr.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import net.minecraft.client.renderer.ShaderDefines;
import net.minecraft.client.renderer.ShaderProgram;

public class ModShaders {
    public static ShaderProgram RIFT_PORTAL = new ShaderProgram(WanderersOfTheRift.id("rift_portal"),
            DefaultVertexFormat.NEW_ENTITY, ShaderDefines.EMPTY);
    public static ShaderProgram RIFT_MAPPER = new ShaderProgram(WanderersOfTheRift.id("rift_mapper"),
            DefaultVertexFormat.POSITION_TEX_COLOR, ShaderDefines.EMPTY);
}

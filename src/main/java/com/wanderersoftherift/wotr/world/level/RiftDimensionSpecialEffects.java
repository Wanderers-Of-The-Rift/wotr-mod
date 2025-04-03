package com.wanderersoftherift.wotr.world.level;

import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

public class RiftDimensionSpecialEffects extends DimensionSpecialEffects {
    public RiftDimensionSpecialEffects() {
        super(
                Float.NaN,
                false,
                DimensionSpecialEffects.SkyType.NONE,
                false,
                false
        );
    }

    @Override
    public @NotNull Vec3 getBrightnessDependentFogColor(@NotNull Vec3 vec3, float v) {
        return new Vec3(0, 0, 0);
    }

    @Override
    public boolean isFoggyAt(int i, int i1) {
        return false;
    }

    @Override
    public boolean renderClouds(@NotNull ClientLevel level, int ticks, float partialTick, double camX, double camY, double camZ, @NotNull Matrix4f modelViewMatrix, @NotNull Matrix4f projectionMatrix) {
        return false;
    }

    @Override
    public boolean renderSky(@NotNull ClientLevel level, int ticks, float partialTick, @NotNull Matrix4f modelViewMatrix, @NotNull Camera camera, @NotNull Matrix4f projectionMatrix, @NotNull Runnable setupFog) {
        return false;
    }

    @Override
    public boolean renderSnowAndRain(@NotNull ClientLevel level, int ticks, float partialTick, double camX, double camY, double camZ) {
        return false;
    }

    @Override
    public boolean tickRain(@NotNull ClientLevel level, int ticks, @NotNull Camera camera) {
        return false;
    }
}

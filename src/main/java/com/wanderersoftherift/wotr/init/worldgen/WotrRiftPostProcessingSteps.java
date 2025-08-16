package com.wanderersoftherift.wotr.init.worldgen;

import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.world.level.levelgen.CorridorBlender;
import com.wanderersoftherift.wotr.world.level.levelgen.RiftPostProcessingStep;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class WotrRiftPostProcessingSteps {
    public static final DeferredRegister<MapCodec<? extends RiftPostProcessingStep>> RIFT_POST_STEPS = DeferredRegister
            .create(WotrRegistries.RIFT_POST_STEPS, WanderersOfTheRift.MODID);

    public static final DeferredHolder<MapCodec<? extends RiftPostProcessingStep>, MapCodec<CorridorBlender>> PERIMETER_GENERATABLE = RIFT_POST_STEPS
            .register("corridor_blender", () -> CorridorBlender.CODEC);

}

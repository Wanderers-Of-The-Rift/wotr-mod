package com.wanderersoftherift.wotr.init.worldgen;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.rift.RiftConfigDataType;
import com.wanderersoftherift.wotr.core.rift.RiftGenerationConfig;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class WotrRiftConfigDataTypes {
    public static final DeferredRegister<RiftConfigDataType<?>> RIFT_CONFIG_DATA_TYPES = DeferredRegister
            .create(WotrRegistries.RIFT_CONFIG_DATA_TYPES, WanderersOfTheRift.MODID);

    public static final DeferredHolder<RiftConfigDataType<?>, RiftConfigDataType<RiftGenerationConfig>> RIFT_GENERATOR_CONFIG = RIFT_CONFIG_DATA_TYPES
            .register("rift_generator", () -> RiftGenerationConfig.TYPE);

}
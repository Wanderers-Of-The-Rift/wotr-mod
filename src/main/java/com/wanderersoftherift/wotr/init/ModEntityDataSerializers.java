package com.wanderersoftherift.wotr.init;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.entity.projectile.SimpleProjectileConfig;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class ModEntityDataSerializers {
    public static final DeferredRegister<EntityDataSerializer<?>> ENTITY_DATA_SERIALIZERS = DeferredRegister
            .create(NeoForgeRegistries.ENTITY_DATA_SERIALIZERS, WanderersOfTheRift.MODID);

    public static final Supplier<EntityDataSerializer<SimpleProjectileConfig.SimpleProjectileConfigRenderConfig>> SIMPLE_PROJECTILE_RENDER_CONFIG = ENTITY_DATA_SERIALIZERS
            .register("simple_projectile_render_config", () -> EntityDataSerializer
                    .forValueType(SimpleProjectileConfig.SimpleProjectileConfigRenderConfig.STREAM_CODEC));
}

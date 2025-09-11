package com.wanderersoftherift.wotr.init;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.entity.mob.RiftMobVariantData;
import com.wanderersoftherift.wotr.entity.portal.RiftEntrance;
import com.wanderersoftherift.wotr.entity.projectile.SimpleProjectileConfig;
import net.minecraft.core.Holder;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class WotrEntityDataSerializers {
    public static final DeferredRegister<EntityDataSerializer<?>> ENTITY_DATA_SERIALIZERS = DeferredRegister
            .create(NeoForgeRegistries.ENTITY_DATA_SERIALIZERS, WanderersOfTheRift.MODID);

    public static final Supplier<EntityDataSerializer<SimpleProjectileConfig.SimpleProjectileConfigRenderConfig>> SIMPLE_PROJECTILE_RENDER_CONFIG = ENTITY_DATA_SERIALIZERS
            .register("simple_projectile_render_config", () -> EntityDataSerializer
                    .forValueType(SimpleProjectileConfig.SimpleProjectileConfigRenderConfig.STREAM_CODEC));
    public static final Supplier<EntityDataSerializer<Holder<RiftMobVariantData>>> MOB_VARIANT_HOLDER = ENTITY_DATA_SERIALIZERS
            .register("mob_variant_holder", () -> EntityDataSerializer.forValueType(RiftMobVariantData.STREAM_CODEC));
    public static final Supplier<EntityDataSerializer<RiftEntrance>> RIFT_ENTRANCE = ENTITY_DATA_SERIALIZERS
            .register("rift_entrance", () -> EntityDataSerializer.forValueType(RiftEntrance.STREAM_CODEC));
}

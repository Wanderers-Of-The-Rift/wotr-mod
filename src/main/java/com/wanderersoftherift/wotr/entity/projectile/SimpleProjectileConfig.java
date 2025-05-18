package com.wanderersoftherift.wotr.entity.projectile;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record SimpleProjectileConfig(int projectiles, int pierce, float velocity, boolean gravityAffected,
        float gravity, int groundPersistTicks, SimpleProjectileConfigRenderConfig renderConfig) {

    public static final SimpleProjectileConfig DEFAULT = new SimpleProjectileConfig(
            1, 0, 1.0F, true, 0.05F, 0, SimpleProjectileConfigRenderConfig.DEFAULT);

    public static final Codec<SimpleProjectileConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.optionalFieldOf("projectiles", 1).forGetter(SimpleProjectileConfig::projectiles),
            Codec.INT.optionalFieldOf("pierce", 0).forGetter(SimpleProjectileConfig::pierce),
            Codec.FLOAT.fieldOf("velocity").forGetter(SimpleProjectileConfig::velocity),
            Codec.BOOL.optionalFieldOf("gravity_affected", true).forGetter(SimpleProjectileConfig::gravityAffected),
            Codec.FLOAT.optionalFieldOf("gravity", 0.05F).forGetter(SimpleProjectileConfig::gravity),
            Codec.INT.optionalFieldOf("ground_persist_ticks", 0).forGetter(SimpleProjectileConfig::groundPersistTicks),
            SimpleProjectileConfigRenderConfig.CODEC.fieldOf("render").forGetter(SimpleProjectileConfig::renderConfig)
    ).apply(instance, SimpleProjectileConfig::new));

    public record SimpleProjectileConfigRenderConfig(ResourceLocation modelResource, ResourceLocation textureResource,
            ResourceLocation animationResource) {

        public static final Codec<SimpleProjectileConfigRenderConfig> CODEC = RecordCodecBuilder
                .create(instance -> instance
                        .group(ResourceLocation.CODEC.fieldOf("model")
                                .forGetter(SimpleProjectileConfigRenderConfig::modelResource),
                                ResourceLocation.CODEC.fieldOf("texture")
                                        .forGetter(SimpleProjectileConfigRenderConfig::textureResource),
                                ResourceLocation.CODEC.fieldOf("animations")
                                        .forGetter(SimpleProjectileConfigRenderConfig::animationResource))
                        .apply(instance, SimpleProjectileConfigRenderConfig::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, SimpleProjectileConfigRenderConfig> STREAM_CODEC = StreamCodec
                .composite(ResourceLocation.STREAM_CODEC, SimpleProjectileConfigRenderConfig::modelResource,
                        ResourceLocation.STREAM_CODEC, SimpleProjectileConfigRenderConfig::textureResource,
                        ResourceLocation.STREAM_CODEC, SimpleProjectileConfigRenderConfig::animationResource,
                        SimpleProjectileConfigRenderConfig::new);
        public static final SimpleProjectileConfigRenderConfig DEFAULT = new SimpleProjectileConfigRenderConfig(
                WanderersOfTheRift.id("geo/ability/fireball.geo.json"),
                WanderersOfTheRift.id("textures/ability/fireball.png"),
                WanderersOfTheRift.id("animations/ability/fireball.animations.json"));
    }

}

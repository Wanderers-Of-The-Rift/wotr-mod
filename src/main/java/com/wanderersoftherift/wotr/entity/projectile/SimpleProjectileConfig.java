package com.wanderersoftherift.wotr.entity.projectile;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public record SimpleProjectileConfig(int projectiles, int pierce, float velocity, boolean gravityAffected,
        float gravity, int groundPersistTicks, SimpleProjectileConfigRenderConfig renderConfig,
        SimpleProjectileConfigSoundConfig soundConfig) {

    public static final SimpleProjectileConfig DEFAULT = new SimpleProjectileConfig(
            1, 0, 1.0F, true, 0.05F, 0, SimpleProjectileConfigRenderConfig.DEFAULT,
            SimpleProjectileConfigSoundConfig.DEFAULT);

    public static final Codec<SimpleProjectileConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.optionalFieldOf("projectiles", 1).forGetter(SimpleProjectileConfig::projectiles),
            Codec.INT.optionalFieldOf("pierce", 0).forGetter(SimpleProjectileConfig::pierce),
            Codec.FLOAT.fieldOf("velocity").forGetter(SimpleProjectileConfig::velocity),
            Codec.BOOL.optionalFieldOf("gravity_affected", true).forGetter(SimpleProjectileConfig::gravityAffected),
            Codec.FLOAT.optionalFieldOf("gravity", 0.05F).forGetter(SimpleProjectileConfig::gravity),
            Codec.INT.optionalFieldOf("ground_persist_ticks", 0).forGetter(SimpleProjectileConfig::groundPersistTicks),
            SimpleProjectileConfigRenderConfig.CODEC.fieldOf("render").forGetter(SimpleProjectileConfig::renderConfig),
            SimpleProjectileConfigSoundConfig.CODEC.optionalFieldOf("sound", SimpleProjectileConfigSoundConfig.DEFAULT)
                    .forGetter(SimpleProjectileConfig::soundConfig)
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

    public record SimpleProjectileConfigSoundConfig(ResourceLocation collisionSound, ResourceLocation fireSound,
            ResourceLocation travelSound) {

        private static final ResourceLocation EMPTY_SOUND = ResourceLocation
                .withDefaultNamespace("intentionally_empty");
        private static final ResourceLocation ARROW_SOUND = ResourceLocation.withDefaultNamespace("entity.arrow.hit");

        public static final Codec<SimpleProjectileConfigSoundConfig> CODEC = RecordCodecBuilder
                .create(instance -> instance
                        .group(ResourceLocation.CODEC.optionalFieldOf("collision", EMPTY_SOUND)
                                .forGetter(SimpleProjectileConfigSoundConfig::collisionSound),
                                ResourceLocation.CODEC.optionalFieldOf("fire", EMPTY_SOUND)
                                        .forGetter(SimpleProjectileConfigSoundConfig::fireSound),
                                ResourceLocation.CODEC.optionalFieldOf("travel", EMPTY_SOUND)
                                        .forGetter(SimpleProjectileConfigSoundConfig::travelSound))
                        .apply(instance, SimpleProjectileConfigSoundConfig::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, SimpleProjectileConfigSoundConfig> STREAM_CODEC = StreamCodec
                .composite(ResourceLocation.STREAM_CODEC, SimpleProjectileConfigSoundConfig::collisionSound,
                        ResourceLocation.STREAM_CODEC, SimpleProjectileConfigSoundConfig::fireSound,
                        ResourceLocation.STREAM_CODEC, SimpleProjectileConfigSoundConfig::travelSound,
                        SimpleProjectileConfigSoundConfig::new);
        public static final SimpleProjectileConfigSoundConfig DEFAULT = new SimpleProjectileConfigSoundConfig(
                ARROW_SOUND, EMPTY_SOUND, EMPTY_SOUND);

        public SoundEvent getCollisionSound() {
            return BuiltInRegistries.SOUND_EVENT.getValue(this.collisionSound);
        }

        public SoundEvent getFireSound() {
            return BuiltInRegistries.SOUND_EVENT.getValue(this.fireSound);
        }

        public SoundEvent getTravelSound() {
            return BuiltInRegistries.SOUND_EVENT.getValue(this.travelSound);
        }

    }

}
package com.wanderersoftherift.wotr.entity.mob;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.serialization.LaxRegistryCodec;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.Map;

public record RiftMobVariantData(Map<Holder<Attribute>, Double> attributes, ResourceLocation texture) {
    public static final Codec<RiftMobVariantData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.unboundedMap(
                    LaxRegistryCodec.create(Registries.ATTRIBUTE), Codec.DOUBLE)
                    .fieldOf("stats")
                    .forGetter(RiftMobVariantData::attributes),
            ResourceLocation.CODEC.fieldOf("texture").forGetter(RiftMobVariantData::texture)
    ).apply(instance, RiftMobVariantData::new)
    );

    public static final Codec<Holder<RiftMobVariantData>> VARIANT_HOLDER_CODEC = LaxRegistryCodec
            .create(WotrRegistries.Keys.MOB_VARIANTS);

    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<RiftMobVariantData>> STREAM_CODEC = ByteBufCodecs
            .holderRegistry(WotrRegistries.Keys.MOB_VARIANTS);

    public static ResourceLocation getTextureForVariant(
            ResourceLocation variantId,
            Registry<RiftMobVariantData> registry) {
        return registry.get(variantId).map(ref -> ref.value().texture()).orElse(variantId);
    }

    // Applies attributes to a LivingEntity, first spawn sets health to max health
    public void applyTo(LivingEntity entity) {
        for (Map.Entry<Holder<Attribute>, Double> entry : attributes.entrySet()) {
            Holder<Attribute> attributeHolder = entry.getKey();
            Double value = entry.getValue();

            AttributeInstance instance = entity.getAttribute(attributeHolder);

            if (instance != null) {
                instance.setBaseValue(value);
                if (attributeHolder.is(Attributes.MAX_HEALTH)) {
                    entity.setHealth(value.floatValue());
                }
            }
        }
    }
}
package com.wanderersoftherift.wotr.entity.mob;

import com.mojang.serialization.Codec;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.Map;
import java.util.Optional;

public record MobVariantData(Map<String, Double> attributes) {
    public static final Codec<MobVariantData> CODEC = Codec.unboundedMap(Codec.STRING, Codec.DOUBLE)
            .xmap(MobVariantData::new, MobVariantData::attributes);

    public static ResourceLocation getTextureForVariant(
            ResourceLocation variantId,
            Registry<MobVariantData> registry,
            String mobType) {
        ResourceLocation prefixedVariantId = WanderersOfTheRift.id(mobType + "/" + variantId.getPath());
        Optional<Holder.Reference<MobVariantData>> holder = registry.get(prefixedVariantId);
        if (holder.isPresent()) {
            return WanderersOfTheRift.id("textures/entity/mob_variant/" + mobType + "/" + variantId.getPath() + ".png");
        }
        return WanderersOfTheRift.id("textures/entity/mob_variant/" + mobType + "/default_" + mobType + ".png");
    }

    // Applies attributes to a LivingEntity, first spawn sets health to max health
    public void applyTo(LivingEntity entity, boolean isInitialSpawn) {
        Registry<Attribute> attributeRegistry = entity.level().registryAccess().lookupOrThrow(Registries.ATTRIBUTE);

        for (Map.Entry<String, Double> entry : attributes.entrySet()) {
            String attributeKey = entry.getKey();
            Double value = entry.getValue();
            ResourceLocation attributeId = ResourceLocation.tryParse(attributeKey);

            if (attributeId != null) {
                Optional<Holder.Reference<Attribute>> holderOpt = attributeRegistry.get(attributeId);
                if (holderOpt.isPresent()) {
                    Holder<Attribute> holder = holderOpt.get();
                    AttributeInstance instance = entity.getAttribute(holder);
                    if (instance != null) {
                        instance.setBaseValue(value);
                        ResourceLocation maxHealthId = BuiltInRegistries.ATTRIBUTE
                                .getKey(Attributes.MAX_HEALTH.value());
                        if (isInitialSpawn && attributeKey.equals(maxHealthId.toString())) {
                            entity.setHealth(value.floatValue());
                        }
                    }
                }
            }
        }
    }
}
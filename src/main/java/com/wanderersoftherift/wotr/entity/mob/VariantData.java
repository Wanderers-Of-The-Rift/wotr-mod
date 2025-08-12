package com.wanderersoftherift.wotr.entity.mob;

import com.mojang.serialization.Codec;
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

public record VariantData(Map<String, Double> attributes) {
    public static final Codec<VariantData> CODEC = Codec.unboundedMap(Codec.STRING, Codec.DOUBLE)
            .xmap(VariantData::new, VariantData::attributes);

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
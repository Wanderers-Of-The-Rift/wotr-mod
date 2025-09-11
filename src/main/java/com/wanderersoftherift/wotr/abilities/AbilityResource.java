package com.wanderersoftherift.wotr.abilities;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.serialization.LaxRegistryCodec;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.attachment.IAttachmentHolder;

public record AbilityResource(int color) {
    public static final Codec<Holder<AbilityResource>> HOLDER_CODEC = LaxRegistryCodec
            .create(WotrRegistries.Keys.ABILITY_RESOURCES);
    public static final Codec<AbilityResource> DIRECT_CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Codec.INT.fieldOf("color").forGetter(AbilityResource::color)
            ).apply(instance, AbilityResource::new)
    );

    public float maxForEntity(IAttachmentHolder holder) {
        return 100; // todo
    }

    public float tickForEntity(IAttachmentHolder holder, float value) {
        return value; // todo
    }

    public float respawnValueForEntity(Entity entity) {
        return 100; // todo
    }
}

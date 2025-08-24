package com.wanderersoftherift.wotr.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;
import java.util.UUID;

public record SerializableDamageSource(Holder<DamageType> type, Optional<UUID> directEntity, Optional<UUID> entity,
        Vec3 position) {

    public static final Codec<SerializableDamageSource> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            DamageType.CODEC.fieldOf("type").forGetter(SerializableDamageSource::type),
            UUIDUtil.CODEC.optionalFieldOf("direct_entity").forGetter(SerializableDamageSource::directEntity),
            UUIDUtil.CODEC.optionalFieldOf("entity").forGetter(SerializableDamageSource::entity),
            Vec3.CODEC.fieldOf("position").forGetter(SerializableDamageSource::position)
    ).apply(instance, SerializableDamageSource::new));

    public SerializableDamageSource(DamageSource base) {
        this(base.typeHolder(), Optional.ofNullable(base.getDirectEntity()).map(Entity::getUUID),
                Optional.ofNullable(base.getEntity()).map(Entity::getUUID), base.sourcePositionRaw());
    }
}

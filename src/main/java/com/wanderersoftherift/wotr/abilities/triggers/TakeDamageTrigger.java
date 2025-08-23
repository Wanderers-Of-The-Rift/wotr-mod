package com.wanderersoftherift.wotr.abilities.triggers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.TrackedAbilityTrigger;
import net.minecraft.core.Holder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;
import java.util.UUID;

public record TakeDamageTrigger(SerializableDamageSource source, float amount) implements TrackedAbilityTrigger {
    private static final MapCodec<TakeDamageTrigger> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            SerializableDamageSource.CODEC.fieldOf("source").forGetter(TakeDamageTrigger::source),
            Codec.FLOAT.fieldOf("amount").forGetter(TakeDamageTrigger::amount)
    ).apply(instance, TakeDamageTrigger::new));

    public static final Type TYPE = new Type(CODEC, null);

    @Override
    public Type type() {
        return TYPE;
    }

    record SerializableDamageSource(Holder<DamageType> type, Optional<UUID> directEntity, Optional<UUID> entity,
            Vec3 position) {

        public static final Codec<SerializableDamageSource> CODEC = RecordCodecBuilder
                .create(instance -> instance.group(
                        DamageType.CODEC.fieldOf("type").forGetter(SerializableDamageSource::type),
                        UUIDUtil.CODEC.optionalFieldOf("direct_entity")
                                .forGetter(SerializableDamageSource::directEntity),
                        UUIDUtil.CODEC.optionalFieldOf("entity").forGetter(SerializableDamageSource::entity),
                        Vec3.CODEC.fieldOf("position").forGetter(SerializableDamageSource::position)
                ).apply(instance, SerializableDamageSource::new));
        SerializableDamageSource(DamageSource base) {
            this(base.typeHolder(), Optional.ofNullable(base.getDirectEntity()).map(Entity::getUUID),
                    Optional.ofNullable(base.getEntity()).map(Entity::getUUID), base.sourcePositionRaw());
        }
    }
}

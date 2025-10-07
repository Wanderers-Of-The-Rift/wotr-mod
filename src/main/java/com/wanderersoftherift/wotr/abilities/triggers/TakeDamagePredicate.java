package com.wanderersoftherift.wotr.abilities.triggers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.init.ability.WotrTrackedAbilityTriggers;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.damagesource.DamageType;

import java.util.Optional;

public record TakeDamagePredicate(Optional<Float> min, Optional<Float> max, Optional<HolderSet<DamageType>> damageType)
        implements TriggerPredicate<TakeDamageTrigger> {

    public static final MapCodec<TakeDamagePredicate> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    Codec.FLOAT.optionalFieldOf("min").forGetter(TakeDamagePredicate::min),
                    Codec.FLOAT.optionalFieldOf("max").forGetter(TakeDamagePredicate::max),
                    RegistryCodecs.homogeneousList(Registries.DAMAGE_TYPE)
                            .optionalFieldOf("types")
                            .forGetter(TakeDamagePredicate::damageType)
            ).apply(instance, TakeDamagePredicate::new)
    );

    @Override
    public Holder<TrackableTrigger.TriggerType<?>> type() {
        return WotrTrackedAbilityTriggers.TAKE_DAMAGE.getDelegate();
    }

    @Override
    public boolean canBeHandledByClient() {
        return min.isEmpty() && max.isEmpty();
    }

    @Override
    public boolean test(TakeDamageTrigger trigger) {
        if (min.isPresent() && trigger.amount() < min.get()) {
            return false;
        }
        if (max.isPresent() && trigger.amount() > max.get()) {
            return false;
        }
        if (damageType.isPresent() && !damageType.get().contains(trigger.source().type())) {
            return false;
        }
        return true;
    }
}

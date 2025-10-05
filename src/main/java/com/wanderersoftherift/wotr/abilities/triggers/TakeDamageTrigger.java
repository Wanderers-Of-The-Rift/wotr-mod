package com.wanderersoftherift.wotr.abilities.triggers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.AbilityContext;
import com.wanderersoftherift.wotr.abilities.attachment.TargetComponent;
import com.wanderersoftherift.wotr.init.WotrDataComponentType;
import com.wanderersoftherift.wotr.util.SerializableDamageSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.EntityHitResult;

public record TakeDamageTrigger(SerializableDamageSource source, float amount) implements TrackableTrigger {
    private static final MapCodec<TakeDamageTrigger> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            SerializableDamageSource.CODEC.fieldOf("source").forGetter(TakeDamageTrigger::source),
            Codec.FLOAT.fieldOf("amount").forGetter(TakeDamageTrigger::amount)
    ).apply(instance, TakeDamageTrigger::new));

    public static final TriggerType<TakeDamageTrigger> TRIGGER_TYPE = new TriggerType<>(
            TakeDamagePredicate.CODEC, null);

    @Override
    public TriggerType<?> type() {
        return TRIGGER_TYPE;
    }

    @Override
    public void addComponents(AbilityContext context) {
        if (!(context.level() instanceof ServerLevel level)) {
            return;
        }
        // Or should this be direct entity? Or maybe we should have a component for each.
        if (source.entity().isEmpty()) {
            return;
        }
        Entity triggerEntity = level.getEntity(source.entity().get());
        if (triggerEntity != null) {
            context.set(WotrDataComponentType.AbilityContextData.TRIGGER_TARGET,
                    new TargetComponent(new EntityHitResult(triggerEntity)));
        }
    }

}

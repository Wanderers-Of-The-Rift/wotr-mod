package com.wanderersoftherift.wotr.abilities.triggers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.AbilityContext;
import com.wanderersoftherift.wotr.abilities.TrackedAbilityTrigger;
import com.wanderersoftherift.wotr.abilities.attachment.TargetComponent;
import com.wanderersoftherift.wotr.init.WotrDataComponentType;
import com.wanderersoftherift.wotr.util.SerializableDamageSource;
import net.minecraft.core.UUIDUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.EntityHitResult;

import java.util.UUID;

public record DealDamageTrigger(SerializableDamageSource source, UUID victim, float amount)
        implements TrackedAbilityTrigger {

    private static final MapCodec<DealDamageTrigger> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            SerializableDamageSource.CODEC.fieldOf("source").forGetter(DealDamageTrigger::source),
            UUIDUtil.CODEC.fieldOf("victim").forGetter(DealDamageTrigger::victim),
            Codec.FLOAT.fieldOf("amount").forGetter(DealDamageTrigger::amount)
    ).apply(instance, DealDamageTrigger::new));

    public static final TriggerType<DealDamageTrigger> TRIGGER_TYPE = new TriggerType<>(CODEC, null);

    @Override
    public TriggerType<DealDamageTrigger> type() {
        return TRIGGER_TYPE;
    }

    @Override
    public void addComponents(AbilityContext context) {
        if (!(context.level() instanceof ServerLevel level)) {
            return;
        }
        Entity victimEntity = level.getEntity(victim);
        if (victimEntity != null) {
            context.set(WotrDataComponentType.AbilityContextData.TRIGGER_TARGET,
                    new TargetComponent(new EntityHitResult(victimEntity)));
        }
    }
}

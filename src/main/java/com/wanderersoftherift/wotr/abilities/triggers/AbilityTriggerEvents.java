package com.wanderersoftherift.wotr.abilities.triggers;

import com.wanderersoftherift.wotr.abilities.attachment.AbilityTracker;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

@EventBusSubscriber
public class AbilityTriggerEvents {

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void damageEvent(LivingDamageEvent.Pre event) {
        var victim = event.getEntity();
        AbilityTracker.forEntity(victim)
                .triggerAbilities(new TakeDamageTrigger(
                        new TakeDamageTrigger.SerializableDamageSource(event.getSource()), event.getNewDamage()));
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void tickEvent(EntityTickEvent.Pre event) {
        var victim = event.getEntity();
        AbilityTracker.forEntity(victim).triggerAbilities(TickTrigger.INSTANCE);
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void attackEvent(LivingIncomingDamageEvent event) {
        var victim = event.getEntity();
        var attacker = event.getSource().getEntity();
        if (attacker != null) {
            AbilityTracker.forEntity(attacker)
                    .triggerAbilities(new DealDamageTrigger(
                            new TakeDamageTrigger.SerializableDamageSource(event.getSource()), victim.getUUID(),
                            event.getAmount()));
        }
    }
}

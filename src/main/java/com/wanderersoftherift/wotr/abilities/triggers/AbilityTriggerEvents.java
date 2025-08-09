package com.wanderersoftherift.wotr.abilities.triggers;

import com.wanderersoftherift.wotr.abilities.attachment.AbilityTracker;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

@EventBusSubscriber
public class AbilityTriggerEvents {

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void damageEvent(LivingDamageEvent.Pre event) {
        var victim = event.getEntity();
        AbilityTracker.forEntity(victim)
                .triggerAbilities(new TakeDamageTrigger(
                        new TakeDamageTrigger.SerializableDamageSource(event.getSource()), event.getNewDamage()));
    }
}

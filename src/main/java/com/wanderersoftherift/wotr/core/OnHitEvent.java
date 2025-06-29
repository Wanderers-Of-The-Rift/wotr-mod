package com.wanderersoftherift.wotr.core;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.init.WotrAttributes;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

@EventBusSubscriber(modid = WanderersOfTheRift.MODID, bus = EventBusSubscriber.Bus.GAME)
public class OnHitEvent {

    @SubscribeEvent
    public static void onCriticalHit(LivingIncomingDamageEvent event) {
        LivingEntity attacker = (LivingEntity) event.getSource().getDirectEntity();
        if (event.getSource().getDirectEntity() instanceof LivingEntity livingAttacker) {
            float dmg = OnHitEffect.critical(event.getAmount(), livingAttacker, event.getEntity(),
                    event.getEntity().getRandom());
            event.setAmount(dmg);
        }
    }

    @SubscribeEvent
    public static void onThornsProc(LivingIncomingDamageEvent event) {
        if (event.getEntity() instanceof LivingEntity living) {
            if (living.getLastAttacker() instanceof LivingEntity attacker) {
                boolean t = OnHitEffect.thorns(living, living.getRandom());
                double thornsDamage = living.getAttributeValue(WotrAttributes.THORNS_DAMAGE);
                if (t) {
                    if (living.getAttributeValue(WotrAttributes.THORNS_DAMAGE) > 1) {
                        attacker.hurt(event.getSource(), (float) thornsDamage);
                    } else {
                        attacker.hurt(event.getSource(), 1);
                    }
                }
            }
        }
    }
}

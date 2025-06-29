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
        if (attacker != null && attacker.attackable()) {
            float dmg = OnHitEffect.critical(event.getAmount(), attacker, event.getEntity(),
                    event.getEntity().getRandom());
            event.setAmount(dmg);
        }
    }

    @SubscribeEvent
    public static void onThornsProc(LivingIncomingDamageEvent event) {
        if (event.getEntity().attackable()) {
            boolean t = OnHitEffect.thorns(event.getEntity(), event.getEntity().getRandom());
            double thornsDamage = event.getEntity().getAttributeValue(WotrAttributes.THORNS_DAMAGE);
            if (t) {
                if(event.getEntity().getAttributeValue(WotrAttributes.THORNS_DAMAGE) > 1) {
                    event.getEntity().getLastAttacker().hurt(event.getSource(), (float) thornsDamage);
                } else {
                    event.getEntity().getLastAttacker().hurt(event.getSource(), 1);
                }
            }
        }
    }
}

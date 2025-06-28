package com.wanderersoftherift.wotr.core;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

@EventBusSubscriber(modid = WanderersOfTheRift.MODID, bus = EventBusSubscriber.Bus.GAME)
public class OnHitEvent {

    @SubscribeEvent
    public static void onHit(LivingIncomingDamageEvent event) {
        float dmg = OnHitEffect.critical(event.getAmount(), event.getEntity(), event.getEntity(), event.getEntity().getRandom());
        event.setAmount(dmg);
    }
}

package com.wanderersoftherift.wotr.effects.events;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

@EventBusSubscriber(modid = WanderersOfTheRift.MODID, bus = EventBusSubscriber.Bus.GAME)
public class DamageTypeEvents {

    @SubscribeEvent
    public static void onLivingDamageEventPost(LivingDamageEvent.Post event) {

        /*
         * if (WotrDamageTypes.FIRE_DAMAGE.equals(event.getSource().typeHolder().getKey())) {
         * event.getEntity().addEffect(new MobEffectInstance(WotrMobEffects.FIRE_BURN_EFFECT, 20 * 30)); }
         */
    }
}

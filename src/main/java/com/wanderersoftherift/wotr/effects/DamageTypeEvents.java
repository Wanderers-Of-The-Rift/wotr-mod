package com.wanderersoftherift.wotr.effects;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.init.ModDamageTypes;
import com.wanderersoftherift.wotr.init.ModMobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

@EventBusSubscriber(modid = WanderersOfTheRift.MODID, bus = EventBusSubscriber.Bus.GAME)
public class DamageTypeEvents {

    @SubscribeEvent
    public static void onLivingDamageEventPost(LivingDamageEvent.Post event) {
        if (ModDamageTypes.FIRE_DAMAGE.equals(event.getSource().typeHolder().getKey())) {
            event.getEntity().addEffect(new MobEffectInstance(ModMobEffects.FIRE_BURN_EFFECT, 20 * 30));
        }
    }
}

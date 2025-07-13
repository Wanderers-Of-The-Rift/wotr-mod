package com.wanderersoftherift.wotr.effects.events;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.effects.CriticalEffect;
import com.wanderersoftherift.wotr.effects.LifeLeechEffect;
import com.wanderersoftherift.wotr.effects.ThornsEffect;
import com.wanderersoftherift.wotr.init.WotrAttributes;
import com.wanderersoftherift.wotr.init.WotrDamageTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

@EventBusSubscriber(modid = WanderersOfTheRift.MODID, bus = EventBusSubscriber.Bus.GAME)
public class OnHitEvent {

    @SubscribeEvent
    public static void criticalEvent(LivingIncomingDamageEvent event) {
        Entity causer = event.getSource().getEntity();
        if (causer instanceof LivingEntity living) {
            event.setAmount(
                    CriticalEffect.calcCriticalDamage(event.getAmount(), living, event.getEntity(),
                            living.getRandom()));
        }
    }

    @SubscribeEvent
    public static void thornsEvent(LivingDamageEvent.Post event) {
        Level level = event.getEntity().level();
        Entity causer = event.getSource().getEntity();
        LivingEntity receiver = event.getEntity();
        if (causer instanceof LivingEntity livCauser && causer.isAlive()) {
            if (!event.getSource().is(WotrDamageTypes.THORNS_DAMAGE) && !event.getSource().is(DamageTypes.THORNS)
                    && event.getNewDamage() != 0) {
                DamageSource thornsDamageSource = new DamageSource(
                        level.registryAccess().get(WotrDamageTypes.THORNS_DAMAGE).get(), receiver, receiver,
                        receiver.position());
                float thornsDamage = (float) ThornsEffect.calcThornsDamage(receiver, receiver.getRandom());
                if (thornsDamage != 0) {
                    livCauser.hurtServer((ServerLevel) level, thornsDamageSource, thornsDamage);
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void lifeLeechEvent(LivingDamageEvent.Pre event) {
        Entity causer = event.getSource().getEntity();
        LivingEntity receiver = event.getEntity();
        if (causer instanceof LivingEntity livCauser && causer.isAlive()) {
            if (event.getNewDamage() != 0) {
                livCauser.heal(LifeLeechEffect.calcHeal(livCauser, receiver, event.getNewDamage()));
            }
        }
    }
}

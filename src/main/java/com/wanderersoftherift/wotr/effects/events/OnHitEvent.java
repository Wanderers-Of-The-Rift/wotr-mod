package com.wanderersoftherift.wotr.effects.events;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.effects.CriticalEffect;
import com.wanderersoftherift.wotr.effects.LifeLeechEffect;
import com.wanderersoftherift.wotr.effects.ThornsEffect;
import com.wanderersoftherift.wotr.init.WotrAttributes;
import com.wanderersoftherift.wotr.init.WotrDamageTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

@EventBusSubscriber(modid = WanderersOfTheRift.MODID, bus = EventBusSubscriber.Bus.GAME)
public class OnHitEvent {

    @SubscribeEvent
    public static void onCriticalHit(LivingIncomingDamageEvent event) {
        if (event.getSource().getEntity() instanceof LivingEntity livingAttacker) {
            float dmg = CriticalEffect.calcFinalDamage(event.getAmount(), livingAttacker, event.getEntity(),
                    event.getEntity().getRandom());
            event.setAmount(dmg);
        }
    }

    @SubscribeEvent
    public static void checkThornsProc(LivingDamageEvent.Post event) {
        Level level = event.getEntity().level();
        DamageSource thorns = new DamageSource(
                level.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(WotrDamageTypes.THORNS_DAMAGE));

        Entity causer = event.getSource().getEntity();
        LivingEntity receiver = event.getEntity();
        if (!event.getSource().is(DamageTypes.THORNS) && !event.getSource().is(WotrDamageTypes.THORNS_DAMAGE) && causer != null && event.getOriginalDamage() != 0) {
            int thornsProc = ThornsEffect.calcThornsMult(event.getEntity(), event.getEntity().getRandom());
            if (thornsProc != 0) {
                causer.hurtServer((ServerLevel) level, thorns,
                        (float) receiver.getAttributeValue(WotrAttributes.THORNS_DAMAGE) * thornsProc);
            }
        }
    }

    @SubscribeEvent
    public static void lifeLeech(LivingDamageEvent.Pre event) {
        LivingEntity causer = (LivingEntity) event.getSource().getEntity();
        LivingEntity receiver = event.getEntity();
        if (causer != null) {
            causer.setHealth(LifeLeechEffect.calcHeal(causer, receiver, event.getOriginalDamage()));
        }
    }
}

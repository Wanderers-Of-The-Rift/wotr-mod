package com.wanderersoftherift.wotr.core;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.init.WotrAttributes;
import com.wanderersoftherift.wotr.init.WotrDamageTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

@EventBusSubscriber(modid = WanderersOfTheRift.MODID, bus = EventBusSubscriber.Bus.GAME)
public class OnHitEvent {

    @SubscribeEvent
    public static void onCriticalHit(LivingIncomingDamageEvent event) {
        if (event.getSource().getDirectEntity() instanceof LivingEntity livingAttacker) {
            float dmg = OnHitEffect.critical(event.getAmount(), livingAttacker, event.getEntity(),
                    event.getEntity().getRandom());
            event.setAmount(dmg);
        }
    }

    @SubscribeEvent
    public static void onThornsProc(LivingIncomingDamageEvent event) {
        Level level = event.getEntity().level();
        DamageSource thorns = new DamageSource(
                level.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(WotrDamageTypes.THORNS_DAMAGE));
        Entity causer = event.getSource().getEntity();
        LivingEntity reciever = event.getEntity();
        if (!WotrDamageTypes.THORNS_DAMAGE.equals(event.getSource().typeHolder()) && causer != null) {
            int thornsProc = OnHitEffect.thorns(event.getEntity(), event.getEntity().getRandom());
            if (thornsProc != 0) {
                causer.hurtServer((ServerLevel) level, thorns,
                        (float) reciever.getAttributeValue(WotrAttributes.THORNS_DAMAGE) * thornsProc);
            } else {
                causer.hurtServer((ServerLevel) level, thorns,
                        (float) reciever.getAttributeValue(WotrAttributes.THORNS_DAMAGE));
            }
        }
    }
}

package com.wanderersoftherift.wotr.effects.events;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.effects.CriticalEffect;
import com.wanderersoftherift.wotr.effects.LifeLeechEffect;
import com.wanderersoftherift.wotr.effects.ThornsEffect;
import com.wanderersoftherift.wotr.init.WotrDamageTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

@EventBusSubscriber(modid = WanderersOfTheRift.MODID, bus = EventBusSubscriber.Bus.GAME)
public class OnHitEvent {

    @SubscribeEvent
    public static void criticalEvent(LivingIncomingDamageEvent event) {
        Entity causer = event.getSource().getEntity();
        if (causer != null) {
            LivingEntity living = (LivingEntity) causer;
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

        if (causer instanceof LivingEntity) {
            if (!event.getSource().is(WotrDamageTypes.THORNS_DAMAGE) && !event.getSource().is(DamageTypes.THORNS)) {
                DamageSource thornsDamageSource = new DamageSource(
                        level.registryAccess()
                                .lookupOrThrow(Registries.DAMAGE_TYPE)
                                .getOrThrow(WotrDamageTypes.THORNS_DAMAGE));
                causer.hurtServer((ServerLevel) level, thornsDamageSource,
                        (float) ThornsEffect.calcThornsDamage(receiver, receiver.getRandom()));
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void lifeLeechEvent(LivingDamageEvent.Pre event) {
        Entity causer = event.getSource().getEntity();
        LivingEntity receiver = event.getEntity();
        DamageContainer container = event.getContainer();
        if (causer != null && causer.isAlive() && !(causer instanceof Projectile)) {
            LivingEntity livCauser = (LivingEntity) causer;
            if (event.getOriginalDamage() != 0 && container.getBlockedDamage() == 0
                    && container.getReduction(DamageContainer.Reduction.ARMOR) == 0
                    && container.getReduction(DamageContainer.Reduction.ENCHANTMENTS) == 0) {
                livCauser.setHealth(
                        livCauser.getHealth()
                                + LifeLeechEffect.calcHeal(livCauser, receiver, event.getNewDamage()));
            }
        }
    }
}

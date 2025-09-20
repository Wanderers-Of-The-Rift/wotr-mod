package com.wanderersoftherift.wotr.abilities.triggers;

import com.wanderersoftherift.wotr.abilities.attachment.AbilityTracker;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.util.SerializableDamageSource;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

@EventBusSubscriber
public class AbilityTriggerEvents {

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void damageEvent(LivingDamageEvent.Pre event) {
        var victim = event.getEntity();
        AbilityTracker.forEntity(victim)
                .triggerAbilities(new TakeDamageTrigger(
                        new SerializableDamageSource(event.getSource()), event.getNewDamage()));
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void tickEvent(ServerTickEvent.Pre event) {
        for (ServerLevel level : event.getServer().getAllLevels()) {
            level.getData(WotrAttachments.TICK_TRIGGER_REGISTRY)
                    .forEach((entity, abilityTracker) -> abilityTracker.triggerAbilities(TickTrigger.INSTANCE));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void attackEvent(LivingIncomingDamageEvent event) {
        var victim = event.getEntity();
        var attacker = event.getSource().getEntity();
        if (attacker != null) {
            AbilityTracker.forEntity(attacker)
                    .triggerAbilities(new DealDamageTrigger(
                            new SerializableDamageSource(event.getSource()), victim.getUUID(), event.getAmount()));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void breakBlockEvent(BlockEvent.BreakEvent event) {
        if (event.isCanceled()) {
            return;
        }
        AbilityTracker.forEntity(event.getPlayer())
                .triggerAbilities(new BreakBlockTrigger(event.getPos(), event.getPlayer().getDirection()));
    }
}

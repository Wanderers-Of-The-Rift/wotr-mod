package com.wanderersoftherift.wotr.client;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.abilities.attachment.TriggerTracker;
import com.wanderersoftherift.wotr.abilities.triggers.DealDamageTrigger;
import com.wanderersoftherift.wotr.abilities.triggers.KillTrigger;
import com.wanderersoftherift.wotr.abilities.triggers.TakeDamageTrigger;
import com.wanderersoftherift.wotr.abilities.triggers.TickTrigger;
import com.wanderersoftherift.wotr.entity.LivingDamageHandledEvent;
import com.wanderersoftherift.wotr.util.SerializableDamageSource;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

/**
 * Events related to abilities - key activation detection and mana ticking.
 */
@EventBusSubscriber(modid = WanderersOfTheRift.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
@OnlyIn(Dist.CLIENT)
public final class AbilityClientTriggerEvents {

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void damageEvent(LivingDamageHandledEvent event) {
        var victim = event.getEntity();
        TriggerTracker.forEntity(victim)
                .trigger(new TakeDamageTrigger(new SerializableDamageSource(event.getSource()),
                        Float.NaN /* unknown amount */));
    }

    @SubscribeEvent
    public static void tickEvent(ClientTickEvent.Pre event) {
        var player = Minecraft.getInstance().player;
        if (player != null) {
            TriggerTracker.forEntity(player).trigger(TickTrigger.INSTANCE);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void attackEvent(LivingDamageHandledEvent event) {
        var victim = event.getEntity();
        var attacker = event.getSource().getEntity();
        if (attacker != null) {
            TriggerTracker.forEntity(attacker)
                    .trigger(new DealDamageTrigger(new SerializableDamageSource(event.getSource()), victim.getUUID(),
                            Float.NaN /* unknown amount */));
        }
    }

    @SubscribeEvent
    private static void clientKill(LivingDeathEvent event) {
        var victim = event.getEntity();
        var source = event.getEntity().getLastDamageSource();
        if (source == null) {
            return;
        }
        var attacker = source.getEntity(); // might not match the server counterpart, but event is unaware of the actual
                                           // source
        if (attacker == Minecraft.getInstance().player) {
            TriggerTracker.forEntity(attacker)
                    .trigger(new KillTrigger(new SerializableDamageSource(event.getSource()), victim.getUUID()));
        }
    }

}

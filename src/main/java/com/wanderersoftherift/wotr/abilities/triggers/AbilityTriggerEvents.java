package com.wanderersoftherift.wotr.abilities.triggers;

import com.wanderersoftherift.wotr.abilities.attachment.TriggerTracker;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.loot.LootEvent;
import com.wanderersoftherift.wotr.util.SerializableDamageSource;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

@EventBusSubscriber
public class AbilityTriggerEvents {

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void damageEvent(LivingDamageEvent.Pre event) {
        var victim = event.getEntity();
        TriggerTracker.forEntity(victim)
                .trigger(new TakeDamageTrigger(
                        new SerializableDamageSource(event.getSource()), event.getNewDamage()));
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void tickEvent(ServerTickEvent.Pre event) {
        for (ServerLevel level : event.getServer().getAllLevels()) {
            level.getData(WotrAttachments.TICK_TRIGGER_REGISTRY)
                    .forEach((entity, abilityTracker) -> abilityTracker.trigger(TickTrigger.INSTANCE));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void attackEvent(LivingIncomingDamageEvent event) {
        var victim = event.getEntity();
        var attacker = event.getSource().getEntity();
        if (attacker != null) {
            TriggerTracker.forEntity(attacker)
                    .trigger(new DealDamageTrigger(
                            new SerializableDamageSource(event.getSource()), victim.getUUID(), event.getAmount()));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void killEvent(LivingDeathEvent event) {
        var victim = event.getEntity();
        var attacker = event.getSource().getEntity();
        if (attacker != null) {
            TriggerTracker.forEntity(attacker)
                    .trigger(new KillTrigger(
                            new SerializableDamageSource(event.getSource()), victim.getUUID()));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void lootEvent(LootEvent.PlayerOpensChest event) {
        // todo this currently doesn't trigger properly when breaking chests
        if (event.getLootContext().hasParameter(LootContextParams.THIS_ENTITY)
                && event.getContainer() instanceof BlockEntity entity) {
            var looter = event.getLootContext().getParameter(LootContextParams.THIS_ENTITY);
            TriggerTracker.forEntity(looter)
                    .trigger(new LootTrigger(BuiltInRegistries.BLOCK_ENTITY_TYPE.wrapAsHolder(entity.getType()),
                            event.getLootTable().getParamSet(), event.getLootTable().getLootTableId()));
        }
    }
}

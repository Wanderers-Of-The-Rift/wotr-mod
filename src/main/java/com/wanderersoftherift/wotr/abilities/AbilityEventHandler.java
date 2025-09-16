package com.wanderersoftherift.wotr.abilities;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.abilities.attachment.OngoingAbilities;
import com.wanderersoftherift.wotr.core.inventory.slot.AbilityEquipmentSlot;
import com.wanderersoftherift.wotr.core.inventory.slot.WotrEquipmentSlotEvent;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.modifier.CollectEquipmentSlotsEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import static com.wanderersoftherift.wotr.init.WotrRegistries.Keys.ABILITIES;

/**
 * Handles setting and ticking attachment data relating to abilities
 */
@EventBusSubscriber(modid = WanderersOfTheRift.MODID, bus = EventBusSubscriber.Bus.GAME)
public class AbilityEventHandler {

    @SubscribeEvent
    public static void collectAbilitySlots(CollectEquipmentSlotsEvent event) {
        if (event.getEntity() instanceof IAttachmentHolder holder) {
            var slotCount = holder.getData(WotrAttachments.ABILITY_SLOTS).getSlots();
            for (int i = 0; i < slotCount && i < AbilityEquipmentSlot.SLOTS.size(); i++) {
                event.getSlots().add(AbilityEquipmentSlot.SLOTS.get(i));
            }
        }
    }

    @SubscribeEvent
    public static void logAvailableAbilities(ServerStartingEvent event) {
        WanderersOfTheRift.LOGGER
                .info("Server loaded pack exists: " + event.getServer().registryAccess().lookup(ABILITIES).isPresent());
        if (event.getServer().registryAccess().lookup(ABILITIES).isPresent()) {
            event.getServer().registryAccess().lookup(ABILITIES).get().asHolderIdMap().forEach((ability) -> {
                WanderersOfTheRift.LOGGER.info(" - {}", ability.getKey());
            });
        }
    }

    @SubscribeEvent
    public static void serverTick(ServerTickEvent.Pre event) {
        for (ServerLevel level : event.getServer().getAllLevels()) {
            tickAttachedEffects(level);
            tickActiveAbilities(level);
        }
    }

    @SubscribeEvent
    public static void onSlotChanged(WotrEquipmentSlotEvent.Changed event) {
        event.getEntity()
                .getExistingData(WotrAttachments.ONGOING_ABILITIES)
                .ifPresent(data -> data.slotChanged(event.getSlot(), event.getFrom(), event.getTo()));
    }

    @SubscribeEvent
    public static void onItemUsed(LivingEntityUseItemEvent event) {
        event.getEntity()
                .getExistingData(WotrAttachments.ONGOING_ABILITIES)
                .ifPresent(OngoingAbilities::interruptChannelledAbilities);
    }

    @SubscribeEvent
    public static void onWeaponUsed(AttackEntityEvent event) {
        event.getEntity()
                .getExistingData(WotrAttachments.ONGOING_ABILITIES)
                .ifPresent(OngoingAbilities::interruptChannelledAbilities);
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent respawnEvent) {
        giveInitialAbilityResources(respawnEvent.getEntity());
    }

    @SubscribeEvent
    public static void onEntitySpawn(FinalizeSpawnEvent spawnEvent) {
        giveInitialAbilityResources(spawnEvent.getEntity());
    }

    public static void giveInitialAbilityResources(Entity entity) {
        var resourceRegistry = entity.registryAccess().lookup(WotrRegistries.Keys.ABILITY_RESOURCES);

        if (!resourceRegistry.isPresent()) {
            return;
        }

        resourceRegistry.get()
                .stream()
                .map(resourceRegistry.get()::wrapAsHolder)
                .forEach(
                        resource -> {
                            var respawnValue = resource.value().respawnValueForEntity(entity);
                            if (respawnValue > 0) {
                                entity.getData(WotrAttachments.ABILITY_RESOURCE_DATA).setAmount(resource, respawnValue);
                            }
                        }
                );
    }

    public static void tickAttachedEffects(ServerLevel level) {
        level.getData(WotrAttachments.ATTACHED_EFFECT_ENTITY_REGISTRY).forEach((entity, data) -> {
            data.tick();
            if (data.isEmpty()) {
                entity.removeData(WotrAttachments.ATTACHED_EFFECTS);
            }
        });
    }

    public static void tickActiveAbilities(ServerLevel level) {
        level.getData(WotrAttachments.ONGOING_ABILITY_ENTITY_REGISTRY).forEach((entity, data) -> {
            data.tick();
            if (data.isEmpty()) {
                entity.removeData(WotrAttachments.ONGOING_ABILITIES);
            }
        });
    }

}

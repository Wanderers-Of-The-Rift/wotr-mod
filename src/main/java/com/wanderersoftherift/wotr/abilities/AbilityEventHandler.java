package com.wanderersoftherift.wotr.abilities;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.abilities.attachment.OngoingAbilities;
import com.wanderersoftherift.wotr.core.inventory.slot.AbilityEquipmentSlot;
import com.wanderersoftherift.wotr.core.inventory.slot.WotrEquipmentSlotEvent;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.init.WotrAttributes;
import com.wanderersoftherift.wotr.modifier.ModifierHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
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
    public static void collectAbilitySlots(ModifierHelper.CollectEquipmentSlotsEvent event) {
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
            tickMana(level);
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
        AttributeInstance maxManaAttribute = respawnEvent.getEntity().getAttribute(WotrAttributes.MAX_MANA);
        if (maxManaAttribute != null) {
            respawnEvent.getEntity().getData(WotrAttachments.MANA).setAmount((float) maxManaAttribute.getValue());
        }
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

    public static void tickMana(ServerLevel level) {
        level.getData(WotrAttachments.MANA_ENTITY_REGISTRY).forEach((entity, manaData) -> {
            manaData.tick();
        });
    }
}

package com.wanderersoftherift.wotr.abilities;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.abilities.attachment.AbilitySlots;
import com.wanderersoftherift.wotr.abilities.attachment.AttachedEffectData;
import com.wanderersoftherift.wotr.abilities.attachment.ManaData;
import com.wanderersoftherift.wotr.abilities.attachment.PlayerCooldownData;
import com.wanderersoftherift.wotr.abilities.attachment.PlayerDurationData;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.init.WotrAttributes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import static com.wanderersoftherift.wotr.init.WotrRegistries.Keys.ABILITIES;

/**
 * Handles setting and ticking attachment data relating to abilities
 */
@EventBusSubscriber(modid = WanderersOfTheRift.MODID, bus = EventBusSubscriber.Bus.GAME)
public class AbilityEvents {

    @SubscribeEvent
    public static void logAvailableAbilities(ServerStartingEvent event) {
        WanderersOfTheRift.LOGGER
                .info("Server loaded pack exists: " + event.getServer().registryAccess().lookup(ABILITIES).isPresent());
        if (event.getServer().registryAccess().lookup(ABILITIES).isPresent()) {
            for (AbstractAbility ability : event.getServer().registryAccess().lookup(ABILITIES).get()) {
                WanderersOfTheRift.LOGGER.info(ability.getName().toString());
            }
        }
    }

    @SubscribeEvent
    public static void serverTick(ServerTickEvent.Pre event) {
        for (ServerLevel level : event.getServer().getAllLevels()) {
            tickAttachedEffects(level);
            tickMana(level);
        }
    }

    @SubscribeEvent
    public static void playerTick(PlayerTickEvent.Post event) {
        Player p = event.getEntity();

        PlayerCooldownData cooldowns = p.getData(WotrAttachments.ABILITY_COOLDOWNS);
        cooldowns.reduceCooldowns();
        p.setData(WotrAttachments.ABILITY_COOLDOWNS, cooldowns);

        // TODO replace this with similar situation to above
        PlayerDurationData durations = p.getData(WotrAttachments.DURATIONS);
        AbilitySlots abilitySlots = p.getData(WotrAttachments.ABILITY_SLOTS);
        for (int slot = 0; slot < abilitySlots.getSlots(); slot++) {
            AbstractAbility ability = abilitySlots.getAbilityInSlot(slot);
            if (ability != null && durations.isDurationRunning(ability.getName())) {
                if (durations.get(ability.getName()) == 1) {
                    ability.onDeactivate(p, slot);
                }
                if (ability.isActive(p)) {
                    ability.tick(p);
                }
            }
        }

        durations.reduceDurations();
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent respawnEvent) {
        AttributeInstance maxManaAttribute = respawnEvent.getEntity().getAttribute(WotrAttributes.MAX_MANA);
        if (maxManaAttribute != null) {
            respawnEvent.getEntity()
                    .getData(WotrAttachments.MANA)
                    .setAmount(respawnEvent.getEntity(), (int) maxManaAttribute.getValue());
        }
    }

    public static void tickAttachedEffects(ServerLevel level) {
        level.getEntities(EntityTypeTest.forClass(LivingEntity.class),
                entity -> entity.hasData(WotrAttachments.ATTACHED_EFFECTS)).forEach(entity -> {
                    AttachedEffectData data = entity.getData(WotrAttachments.ATTACHED_EFFECTS);
                    data.tick(entity, level);
                    if (data.isEmpty()) {
                        entity.removeData(WotrAttachments.ATTACHED_EFFECTS);
                    }
                });
    }

    public static void tickMana(ServerLevel level) {
        level.getPlayers(player -> player.hasData(WotrAttachments.MANA)).forEach(player -> {
            ManaData data = player.getData(WotrAttachments.MANA);
            data.tick(player);
        });
    }
}

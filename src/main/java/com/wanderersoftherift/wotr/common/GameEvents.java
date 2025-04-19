package com.wanderersoftherift.wotr.common;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.abilities.AbstractAbility;
import com.wanderersoftherift.wotr.abilities.attachment.AbilitySlots;
import com.wanderersoftherift.wotr.abilities.attachment.PlayerCooldownData;
import com.wanderersoftherift.wotr.abilities.attachment.PlayerDurationData;
import com.wanderersoftherift.wotr.init.ModAttachments;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import static com.wanderersoftherift.wotr.init.RegistryEvents.ABILITY_REGISTRY;

@EventBusSubscriber(modid = WanderersOfTheRift.MODID, bus = EventBusSubscriber.Bus.GAME)
public class GameEvents {

    // TODO This probably placeholder, maybe find a better way to handle this
    /*
     * This ticks for each player to reduce their overall cooldowns, and durations.
     */
    @SubscribeEvent
    public static void playerTick(PlayerTickEvent.Post event) {
        Player p = event.getEntity();

        PlayerCooldownData cooldowns = p.getData(ModAttachments.ABILITY_COOLDOWNS);
        cooldowns.reduceCooldowns();
        p.setData(ModAttachments.ABILITY_COOLDOWNS, cooldowns);

        // TODO replace this with similar situation to above
        PlayerDurationData durations = p.getData(ModAttachments.DURATIONS);
        AbilitySlots abilitySlots = p.getData(ModAttachments.ABILITY_SLOTS);
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
    public static void serverLoaded(ServerStartingEvent event) {
        WanderersOfTheRift.LOGGER.info("Server loaded pack exists: "
                + event.getServer().registryAccess().lookup(ABILITY_REGISTRY).isPresent());
        if (event.getServer().registryAccess().lookup(ABILITY_REGISTRY).isPresent()) {
            for (AbstractAbility ability : event.getServer().registryAccess().lookup(ABILITY_REGISTRY).get()) {
                WanderersOfTheRift.LOGGER.info(ability.getName().toString());
            }
        }
    }
}

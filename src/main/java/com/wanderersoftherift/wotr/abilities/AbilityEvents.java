package com.wanderersoftherift.wotr.abilities;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.abilities.attachment.AttachedEffectData;
import com.wanderersoftherift.wotr.abilities.attachment.ManaData;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.init.WotrAttributes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

/**
 * Handles setting and ticking attachment data relating to abilities
 */
@EventBusSubscriber(modid = WanderersOfTheRift.MODID, bus = EventBusSubscriber.Bus.GAME)
public class AbilityEvents {

    @SubscribeEvent
    public static void serverTick(ServerTickEvent.Pre event) {
        for (ServerLevel level : event.getServer().getAllLevels()) {
            tickAttachedEffects(level);
            tickMana(level);
        }
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

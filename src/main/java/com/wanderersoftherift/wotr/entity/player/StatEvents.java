package com.wanderersoftherift.wotr.entity.player;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.init.WotrAttributes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber
public final class StatEvents {

    @SubscribeEvent
    public static void onPlayerSpawnEvent(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            player.getData(WotrAttachments.BASE_STATISTICS).applyStatistics();
        }
    }

    @SubscribeEvent
    public static void onStrengthChanged(PlayerAttributeChangedEvent event) {
        if (WotrAttributes.STRENGTH.equals(event.getAttribute())) {
            event.getEntity().displayClientMessage(Component.literal("Strength changed"), false);
            AttributeMap attributes = event.getEntity().getAttributes();
            int strength = (int) event.getEntity().getAttributeValue(WotrAttributes.STRENGTH);
            attributes.getInstance(Attributes.ATTACK_DAMAGE)
                    .addOrReplacePermanentModifier(new AttributeModifier(WanderersOfTheRift.id("strength_bonus"),
                            strength * 0.2f, AttributeModifier.Operation.ADD_VALUE));
            attributes.getInstance(Attributes.KNOCKBACK_RESISTANCE)
                    .addOrReplacePermanentModifier(new AttributeModifier(WanderersOfTheRift.id("strength_bonus"),
                            strength * 0.05f, AttributeModifier.Operation.ADD_VALUE));
        }
    }
}

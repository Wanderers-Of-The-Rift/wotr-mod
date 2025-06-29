package com.wanderersoftherift.wotr.core.rift;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.init.WotrTags;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.BlockItem;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.Set;

import static com.wanderersoftherift.wotr.entity.portal.PortalSpawnLocation.DEFAULT_RIFT_EXIT_POSITION;

@EventBusSubscriber
public class RiftEvents {

    @SubscribeEvent
    public static void onPlaceBlock(PlayerInteractEvent.RightClickBlock event) {
        if (!RiftLevelManager.isRift(event.getLevel())) {
            return;
        }
        if (event.getItemStack().is(WotrTags.Items.BANNED_IN_RIFT)
                || (event.getItemStack().getItem() instanceof BlockItem blockItem
                        && blockItem.getBlock().defaultBlockState().is(WotrTags.Blocks.BANNED_IN_RIFT))) {
            event.setUseItem(TriState.FALSE);
            event.getEntity()
                    .displayClientMessage(
                            Component.translatable(WanderersOfTheRift.translationId("message", "disabled_in_rifts")),
                            true);
        }
    }

    @SubscribeEvent
    public static void onPlayerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        ServerLevel originLevel = RiftLevelManager.getRiftLevel(event.getFrom().location());
        if (originLevel == null) {
            return;
        }
        RiftData riftData = RiftData.get(originLevel);
        if (riftData.containsPlayer(event.getEntity())) {
            var position = DEFAULT_RIFT_EXIT_POSITION.offset(3, 0, 3);
            event.getEntity()
                    .teleportTo(originLevel, position.getX(), position.getY(), position.getZ(), Set.of(),
                            event.getEntity().getYRot(), event.getEntity().getXRot(), false);
        }
    }

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            RiftLevelManager.onPlayerDeath(player, player.serverLevel());
        }
    }
}

package com.wanderersoftherift.wotr.core.rift;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.init.WotrAttachments;
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
        if (!event.getItemStack().is(WotrTags.Items.BANNED_IN_RIFT)
                && (!(event.getItemStack().getItem() instanceof BlockItem blockItem)
                        || !blockItem.getBlock().defaultBlockState().is(WotrTags.Blocks.BANNED_IN_RIFT))) {
            return;
        }
        event.setUseItem(TriState.FALSE);
        event.getEntity()
                .displayClientMessage(
                        Component.translatable(WanderersOfTheRift.translationId("message", "disabled_in_rifts")), true);
    }

    @SubscribeEvent
    public static void onPlayerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        var riftEntryStates = player.getData(WotrAttachments.RIFT_ENTRY_STATES);
        if (riftEntryStates.isEmpty()) {
            return;
        }
        var lastEntryState = riftEntryStates.getLast();
        ServerLevel riftLevel = RiftLevelManager.getRiftLevel(lastEntryState.riftDimension());
        if (riftLevel == null || lastEntryState.riftDimension().equals(event.getTo())) {
            return;
        }
        RiftData riftData = RiftData.get(riftLevel);
        if (!riftData.containsPlayer(player)) {
            return;
        }
        var position = DEFAULT_RIFT_EXIT_POSITION.offset(3, 0, 3);
        player.teleportTo(riftLevel, position.getX(), position.getY(), position.getZ(), Set.of(), player.getYRot(),
                player.getXRot(), false);
    }

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            event.setCanceled(RiftLevelManager.onPlayerDeath(player, event));
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        var deathRiftEntryState = player.getData(WotrAttachments.DEATH_RIFT_ENTRY_STATE);
        if (deathRiftEntryState == RiftEntryState.EMPTY) {
            return;
        }
        var newRift = RiftLevelManager.getRiftLevel(deathRiftEntryState.previousDimension());
        if (newRift == null) {
            return;
        }
        var position = deathRiftEntryState.previousPosition();
        player.teleportTo(newRift, position.x(), position.y(), position.z(), Set.of(), player.getYRot(),
                player.getXRot(), false);
    }
}

package com.wanderersoftherift.wotr.core.rift.map;

import com.wanderersoftherift.wotr.core.rift.RiftLevelManager;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.world.level.FastRiftGenerator;
import com.wanderersoftherift.wotr.world.level.levelgen.space.RoomRiftSpace;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityEvent;

@EventBusSubscriber
public class RiftMapEventHandler {
    @SubscribeEvent
    public static void onPlayerChangedSection(EntityEvent.EnteringSection event) {
        if (!(event.getEntity() instanceof Player player) || !RiftLevelManager.isRift(player.level())) {
            return;
        }
        if (!(player.level() instanceof ServerLevel level)
                || !(level.getChunkSource().getGenerator() instanceof FastRiftGenerator generator)) {
            return;
        }

        if (generator.getOrCreateLayout(level.getServer())
                .getChunkSpace(event.getNewPos()) instanceof RoomRiftSpace room) {
            player.level().getData(WotrAttachments.RIFT_MAP_DATA).enterRoom(room, player);
        }
    }
}

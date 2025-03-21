package com.wanderersoftherift.wotr.rift.objective;


import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.network.S2CRiftObjectiveStatusPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Optional;

@EventBusSubscriber(modid = WanderersOfTheRift.MODID)
public class RiftObjectiveEvents {

    @SubscribeEvent
    public static void onPlayerJoinLevel(EntityJoinLevelEvent event) {
        if(event.getEntity().level() instanceof ServerLevel serverLevel && event.getEntity() instanceof ServerPlayer player) {
            LevelRiftObjectiveData data = LevelRiftObjectiveData.getFromLevel(serverLevel);
            if(data.getObjective() != null) {
                PacketDistributor.sendToPlayer(player, new S2CRiftObjectiveStatusPacket(Optional.of(data.getObjective())));
            }else{
                PacketDistributor.sendToPlayer(player, new S2CRiftObjectiveStatusPacket(Optional.empty()));
            }
        }
    }
}

package com.wanderersoftherift.wotr.entity.mob;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;

@EventBusSubscriber(modid = WanderersOfTheRift.MODID)
public class RiftMobVariantEventHandler {
    @SubscribeEvent
    public static void onEntityFinalizeSpawn(FinalizeSpawnEvent event) {
        if (event.getEntity() instanceof VariedRiftMob mob) {
            mob.applyVariantStats();
        }
    }
}
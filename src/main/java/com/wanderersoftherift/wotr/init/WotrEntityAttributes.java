package com.wanderersoftherift.wotr.init;

import com.wanderersoftherift.wotr.entity.mob.RiftZombie;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;

import static com.wanderersoftherift.wotr.WanderersOfTheRift.MODID;
import static net.neoforged.fml.common.EventBusSubscriber.Bus;

@EventBusSubscriber(bus = Bus.MOD, modid = MODID)
public class WotrEntityAttributes {

    @SubscribeEvent
    public static void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
        event.put(WotrEntities.RIFT_ZOMBIE.get(), RiftZombie.createAttributes().build());
    }
}
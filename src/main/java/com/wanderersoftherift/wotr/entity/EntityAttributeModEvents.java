package com.wanderersoftherift.wotr.entity;

import com.wanderersoftherift.wotr.entity.mob.NoirZombie;
import com.wanderersoftherift.wotr.init.WotrEntities;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;

import static com.wanderersoftherift.wotr.WanderersOfTheRift.MODID;
import static net.neoforged.fml.common.EventBusSubscriber.Bus;

@EventBusSubscriber(bus = Bus.MOD, modid = MODID)
public class EntityAttributeModEvents {

    @SubscribeEvent
    public static void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
        event.put(WotrEntities.NOIR_ZOMBIE.get(), NoirZombie.createAttributes().build());
    }
}
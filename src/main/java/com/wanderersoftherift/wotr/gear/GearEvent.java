package com.wanderersoftherift.wotr.gear;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;

@EventBusSubscriber(modid = WanderersOfTheRift.MODID, bus = EventBusSubscriber.Bus.GAME)
public class GearEvent {

    @SubscribeEvent
    public static void useBasic(InputEvent.MouseButton.Pre input, ServerPlayer player){
        ItemStack item = player.getMainHandItem();
        if(input.getButton() == 0 ){

        }
    }
}

package com.wanderersoftherift.wotr.events.client;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.events.RenderEvents;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.TextureAtlasStitchedEvent;

@EventBusSubscriber(modid = WanderersOfTheRift.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class TextureStitchEvent {
    @SubscribeEvent
    public static void onStitch(TextureAtlasStitchedEvent event) {
        RenderEvents.resetCached();
    }
}

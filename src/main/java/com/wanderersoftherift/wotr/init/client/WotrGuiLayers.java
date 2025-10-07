package com.wanderersoftherift.wotr.init.client;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

@EventBusSubscriber(modid = WanderersOfTheRift.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class WotrGuiLayers {
    public static final ResourceLocation ABILITY_BAR = WanderersOfTheRift.id("ability_bar");
    public static final ResourceLocation EFFECT_BAR = WanderersOfTheRift.id("effect_bar");
    public static final ResourceLocation ABILITY_RESOURCE_BARS = WanderersOfTheRift.id("ability_resource_bars");
    public static final ResourceLocation OBJECTIVE = WanderersOfTheRift.id("objective");

    @SubscribeEvent
    public static void registerGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.HOTBAR, ABILITY_BAR, WotrConfigurableLayers.ABILITY_BAR.get());
        event.registerAbove(ABILITY_BAR, ABILITY_RESOURCE_BARS, WotrConfigurableLayers.ABILITY_RESOURCE_BARS.get());
        event.registerAbove(ABILITY_RESOURCE_BARS, EFFECT_BAR, WotrConfigurableLayers.EFFECT_BAR.get());
        event.registerAboveAll(OBJECTIVE, WotrConfigurableLayers.OBJECTIVE.get());
    }
}

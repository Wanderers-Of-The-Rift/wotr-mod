package com.wanderersoftherift.wotr.init.client;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

@EventBusSubscriber(modid = WanderersOfTheRift.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModGuiLayers {
    public static final ResourceLocation ABILITY_BAR = WanderersOfTheRift.id("ability_bar");
    public static final ResourceLocation EFFECT_BAR = WanderersOfTheRift.id("effect_bar");
    public static final ResourceLocation MANA_BAR = WanderersOfTheRift.id("mana_bar");
    public static final ResourceLocation OBJECTIVE = WanderersOfTheRift.id("objective");

    @SubscribeEvent
    public static void registerGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.HOTBAR, ABILITY_BAR, ModConfigurableLayers.ABILITY_BAR.get());
        event.registerAbove(ABILITY_BAR, MANA_BAR, ModConfigurableLayers.MANA_BAR.get());
        event.registerAbove(MANA_BAR, EFFECT_BAR, ModConfigurableLayers.EFFECT_BAR.get());
        event.registerAboveAll(OBJECTIVE, ModConfigurableLayers.OBJECTIVE.get());
    }
}

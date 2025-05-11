package com.wanderersoftherift.wotr.gui.config;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.gui.screen.settings.HudConfigOptionsScreen;
import com.wanderersoftherift.wotr.init.client.ModConfigurableLayers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * Event handlers related to configurable layers
 */
@EventBusSubscriber(modid = WanderersOfTheRift.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public final class ConfigurableLayerEvents {

    private static final Map<ResourceLocation, ConfigurableLayerAdapter> adapterLookup = new HashMap<>();
    private static boolean adaptersMapped = false;

    private ConfigurableLayerEvents() {

    }

    /**
     * Injects the configure hud button in the pause screen
     * 
     * @param event
     */
    @SubscribeEvent
    public static void postInitPauseScreen(ScreenEvent.Init.Post event) {
        if (event.getScreen() instanceof PauseScreen) {
            event.addListener(
                    Button.builder(Component.translatable(WanderersOfTheRift.translationId("screen", "configure_hud")),
                            button -> {
                                Minecraft minecraft = Minecraft.getInstance();
                                minecraft.setScreen(new HudConfigOptionsScreen());
                                minecraft.mouseHandler.releaseMouse();
                            }).pos(0, 0).size(80, 20).build());
        }
    }

    /**
     * Allows adapters to adjust the rendering of their linked layers
     * 
     * @param event
     */
    @SubscribeEvent
    public static void preRenderLayers(RenderGuiLayerEvent.Pre event) {
        if (!adaptersMapped) {
            mapAdapters();
        }
        ConfigurableLayerAdapter adapter = adapterLookup.get(event.getName());
        if (adapter != null) {
            if (adapter.getConfig().isVisible()) {
                adapter.preRender(event.getGuiGraphics());
            } else {
                event.setCanceled(true);
            }
        }
    }

    /**
     * Allows adapters to clean up after adjusting rendering for their layers
     * 
     * @param event
     */
    @SubscribeEvent
    public static void postRenderLayers(RenderGuiLayerEvent.Post event) {
        ConfigurableLayerAdapter proxy = adapterLookup.get(event.getName());
        if (proxy != null) {
            proxy.postRender(event.getGuiGraphics());
        }
    }

    private static void mapAdapters() {
        ModConfigurableLayers.CONFIGURABLE_LAYER_REGISTRY.stream()
                .filter(x -> x instanceof FixedSizeLayerAdapter)
                .map(FixedSizeLayerAdapter.class::cast)
                .forEach(x -> {
                    x.targetLayers().forEach(id -> adapterLookup.put(id, x));
                });
        adaptersMapped = true;
    }
}

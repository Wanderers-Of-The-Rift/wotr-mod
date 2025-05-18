package com.wanderersoftherift.wotr.init.client;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.gui.config.ConfigurableLayer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

@EventBusSubscriber(modid = WanderersOfTheRift.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class WotrClientRegistries {

    public static final Registry<ConfigurableLayer> CONFIGURABLE_LAYERS = new RegistryBuilder<>(
            Keys.CONFIGURABLE_LAYERS).create();

    public static final class Keys {

        public static final ResourceKey<Registry<ConfigurableLayer>> CONFIGURABLE_LAYERS = ResourceKey
                .createRegistryKey(WanderersOfTheRift.id("configurable_layer"));

        private Keys() {
        }
    }

    private WotrClientRegistries() {
    }

    @SubscribeEvent
    public static void registerRegistries(NewRegistryEvent event) {
        event.register(CONFIGURABLE_LAYERS);
    }
}

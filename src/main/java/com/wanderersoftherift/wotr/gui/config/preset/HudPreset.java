package com.wanderersoftherift.wotr.gui.config.preset;

import com.mojang.serialization.Codec;
import com.wanderersoftherift.wotr.gui.config.ConfigurableLayer;
import com.wanderersoftherift.wotr.init.client.ModConfigurableLayers;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * A HUD preset provides the configuration for multiple hud elements/layers. Any layer that doesn't have a configuration
 * will use its defaults.
 * 
 * @param id         The resource location identifying the HUD preset
 * @param elementMap A map of ConfigurableLayer id to ElementPreset to configure it
 */
public record HudPreset(ResourceLocation id, Map<ResourceLocation, ElementPreset> elementMap) {
    public static final Codec<Map<ResourceLocation, ElementPreset>> MAP_CODEC = Codec
            .unboundedMap(ResourceLocation.CODEC, ElementPreset.CODEC);

    /**
     * @param registryAccess
     * @return A HUD preset mapping from the current configuration
     */
    public static Map<ResourceLocation, ElementPreset> fromConfig(RegistryAccess registryAccess) {
        Registry<ConfigurableLayer> registry = registryAccess
                .lookupOrThrow(ModConfigurableLayers.CONFIGURABLE_LAYER_KEY);
        return registry.stream()
                .filter(layer -> !layer.getConfig().isDefault())
                .collect(Collectors.toMap(registry::getKey,
                        layer -> new ElementPreset(layer.getConfig().isVisible(), layer.getConfig().getAnchor(),
                                layer.getConfig().getX(), layer.getConfig().getY(),
                                layer.getConfig().hasOrientation() ? Optional.of(layer.getConfig().getOrientation())
                                        : Optional.empty())));
    }
}

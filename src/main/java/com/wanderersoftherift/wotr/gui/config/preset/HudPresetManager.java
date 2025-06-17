package com.wanderersoftherift.wotr.gui.config.preset;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.neoforged.fml.loading.FMLPaths;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * PresetManager loads and holds the collection of available HUD presets
 */
public class HudPresetManager implements PreparableReloadListener {
    public static final ResourceLocation CUSTOM_ID = WanderersOfTheRift.id("custom");
    private static final String PATH = "hud_preset";
    private static final String EXTENSION = ".json";
    private static final Path CUSTOM_PRESET_LOCATION = FMLPaths.CONFIGDIR.get().resolve("custom_hud_preset.json");
    private static final HudPresetManager INSTANCE = new HudPresetManager();

    private Map<ResourceLocation, HudPreset> presets = new LinkedHashMap<>();
    private HudPreset custom = new HudPreset(CUSTOM_ID, ImmutableMap.of());

    private HudPresetManager() {
        if (Files.isRegularFile(CUSTOM_PRESET_LOCATION)) {
            try (Reader reader = Files.newBufferedReader(CUSTOM_PRESET_LOCATION)) {
                JsonElement jsonElement = JsonParser.parseReader(reader);
                DataResult<Map<ResourceLocation, ElementPreset>> result = HudPreset.MAP_CODEC.parse(JsonOps.INSTANCE,
                        jsonElement);
                result.ifSuccess((preset) -> custom = new HudPreset(CUSTOM_ID, preset))
                        .ifError(error -> WanderersOfTheRift.LOGGER.error("Failed to read custom hud preset: {}",
                                error.message()));
            } catch (IOException e) {
                WanderersOfTheRift.LOGGER.error("Failed to read custom hud present", e);
            }
        }
        presets.put(CUSTOM_ID, custom);
    }

    public static HudPresetManager getInstance() {
        return INSTANCE;
    }

    /**
     * @return The preset used for local customisation
     */
    public HudPreset getCustom() {
        return custom;
    }

    /**
     * Updates and saves the custom preset to reflect the player's local config
     * 
     * @param access
     */
    public void updateCustom(RegistryAccess access) {
        custom = new HudPreset(CUSTOM_ID, HudPreset.fromConfig(access));
        // save
        DataResult<JsonElement> json = HudPreset.MAP_CODEC.encodeStart(JsonOps.INSTANCE, custom.elementMap());
        json.ifSuccess(data -> {
            try (BufferedWriter writer = Files.newBufferedWriter(CUSTOM_PRESET_LOCATION)) {
                writer.write(data.toString());
            } catch (IOException e) {
                WanderersOfTheRift.LOGGER.error("Failed to write custom hud preset", e);
            }
        });

        presets.put(CUSTOM_ID, custom);
    }

    public HudPreset getPreset(ResourceLocation id) {
        return presets.get(id);
    }

    public Collection<HudPreset> getPresets() {
        return presets.values();
    }

    @Override
    public @NotNull CompletableFuture<Void> reload(
            @NotNull PreparationBarrier barrier,
            @NotNull ResourceManager manager,
            @NotNull Executor backgroundExecutor,
            @NotNull Executor gameExecutor) {
        return this.load(manager, backgroundExecutor).thenCompose(barrier::wait).thenAcceptAsync(values -> {
            values.put(CUSTOM_ID, custom);
            this.presets.clear();
            this.presets.putAll(values);
        }, gameExecutor);
    }

    private CompletableFuture<Map<ResourceLocation, HudPreset>> load(ResourceManager manager, Executor executor) {
        return CompletableFuture.supplyAsync(() -> load(manager), executor);
    }

    private static Map<ResourceLocation, HudPreset> load(ResourceManager manager) {
        Map<ResourceLocation, HudPreset> values = new LinkedHashMap<>();

        for (Map.Entry<ResourceLocation, Resource> hudPreset : manager
                .listResources(PATH, resourceLocation -> resourceLocation.getPath().endsWith(EXTENSION))
                .entrySet()) {
            ResourceLocation location = ResourceLocation.fromNamespaceAndPath(hudPreset.getKey().getNamespace(),
                    hudPreset.getKey()
                            .getPath()
                            .substring(PATH.length() + 1, hudPreset.getKey().getPath().length() - EXTENSION.length()));
            try (Reader reader = hudPreset.getValue().openAsReader()) {
                JsonElement jsonElement = JsonParser.parseReader(reader);
                DataResult<Map<ResourceLocation, ElementPreset>> result = HudPreset.MAP_CODEC.parse(JsonOps.INSTANCE,
                        jsonElement);
                result.ifSuccess((preset) -> values.put(location, new HudPreset(location, preset)))
                        .ifError(error -> WanderersOfTheRift.LOGGER.error("Failed to read hud_preset '{}': {}",
                                location, error.message()));
            } catch (IOException e) {
                WanderersOfTheRift.LOGGER.error("Failed to read hud_preset '{}'", location, e);
            }
        }

        return values;
    }
}

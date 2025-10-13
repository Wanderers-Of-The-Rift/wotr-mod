package com.wanderersoftherift.wotr.datagen;

import com.google.common.hash.Hashing;
import com.google.common.hash.HashingOutputStream;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.datagen.textures.TextureGenerator;
import com.wanderersoftherift.wotr.datagen.textures.TextureGeneratorCollector;
import com.wanderersoftherift.wotr.datagen.textures.TextureTransform;
import net.minecraft.Util;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.neoforged.neoforge.common.extensions.IModelProviderExtension;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class WotrTextureProvider implements DataProvider, IModelProviderExtension {
    private final ResourceManager resourceManager;
    private final PackOutput.PathProvider texturePathProvider;

    static Map<Integer, Integer> processorBlockColorMap = new HashMap<>();

    static {
        processorBlockColorMap.put(15, 0x5EDFB2);
        processorBlockColorMap.put(14, 0xC96C7E);
        processorBlockColorMap.put(13, 0xA2DD7A);
        processorBlockColorMap.put(12, 0xFFFFFF);
        processorBlockColorMap.put(11, 0xF66FED);
        processorBlockColorMap.put(10, 0xC773F2);
        processorBlockColorMap.put(9, 0x609CF9);
        processorBlockColorMap.put(8, 0x64D9F5);
        processorBlockColorMap.put(7, 0x636C7E);
        processorBlockColorMap.put(6, 0xC9D97E);
        processorBlockColorMap.put(5, 0x646FF5);
        processorBlockColorMap.put(4, 0x9360F9);
        processorBlockColorMap.put(3, 0x63DA7E);
        processorBlockColorMap.put(2, 0xCE7D67);
        processorBlockColorMap.put(1, 0x888F9C);
    }

    enum TextureVariant {
        Block(""),
        DirectionPillar("directional_pillar"),
        DirectionPillarTop("directional_pillar_top"),
        Glass("glass"),
        GlassPaneTop("glass_pane_top"),
        Trapdoor("trapdoor");

        private final String suffix;

        TextureVariant(String suffix) { this.suffix = suffix; }
        String getSuffix() { return this.suffix; }
    }

    WotrTextureProvider(ResourceManager resourceManager, PackOutput output) {
        this.texturePathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "textures");
        this.resourceManager = resourceManager;
    }

    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput cachedOutput) {
        TextureGeneratorCollector textureCollector = new TextureGeneratorCollector(resourceManager, cachedOutput, texturePathProvider);

        this.registerTextures(new TextureGenerator(textureCollector));

        return textureCollector.save();
    }

    public void registerTextures(TextureGenerator textureGenerator) {
        processorBlockColorMap.forEach( (number, color) -> {
            // Make all processor blocks textures
            Arrays.stream(TextureVariant.values()).toList().forEach((variant) -> {
                String source_path = !Objects.equals(variant.getSuffix(), "") ? "block/processor_block/processor_block_template_" + variant.getSuffix() : "block/processor_block/processor_block_template";

                textureGenerator.tintGenerator(
                        ResourceLocation.fromNamespaceAndPath(WanderersOfTheRift.MODID, source_path),
                        ResourceLocation.fromNamespaceAndPath(WanderersOfTheRift.MODID, source_path.replace("template", String.valueOf(number)).replace("processor_block/", "")),
                        color
                );
            });

            // Make all fluid block textures
            textureGenerator.tintGenerator(
                    ResourceLocation.withDefaultNamespace("block/water_flow"),
                    ResourceLocation.fromNamespaceAndPath(WanderersOfTheRift.MODID, "block/water_flow_" + number),
                    color
            );

            textureGenerator.tintGenerator(
                    ResourceLocation.withDefaultNamespace("block/water_still"),
                    ResourceLocation.fromNamespaceAndPath(WanderersOfTheRift.MODID, "block/water_still_" + number),
                    color
            );

        });
    }

    @Override
    public @NotNull String getName() {
        return "Wotr Texture Definition";
    }
}

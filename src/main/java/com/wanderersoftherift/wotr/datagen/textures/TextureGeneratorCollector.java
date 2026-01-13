package com.wanderersoftherift.wotr.datagen.textures;

import com.google.common.hash.Hashing;
import com.google.common.hash.HashingOutputStream;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import net.minecraft.Util;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceMetadata;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class TextureGeneratorCollector implements Consumer<TextureTransform> {
    private final ResourceManager resourceManager;
    private final CachedOutput output;
    private final PackOutput.PathProvider pathProvider;
    private final ArrayList<TextureTransform> transforms;

    public TextureGeneratorCollector(ResourceManager resourceManager, CachedOutput output,
            PackOutput.PathProvider pathProvider) {
        this.resourceManager = resourceManager;
        this.transforms = new ArrayList<>();
        this.output = output;
        this.pathProvider = pathProvider;
    }

    @Override
    public void accept(TextureTransform textureTransform) {
        transforms.add(textureTransform);
    }

    public CompletableFuture<?> applyTransform(TextureTransform transform) {
        ResourceLocation resourceLocation = transform.sourcePath.withPrefix("textures/").withSuffix(".png");
        Path pathOut = pathProvider.file(transform.destinationPath, "png");

        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
        HashingOutputStream hashingoutputstream = new HashingOutputStream(Hashing.sha1(), bytearrayoutputstream);

        try {
            Resource input = resourceManager.getResource(resourceLocation).orElseThrow();
            ResourceMetadata resourceMeta = input.metadata();
            AnimationMetadataSection textureMeta = resourceMeta.getSection(AnimationMetadataSection.TYPE).orElse(null);

            InputStream r = input.open();

            ImageInputStream imageInputStream = ImageIO.createImageInputStream(r);
            BufferedImage image = ImageIO.read(imageInputStream);

            int width = image.getWidth();
            int height = image.getHeight();

            BufferedImage out = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    int transform1 = transform.transform(x, y, image.getRGB(x, y));
                    out.setRGB(x, y, transform1);
                }
            }

            ImageOutputStream imageOutputStream = ImageIO.createImageOutputStream(hashingoutputstream);
            ImageIO.write(out, "png", imageOutputStream);

            if (textureMeta != null) {
                JsonElement animation = AnimationMetadataSection.CODEC.encodeStart(JsonOps.INSTANCE, textureMeta)
                        .getOrThrow();
                JsonObject json = new JsonObject();
                json.add("animation", animation);
                DataProvider.saveStable(output, json, pathProvider.file(transform.destinationPath, "png.mcmeta"));
            }

            output.writeIfNeeded(pathOut, bytearrayoutputstream.toByteArray(), hashingoutputstream.hash());

        } catch (Exception e) {
            WanderersOfTheRift.LOGGER.warn("WotrTextureProvider - Error with resource {}", resourceLocation);
        }

        return CompletableFuture.completedFuture(transform.destinationPath);
    }

    public CompletableFuture<?> applyTransformAsync(TextureTransform transform) {
        return CompletableFuture.runAsync(() -> applyTransform(transform),
                Util.backgroundExecutor().forName("saveStable"));
    }

    public CompletableFuture<?> save() {
        WanderersOfTheRift.LOGGER.info("TextureGeneratorCollector save {}", transforms.size());
        return CompletableFuture
                .allOf(transforms.stream().map(this::applyTransformAsync).toArray(CompletableFuture[]::new));
    }
}
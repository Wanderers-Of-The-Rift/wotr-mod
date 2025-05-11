package com.wanderersoftherift.wotr.datagen.provider;

import com.mojang.serialization.Codec;
import com.wanderersoftherift.wotr.item.riftkey.KeyForgeRecipe;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public abstract class KeyForgeRecipeProvider<T extends Holder<?>> implements DataProvider {

    private final String name;
    private final PackOutput.PathProvider pathProvider;
    private final CompletableFuture<HolderLookup.Provider> registries;
    private final Codec<T> outputCodec;

    public KeyForgeRecipeProvider(String name, PackOutput output, CompletableFuture<HolderLookup.Provider> registries,
            ResourceKey<? extends Registry<KeyForgeRecipe<T>>> outputRegistryKey, Codec<T> outputCodec) {
        this.name = name;
        this.pathProvider = output.createRegistryElementsPathProvider(outputRegistryKey);
        this.registries = registries;
        this.outputCodec = outputCodec;
    }

    public abstract void generate(HolderLookup.Provider registries, Consumer<KeyForgeRecipe<T>> writer);

    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput output) {
        return this.registries.thenCompose(provider -> {
            Set<ResourceLocation> set = new HashSet<>();
            List<CompletableFuture<?>> list = new ArrayList<>();
            Consumer<KeyForgeRecipe<T>> consumer = (recipe) -> {
                if (!set.add(ResourceLocation.parse(recipe.getOutput().getRegisteredName()))) {
                    throw new IllegalStateException("Duplicate recipe " + recipe.getOutput().getRegisteredName());
                } else {
                    Path path = this.pathProvider.json(ResourceLocation.parse(recipe.getOutput().getRegisteredName()));
                    list.add(
                            DataProvider.saveStable(output, provider, KeyForgeRecipe.codec(outputCodec), recipe, path));
                }
            };

            generate(provider, consumer);

            return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
        });
    }

    @Override
    public final @NotNull String getName() {
        return name;
    }
}

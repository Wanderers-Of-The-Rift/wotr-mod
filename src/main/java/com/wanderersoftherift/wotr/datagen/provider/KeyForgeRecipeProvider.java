package com.wanderersoftherift.wotr.datagen.provider;

import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.item.riftkey.KeyForgeRecipe;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public abstract class KeyForgeRecipeProvider implements DataProvider {

    private final String name;
    private final PackOutput.PathProvider pathProvider;
    private final CompletableFuture<HolderLookup.Provider> registries;

    public KeyForgeRecipeProvider(String name, PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        this.name = name;
        this.pathProvider = output.createRegistryElementsPathProvider(WotrRegistries.Keys.KEY_FORGE_RECIPES);
        this.registries = registries;
    }

    public abstract void generate(HolderLookup.Provider registries, Consumer<KeyForgeRecipe> writer);

    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput output) {
        return this.registries.thenCompose(provider -> {
            Set<ResourceLocation> set = new HashSet<>();
            List<CompletableFuture<?>> list = new ArrayList<>();
            Consumer<KeyForgeRecipe> consumer = (recipe) -> {
                Holder<?> holder = (Holder<?>) recipe.getOutput();
                if (!set.add(ResourceLocation.parse(holder.getRegisteredName()))) {
                    throw new IllegalStateException("Duplicate recipe " + holder.getRegisteredName());
                } else {
                    Path path = this.pathProvider.json(ResourceLocation.parse(holder.getRegisteredName()));
                    list.add(
                            DataProvider.saveStable(output, provider, KeyForgeRecipe.codec(), recipe, path));
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

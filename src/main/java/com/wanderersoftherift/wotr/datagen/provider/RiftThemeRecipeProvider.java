package com.wanderersoftherift.wotr.datagen.provider;

import com.wanderersoftherift.wotr.init.RegistryEvents;
import com.wanderersoftherift.wotr.item.riftkey.ThemeRecipe;
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

public abstract class RiftThemeRecipeProvider implements DataProvider {

    private final PackOutput.PathProvider pathProvider;
    private final CompletableFuture<HolderLookup.Provider> registries;

    public RiftThemeRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        this.pathProvider = output.createRegistryElementsPathProvider(RegistryEvents.RIFT_THEME_RECIPE);
        this.registries = registries;
    }

    public abstract void generate(HolderLookup.Provider registries, Consumer<ThemeRecipe> writer);

    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput output) {
        return this.registries.thenCompose(provider -> {
            Set<ResourceLocation> set = new HashSet<>();
            List<CompletableFuture<?>> list = new ArrayList<>();
            Consumer<ThemeRecipe> consumer = (recipe) -> {
                if (!set.add(ResourceLocation.parse(recipe.getTheme().getRegisteredName()))) {
                    throw new IllegalStateException("Duplicate recipe " + recipe.getTheme().getRegisteredName());
                } else {
                    Path path = this.pathProvider.json(ResourceLocation.parse(recipe.getTheme().getRegisteredName()));
                    list.add(DataProvider.saveStable(output, provider, ThemeRecipe.CODEC, recipe, path));
                }
            };

            generate(provider, consumer);

            return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
        });
    }

    @Override
    public final @NotNull String getName() {
        return "RiftThemeRecipe";
    }
}

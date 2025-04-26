package com.wanderersoftherift.wotr.datagen;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.datagen.provider.RiftThemeRecipeProvider;
import com.wanderersoftherift.wotr.init.ModRiftThemes;
import com.wanderersoftherift.wotr.item.riftkey.EssencePredicate;
import com.wanderersoftherift.wotr.item.riftkey.ThemeRecipe;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class ModRiftThemeRecipeProvider extends RiftThemeRecipeProvider {

    public ModRiftThemeRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    public void generate(HolderLookup.Provider registries, Consumer<ThemeRecipe> writer) {
        writer.accept(new ThemeRecipe.Builder(
                DeferredHolder.create(ModRiftThemes.RIFT_THEME_KEY, WanderersOfTheRift.id("cave"))).setPriority(-1)
                .build());
        writer.accept(new ThemeRecipe.Builder(
                DeferredHolder.create(ModRiftThemes.RIFT_THEME_KEY, WanderersOfTheRift.id("forest")))
                .withEssenceReq(new EssencePredicate.Builder(WanderersOfTheRift.id("life")).setMinPercent(50f).build())
                .build());

    }
}

package com.wanderersoftherift.wotr.datagen;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.codec.DeferrableRegistryCodec;
import com.wanderersoftherift.wotr.datagen.provider.KeyForgeRecipeProvider;
import com.wanderersoftherift.wotr.init.ModRiftThemes;
import com.wanderersoftherift.wotr.init.RegistryEvents;
import com.wanderersoftherift.wotr.item.riftkey.EssencePredicate;
import com.wanderersoftherift.wotr.item.riftkey.KeyForgeRecipe;
import com.wanderersoftherift.wotr.world.level.levelgen.theme.RiftTheme;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class ModRiftThemeRecipeProvider extends KeyForgeRecipeProvider<Holder<RiftTheme>> {

    public ModRiftThemeRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super("RiftThemeRecipe", output, registries, RegistryEvents.RIFT_THEME_RECIPE,
                DeferrableRegistryCodec.create(ModRiftThemes.RIFT_THEME_KEY));
    }

    @Override
    public void generate(HolderLookup.Provider registries, Consumer<KeyForgeRecipe<Holder<RiftTheme>>> writer) {
        writer.accept(KeyForgeRecipe.create(
                (Holder<RiftTheme>) DeferredHolder.create(ModRiftThemes.RIFT_THEME_KEY, WanderersOfTheRift.id("cave")))
                .setPriority(-1)
                .build());
        writer.accept(KeyForgeRecipe
                .create((Holder<RiftTheme>) DeferredHolder.create(ModRiftThemes.RIFT_THEME_KEY,
                        WanderersOfTheRift.id("forest")))
                .withEssenceReq(new EssencePredicate.Builder(WanderersOfTheRift.id("plant")).setMinPercent(50f).build())
                .build());
        writer.accept(KeyForgeRecipe
                .create((Holder<RiftTheme>) DeferredHolder.create(ModRiftThemes.RIFT_THEME_KEY,
                        WanderersOfTheRift.id("processor")))
                .withEssenceReq(
                        new EssencePredicate.Builder(WanderersOfTheRift.id("processor")).setMinPercent(1F).build())
                .build());
        writer.accept(KeyForgeRecipe
                .create((Holder<RiftTheme>) DeferredHolder.create(ModRiftThemes.RIFT_THEME_KEY,
                        WanderersOfTheRift.id("mushroom")))
                .withEssenceReq(
                        new EssencePredicate.Builder(WanderersOfTheRift.id("mushroom")).setMinPercent(50F).build())
                .setPriority(10)
                .build());

    }
}

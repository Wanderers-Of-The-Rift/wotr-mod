package com.wanderersoftherift.wotr.datagen;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.codec.LaxRegistryCodec;
import com.wanderersoftherift.wotr.datagen.provider.KeyForgeRecipeProvider;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.item.riftkey.EssencePredicate;
import com.wanderersoftherift.wotr.item.riftkey.KeyForgeRecipe;
import com.wanderersoftherift.wotr.world.level.levelgen.theme.RiftTheme;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class WotrRiftThemeRecipeProvider extends KeyForgeRecipeProvider<Holder<RiftTheme>> {

    public WotrRiftThemeRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super("RiftThemeRecipe", output, registries, WotrRegistries.Keys.RIFT_THEME_RECIPES,
                LaxRegistryCodec.create(WotrRegistries.Keys.RIFT_THEMES));
    }

    @Override
    public void generate(HolderLookup.Provider registries, Consumer<KeyForgeRecipe<Holder<RiftTheme>>> writer) {
        writer.accept(KeyForgeRecipe
                .create((Holder<RiftTheme>) DeferredHolder.create(WotrRegistries.Keys.RIFT_THEMES,
                        WanderersOfTheRift.id("buzzy_bees")))
                .withEssenceReq(
                        new EssencePredicate.Builder(WanderersOfTheRift.id("life")).setMinPercent(25F).build())
                .withEssenceReq(
                        new EssencePredicate.Builder(WanderersOfTheRift.id("water")).setMinPercent(5F).build())
                .withEssenceReq(
                        new EssencePredicate.Builder(WanderersOfTheRift.id("order")).setMinPercent(5F).build())
                .withEssenceReq(
                        new EssencePredicate.Builder(WanderersOfTheRift.id("plant")).setMinPercent(15F).build())
                .setPriority(10)
                .build());
        writer.accept(KeyForgeRecipe.create(
                (Holder<RiftTheme>) DeferredHolder.create(WotrRegistries.Keys.RIFT_THEMES,
                        WanderersOfTheRift.id("cave")))
                .setPriority(-1)
                .build());
        writer.accept(KeyForgeRecipe
                .create((Holder<RiftTheme>) DeferredHolder.create(WotrRegistries.Keys.RIFT_THEMES,
                        WanderersOfTheRift.id("forest")))
                .withEssenceReq(new EssencePredicate.Builder(WanderersOfTheRift.id("plant")).setMinPercent(50f).build())
                .build());
        writer.accept(KeyForgeRecipe
                .create((Holder<RiftTheme>) DeferredHolder.create(WotrRegistries.Keys.RIFT_THEMES,
                        WanderersOfTheRift.id("processor")))
                .withEssenceReq(
                        new EssencePredicate.Builder(WanderersOfTheRift.id("processor")).setMinPercent(1F).build())
                .build());
        writer.accept(KeyForgeRecipe
                .create((Holder<RiftTheme>) DeferredHolder.create(WotrRegistries.Keys.RIFT_THEMES,
                        WanderersOfTheRift.id("mushroom")))
                .withEssenceReq(
                        new EssencePredicate.Builder(WanderersOfTheRift.id("mushroom")).setMinPercent(50F).build())
                .setPriority(10)
                .build());
        writer.accept(KeyForgeRecipe
                .create((Holder<RiftTheme>) DeferredHolder.create(WotrRegistries.Keys.RIFT_THEMES,
                        WanderersOfTheRift.id("nether")))
                .withEssenceReq(
                        new EssencePredicate.Builder(WanderersOfTheRift.id("nether")).setMinPercent(50F).build())
                .setPriority(10)
                .build());
        writer.accept(KeyForgeRecipe
                .create((Holder<RiftTheme>) DeferredHolder.create(WotrRegistries.Keys.RIFT_THEMES,
                        WanderersOfTheRift.id("noir")))
                .withEssenceReq(
                        new EssencePredicate.Builder(WanderersOfTheRift.id("light")).setMinPercent(50F).build())
                .setPriority(10)
                .build());
    }
}

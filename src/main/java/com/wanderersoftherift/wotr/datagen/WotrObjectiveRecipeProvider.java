package com.wanderersoftherift.wotr.datagen;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.datagen.provider.KeyForgeRecipeProvider;
import com.wanderersoftherift.wotr.init.WotrDataComponentType;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.item.riftkey.EssencePredicate;
import com.wanderersoftherift.wotr.item.riftkey.KeyForgeRecipe;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class WotrObjectiveRecipeProvider extends KeyForgeRecipeProvider {

    public WotrObjectiveRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super("RiftObjectiveRecipe", output, registries,
                (recipe) -> ResourceLocation.parse(((Holder<?>) recipe.getOutput()).getRegisteredName()));
    }

    @Override
    public void generate(HolderLookup.Provider registries, Consumer<KeyForgeRecipe> writer) {
        writer.accept(KeyForgeRecipe
                .create(WotrDataComponentType.RIFT_OBJECTIVE.get(),
                        DeferredHolder.create(WotrRegistries.Keys.OBJECTIVES, WanderersOfTheRift.id("kill")))
                .setPriority(-1)
                .build());
        writer.accept(
                KeyForgeRecipe
                        .create(WotrDataComponentType.RIFT_OBJECTIVE.get(),
                                DeferredHolder.create(WotrRegistries.Keys.OBJECTIVES, WanderersOfTheRift.id("stealth")))
                        .withEssenceReq(
                                new EssencePredicate.Builder(WanderersOfTheRift.id("dark")).setMinPercent(5f).build())
                        .build());

    }
}

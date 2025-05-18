package com.wanderersoftherift.wotr.datagen;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.codec.LaxRegistryCodec;
import com.wanderersoftherift.wotr.datagen.provider.KeyForgeRecipeProvider;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.item.riftkey.EssencePredicate;
import com.wanderersoftherift.wotr.item.riftkey.KeyForgeRecipe;
import com.wanderersoftherift.wotr.rift.objective.ObjectiveType;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class WotrObjectiveRecipeProvider extends KeyForgeRecipeProvider<Holder<ObjectiveType>> {

    public WotrObjectiveRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super("RiftObjectiveRecipe", output, registries, WotrRegistries.Keys.RIFT_OBJECTIVE_RECIPES,
                LaxRegistryCodec.create(WotrRegistries.Keys.OBJECTIVES));
    }

    @Override
    public void generate(HolderLookup.Provider registries, Consumer<KeyForgeRecipe<Holder<ObjectiveType>>> writer) {
        writer.accept(KeyForgeRecipe.create((Holder<ObjectiveType>) DeferredHolder
                .create(WotrRegistries.Keys.OBJECTIVES, WanderersOfTheRift.id("kill"))).setPriority(-1).build());
        writer.accept(KeyForgeRecipe
                .create((Holder<ObjectiveType>) DeferredHolder.create(WotrRegistries.Keys.OBJECTIVES,
                        WanderersOfTheRift.id("stealth")))
                .withEssenceReq(new EssencePredicate.Builder(WanderersOfTheRift.id("dark")).setMinPercent(5f).build())
                .build());

    }
}

package com.wanderersoftherift.wotr.datagen;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.codec.DeferrableRegistryCodec;
import com.wanderersoftherift.wotr.datagen.provider.KeyForgeRecipeProvider;
import com.wanderersoftherift.wotr.init.RegistryEvents;
import com.wanderersoftherift.wotr.item.riftkey.EssencePredicate;
import com.wanderersoftherift.wotr.item.riftkey.KeyForgeRecipe;
import com.wanderersoftherift.wotr.rift.objective.ObjectiveType;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class ModObjectiveRecipeProvider extends KeyForgeRecipeProvider<Holder<ObjectiveType>> {

    public ModObjectiveRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super("RiftObjectiveRecipe", output, registries, RegistryEvents.RIFT_OBJECTIVE_RECIPE,
                DeferrableRegistryCodec.create(RegistryEvents.OBJECTIVE_REGISTRY));
    }

    @Override
    public void generate(HolderLookup.Provider registries, Consumer<KeyForgeRecipe<Holder<ObjectiveType>>> writer) {
        writer.accept(KeyForgeRecipe.create((Holder<ObjectiveType>) DeferredHolder
                .create(RegistryEvents.OBJECTIVE_REGISTRY, WanderersOfTheRift.id("kill"))).setPriority(-1).build());
        writer.accept(KeyForgeRecipe
                .create((Holder<ObjectiveType>) DeferredHolder.create(RegistryEvents.OBJECTIVE_REGISTRY,
                        WanderersOfTheRift.id("stealth")))
                .withEssenceReq(new EssencePredicate.Builder(WanderersOfTheRift.id("dark")).setMinPercent(5f).build())
                .build());

    }
}

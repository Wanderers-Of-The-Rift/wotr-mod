package com.wanderersoftherift.wotr.datagen.provider;

import com.wanderersoftherift.wotr.abilities.AbstractAbility;
import com.wanderersoftherift.wotr.init.RegistryEvents;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public abstract class AbilityProvider implements DataProvider {

    private final PackOutput.PathProvider pathProvider;
    private final CompletableFuture<HolderLookup.Provider> registries;

    public AbilityProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        this.pathProvider = output.createRegistryElementsPathProvider(RegistryEvents.ABILITY_REGISTRY);
        this.registries = registries;
    }

    public abstract void generate(HolderLookup.Provider registries, Consumer<AbstractAbility> writer);

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        return this.registries.thenCompose(provider -> {
            Set<ResourceLocation> set = new HashSet<>();
            List<CompletableFuture<?>> list = new ArrayList<>();
            Consumer<AbstractAbility> consumer = (ability) -> {
                if (!set.add(ability.getName())) {
                    throw new IllegalStateException("Duplicate ability " + ability.getName());
                } else {
                    Path path = this.pathProvider.json(ability.getName());
                    list.add(DataProvider.saveStable(output, provider, AbstractAbility.DIRECT_CODEC, ability, path));
                }
            };

            generate(provider, consumer);

            return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
        });
    }

    @Override
    public final String getName() {
        return "Abilities";
    }
}

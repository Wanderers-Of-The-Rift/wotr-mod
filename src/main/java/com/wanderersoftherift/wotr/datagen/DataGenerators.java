package com.wanderersoftherift.wotr.datagen;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.init.ModDamageTypes;
import com.wanderersoftherift.wotr.init.RegistryEvents;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.List;
import java.util.Set;

@EventBusSubscriber(modid = WanderersOfTheRift.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class DataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent.Client event) {
        event.createDatapackRegistryObjects(
                new RegistrySetBuilder().add(Registries.DAMAGE_TYPE, bootstrap -> {
                    bootstrap.register(
                            ModDamageTypes.FIRE_DAMAGE, new DamageType("wotr.fire", DamageScaling.NEVER, 0.0F));
                    bootstrap.register(
                            ModDamageTypes.ICE_DAMAGE, new DamageType("wotr.ice", DamageScaling.NEVER, 0.0F));
                }).add(RegistryEvents.ABILITY_REGISTRY, ModAbilityProvider::bootstrapAbilities)
        );
        event.createProvider(ModLanguageProvider::new);
        event.createProvider(ModModelProvider::new);

        event.createProvider(ModDataMapProvider::new);
        event.createProvider(ModSoundsProvider::new);

        // Tags
        ModBlockTagProvider modBlockTagProvider = event.createProvider(ModBlockTagProvider::new);
        event.createProvider((output, lookupProvider) -> new ModItemTagProvider(output, lookupProvider,
                modBlockTagProvider.contentsGetter()));
        event.createProvider(ModAbilityTagsProvider::new);

        // event.createProvider(ModAbilityProvider::new);
        event.createProvider(ModRiftThemeRecipeProvider::new);

        event.createProvider(ModRecipeProvider.Runner::new);

        event.createProvider((output, lookupProvider) -> new LootTableProvider(output, Set.of(), List.of(
                new LootTableProvider.SubProviderEntry(ModBlockLootTableProvider::new, LootContextParamSets.BLOCK),
                new LootTableProvider.SubProviderEntry(ModChestLootTableProvider::new, LootContextParamSets.CHEST),
                new LootTableProvider.SubProviderEntry(ModRiftObjectiveLootTableProvider::new,
                        LootContextParamSets.EMPTY),
                new LootTableProvider.SubProviderEntry(ModLootBoxLootTableProvider::new, LootContextParamSets.EMPTY)),
                lookupProvider));

        event.createProvider(ModObjectiveRecipeProvider::new);
    }
}

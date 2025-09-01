package com.wanderersoftherift.wotr.datagen;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.api.datagen.LanguageProviderCache;
import com.klikli_dev.modonomicon.api.datagen.NeoBookProvider;
import com.klikli_dev.modonomicon.datagen.BlockTagsProvider;
import com.klikli_dev.modonomicon.datagen.EnUsProvider;
import com.klikli_dev.modonomicon.datagen.ItemTagsProvider;
import com.klikli_dev.modonomicon.datagen.ModonomiconModelProvider;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.datagen.book.WotrBook;
import com.wanderersoftherift.wotr.init.WotrDamageTypes;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
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
                            WotrDamageTypes.FIRE_DAMAGE, new DamageType("wotr.fire", DamageScaling.NEVER, 0.0F));
                    bootstrap.register(
                            WotrDamageTypes.ICE_DAMAGE, new DamageType("wotr.ice", DamageScaling.NEVER, 0.0F));
                    bootstrap.register(
                            WotrDamageTypes.THORNS_DAMAGE, new DamageType("wort:thorns", DamageScaling.NEVER, 0.0F));
                })
                        .add(WotrRegistries.Keys.ABILITIES, WotrAbilityProvider::bootstrapAbilities)
                        .add(WotrRegistries.Keys.MODIFIERS, WotrModifierProvider::bootstrapModifiers)
                        .add(WotrRegistries.Keys.OBJECTIVES, context -> {
                        })
                        .add(WotrRegistries.Keys.RIFT_THEMES, context -> {
                        })
                        .add(WotrRegistries.Keys.RUNEGEM_DATA, WotrRuneGemDataProvider::bootstrapRuneGems)

        );
        event.createProvider(WotrModelProvider::new);

        event.createProvider(WotrDataMapProvider::new);
        event.createProvider(WotrSoundsProvider::new);

        // Tags
        event.createBlockAndItemTags(WotrBlockTagProvider::new, WotrItemTagProvider::new);
        event.createProvider(WotrAbilityTagsProvider::new);
        event.createProvider(WotrRiftThemeTagsProvider::new);
        event.createProvider(WotrObjectiveTagsProvider::new);

        event.createProvider(WotrRecipeProvider.Runner::new);

        event.createProvider((output, lookupProvider) -> new LootTableProvider(output, Set.of(), List.of(
                new LootTableProvider.SubProviderEntry(WotrBlockLootTableProvider::new, LootContextParamSets.BLOCK),
                new LootTableProvider.SubProviderEntry(WotrChestLootTableProvider::new, LootContextParamSets.CHEST),
                new LootTableProvider.SubProviderEntry(WotrRiftObjectiveLootTableProvider::new,
                        LootContextParamSets.EMPTY),
                new LootTableProvider.SubProviderEntry(WotrLootBoxLootTableProvider::new, LootContextParamSets.EMPTY)),
                lookupProvider));

        event.createProvider(WotrRunegemDataTagsProvider::new);

        event.createProvider(WotrLanguageProvider::new);
        var enUsCache = new LanguageProviderCache("en_us");
        DataGenerator generator = event.getGenerator();
        generator.addProvider(true, NeoBookProvider.of(event,
                        new WotrBook(Modonomicon.MOD_ID, enUsCache))
        );
        generator.addProvider(true, new EnUsProvider(generator.getPackOutput(), enUsCache));
        generator.addProvider(true, new ModonomiconModelProvider(generator.getPackOutput()));

        var blockTagsProvider = new BlockTagsProvider(generator.getPackOutput(), event.getLookupProvider());
        generator.addProvider(true,blockTagsProvider);
        generator.addProvider(true, new ItemTagsProvider(generator.getPackOutput(), event.getLookupProvider(), blockTagsProvider.contentsGetter()));
    }
}

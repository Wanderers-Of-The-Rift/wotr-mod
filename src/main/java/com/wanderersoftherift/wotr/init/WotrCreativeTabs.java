package com.wanderersoftherift.wotr.init;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.abilities.Ability;
import com.wanderersoftherift.wotr.core.guild.currency.Currency;
import com.wanderersoftherift.wotr.item.ability.ActivatableAbility;
import com.wanderersoftherift.wotr.item.currency.CurrencyProvider;
import com.wanderersoftherift.wotr.item.runegem.RunegemData;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class WotrCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister
            .create(Registries.CREATIVE_MODE_TAB, WanderersOfTheRift.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> WOTR_TAB = CREATIVE_MODE_TABS.register(
            WanderersOfTheRift.MODID,
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup." + WanderersOfTheRift.MODID))
                    .withTabsBefore(CreativeModeTabs.COMBAT)
                    .icon(WotrBlocks.KEY_FORGE::toStack)
                    .displayItems((parameters, output) -> {
                        output.accept(WotrItems.RIFT_KEY);
                        output.accept(WotrItems.SKILL_THREAD);
                        output.accept(WotrItems.EARTH_ESSENCE);
                        output.accept(WotrItems.WATER_ESSENCE);
                        output.accept(WotrItems.PLANT_ESSENCE);
                        output.accept(WotrItems.DEATH_ESSENCE);
                        output.accept(WotrItems.LIFE_ESSENCE);
                        output.accept(WotrItems.NETHER_ESSENCE);
                        output.accept(WotrItems.LIGHT_ESSENCE);
                        output.accept(WotrItems.HONEY_ESSENCE);
                        output.accept(WotrItems.MUSHROOM_ESSENCE);
                        output.accept(WotrItems.FABRIC_ESSENCE);
                        output.accept(WotrItems.DARK_ESSENCE);
                        output.accept(WotrItems.FIRE_ESSENCE);
                        output.accept(WotrItems.AIR_ESSENCE);
                        output.accept(WotrItems.ENERGY_ESSENCE);
                        output.accept(WotrItems.ANIMAL_ESSENCE);
                        output.accept(WotrItems.CRYSTAL_ESSENCE);
                        output.accept(WotrItems.METAL_ESSENCE);
                        output.accept(WotrItems.FOOD_ESSENCE);
                        output.accept(WotrItems.SLIME_ESSENCE);
                        output.accept(WotrItems.MIND_ESSENCE);
                        output.accept(WotrItems.MECHA_ESSENCE);
                        output.accept(WotrItems.END_ESSENCE);
                        output.accept(WotrItems.FLOW_ESSENCE);
                        output.accept(WotrItems.FORM_ESSENCE);
                        output.accept(WotrItems.ORDER_ESSENCE);
                        output.accept(WotrItems.CHAOS_ESSENCE);


                        WotrItems.BLOCK_ITEMS.stream()
                                .filter(x -> x.get().getBlock() != WotrBlocks.NPC.get())
                                .forEach(item -> output.accept(item.get()));
                        parameters.holders().lookup(WotrRegistries.Keys.CURRENCIES).ifPresent((currencies) -> {
                            generateCurrencyBags(output, currencies);
                        });
                    })
                    .build());

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> WOTR_NPC_TAB = CREATIVE_MODE_TABS.register(
            WanderersOfTheRift.MODID + "npc",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup." + WanderersOfTheRift.MODID + ".npc"))
                    .withTabsBefore(WOTR_TAB.getId())
                    .icon(WotrBlocks.NPC::toStack)
                    .displayItems((parameters, output) -> {
                        parameters.holders().lookupOrThrow(WotrRegistries.Keys.NPCS).listElements().forEach(npc -> {
                            ItemStack item = new ItemStack(WotrBlocks.NPC.asItem());
                            item.set(WotrDataComponentType.NPC_IDENTITY, npc);
                            output.accept(item);
                        });
                    })
                    .build());

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> WOTR_ABILITY_TAB = CREATIVE_MODE_TABS.register(
            WanderersOfTheRift.MODID + "_ability",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup." + WanderersOfTheRift.MODID + ".ability"))
                    .withTabsBefore(WOTR_TAB.getId())
                    .icon(WotrItems.ABILITY_HOLDER::toStack)
                    .displayItems((parameters, output) -> {
                        output.accept(WotrItems.ABILITY_HOLDER);
                        parameters.holders().lookup(WotrRegistries.Keys.ABILITIES).ifPresent((abilities) -> {
                            generateAbilityItems(output, abilities);
                        });
                    })
                    .build());

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> WOTR_RUNEGEM_TAB = CREATIVE_MODE_TABS.register(
            WanderersOfTheRift.MODID + "_runegem",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup." + WanderersOfTheRift.MODID + ".runegem"))
                    .withTabsBefore(WOTR_ABILITY_TAB.getId())
                    .icon(WotrItems.CUT_RUNEGEM_GEODE::toStack)
                    .displayItems((parameters, output) -> {
                        output.accept(WotrItems.RAW_RUNEGEM_GEODE);
                        output.accept(WotrItems.SHAPED_RUNEGEM_GEODE);
                        output.accept(WotrItems.CUT_RUNEGEM_GEODE);
                        output.accept(WotrItems.POLISHED_RUNEGEM_GEODE);
                        output.accept(WotrItems.FRAMED_RUNEGEM_GEODE);
                        output.accept(WotrItems.RAW_RUNEGEM_MONSTER);
                        output.accept(WotrItems.SHAPED_RUNEGEM_MONSTER);
                        output.accept(WotrItems.CUT_RUNEGEM_MONSTER);
                        output.accept(WotrItems.POLISHED_RUNEGEM_MONSTER);
                        output.accept(WotrItems.FRAMED_RUNEGEM_MONSTER);
                        parameters.holders().lookup(WotrRegistries.Keys.RUNEGEM_DATA).ifPresent((runegems) -> {
                            generateRunegems(output, runegems);
                        });
                    })
                    .build());

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> WOTR_DEV_TAB = CREATIVE_MODE_TABS.register(
            WanderersOfTheRift.MODID + "_dev",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup." + WanderersOfTheRift.MODID + ".dev"))
                    .withTabsBefore(WOTR_RUNEGEM_TAB.getId())
                    .icon(() -> WotrBlocks.PROCESSOR_BLOCK_3.getBlock().get().asItem().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        output.accept(WotrItems.BUILDER_GLASSES);
                        output.accept(WotrItems.NOIR_HELMET);
                        output.accept(WotrItems.COLOR_HELMET);
                        WotrItems.DEV_BLOCK_ITEMS.forEach(item -> output.accept(item.get()));
                    })
                    .build());

    private static void generateAbilityItems(
            CreativeModeTab.Output output,
            HolderLookup.RegistryLookup<Ability> registry) {
        registry.listElements().filter(x -> x.value().isInCreativeMenu()).forEach(abilityHolder -> {
            ItemStack item = WotrItems.ABILITY_HOLDER.toStack();
            item.set(WotrDataComponentType.ABILITY, new ActivatableAbility(abilityHolder));
            output.accept(item);
        });
    }

    private static void generateCurrencyBags(
            CreativeModeTab.Output output,
            HolderLookup.RegistryLookup<Currency> currencies) {
        currencies.listElements().forEach(currency -> {
            ItemStack item = WotrItems.CURRENCY_BAG.toStack();
            ResourceLocation id = currency.getKey().location();
            item.set(WotrDataComponentType.CURRENCY_PROVIDER, new CurrencyProvider(currency, 100));
            item.set(DataComponents.ITEM_NAME,
                    Component.translatable(WanderersOfTheRift.translationId("currency", id)));
            output.accept(item);
        });
    }

    private static void generateRunegems(
            CreativeModeTab.Output output,
            HolderLookup.RegistryLookup<RunegemData> registry) {
        registry.listElements().forEach(runegemHolder -> {
            ItemStack item = WotrItems.RUNEGEM.toStack();
            item.set(WotrDataComponentType.RUNEGEM_DATA, runegemHolder.value());
            output.accept(item);
        });
    }

}

package com.wanderersoftherift.wotr.init;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.abilities.AbstractAbility;
import com.wanderersoftherift.wotr.item.runegem.RunegemData;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
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
                        WotrItems.BLOCK_ITEMS.forEach(item -> output.accept(item.get()));
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
                        WotrItems.DEV_BLOCK_ITEMS.forEach(item -> output.accept(item.get()));
                    })
                    .build());

    private static void generateAbilityItems(
            CreativeModeTab.Output output,
            HolderLookup.RegistryLookup<AbstractAbility> registry) {
        registry.listElements().forEach(abilityHolder -> {
            ItemStack item = WotrItems.ABILITY_HOLDER.toStack();
            item.set(WotrDataComponentType.ABILITY, abilityHolder);
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

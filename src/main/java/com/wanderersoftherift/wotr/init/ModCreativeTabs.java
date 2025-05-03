package com.wanderersoftherift.wotr.init;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister
            .create(Registries.CREATIVE_MODE_TAB, WanderersOfTheRift.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> WOTR_TAB = CREATIVE_MODE_TABS.register(
            WanderersOfTheRift.MODID,
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup." + WanderersOfTheRift.MODID))
                    .withTabsBefore(CreativeModeTabs.COMBAT)
                    .icon(ModBlocks.ABILITY_BENCH::toStack)
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.RIFT_KEY);
                        output.accept(ModItems.ABILITY_HOLDER);
                        output.accept(ModItems.SKILL_THREAD);
                        output.accept(ModItems.RAW_RUNEGEM_GEODE);
                        output.accept(ModItems.SHAPED_RUNEGEM_GEODE);
                        output.accept(ModItems.CUT_RUNEGEM_GEODE);
                        output.accept(ModItems.POLISHED_RUNEGEM_GEODE);
                        output.accept(ModItems.FRAMED_RUNEGEM_GEODE);
                        ModItems.BLOCK_ITEMS.forEach(item -> output.accept(item.get()));
                    })
                    .build());

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> WOTR_DEV_TAB = CREATIVE_MODE_TABS.register(
            WanderersOfTheRift.MODID + "_dev",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup." + WanderersOfTheRift.MODID + ".dev"))
                    .withTabsBefore(CreativeModeTabs.COMBAT)
                    .icon(() -> ModBlocks.PROCESSOR_BLOCK_3.getBlock().get().asItem().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.BUILDER_GLASSES);
                        ModItems.DEV_BLOCK_ITEMS.forEach(item -> output.accept(item.get()));
                    })
                    .build());
}

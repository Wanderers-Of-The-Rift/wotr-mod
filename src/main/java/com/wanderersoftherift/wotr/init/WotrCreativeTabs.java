package com.wanderersoftherift.wotr.init;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
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
                    .icon(WotrBlocks.ABILITY_BENCH::toStack)
                    .displayItems((parameters, output) -> {
                        output.accept(WotrItems.RIFT_KEY);
                        output.accept(WotrItems.ABILITY_HOLDER);
                        output.accept(WotrItems.SKILL_THREAD);
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
                        WotrItems.BLOCK_ITEMS.forEach(item -> output.accept(item.get()));
                    })
                    .build());

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> WOTR_DEV_TAB = CREATIVE_MODE_TABS.register(
            WanderersOfTheRift.MODID + "_dev",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup." + WanderersOfTheRift.MODID + ".dev"))
                    .withTabsBefore(CreativeModeTabs.COMBAT)
                    .icon(() -> WotrBlocks.PROCESSOR_BLOCK_3.getBlock().get().asItem().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        output.accept(WotrItems.BUILDER_GLASSES);
                        WotrItems.DEV_BLOCK_ITEMS.forEach(item -> output.accept(item.get()));
                    })
                    .build());
}

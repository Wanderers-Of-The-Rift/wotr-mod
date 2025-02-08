package com.dimensiondelvers.dimensiondelvers.init;

import com.dimensiondelvers.dimensiondelvers.DimensionDelvers;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, DimensionDelvers.MODID);


    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> DIMENSION_DELVERS_TAB =
            CREATIVE_MODE_TABS.register("dimensiondelvers",
                    () -> CreativeModeTab.builder()
                            .title(Component.translatable("itemGroup.dimensiondelvers"))
                            .withTabsBefore(CreativeModeTabs.COMBAT).icon(() -> ModItems.EXAMPLE_ITEM.get().getDefaultInstance())
                            .displayItems((parameters, output) -> {
                                output.accept(ModItems.EXAMPLE_BLOCK_ITEM.get());
                                output.accept(ModItems.EXAMPLE_ITEM);
                                output.accept(ModItems.DEV_BLOCK_ITEM);
                                output.accept(ModItems.RUNE_ANVIL_BLOCK_ITEM);

                                output.accept(ModItems.RUNEGEM);
                            }).build());

}

package com.wanderersoftherift.wotr.init;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, WanderersOfTheRift.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> WOTR_TAB =
            CREATIVE_MODE_TABS.register(WanderersOfTheRift.MODID,
                    () -> CreativeModeTab.builder()
                            .title(Component.translatable("itemGroup." + WanderersOfTheRift.MODID))
                            .withTabsBefore(CreativeModeTabs.COMBAT).icon(() -> ModItems.EXAMPLE_ITEM.get().getDefaultInstance())
                            .displayItems((parameters, output) -> {
                                output.accept(ModItems.EXAMPLE_ITEM);
                                output.accept(ModItems.RIFT_KEY);
                                ModItems.BLOCK_ITEMS.forEach(item -> output.accept(item.get()));

                                output.accept(ModItems.BUILDER_GLASSES);

                                output.accept(ModItems.RUNEGEM_GEODE);
                            }).build());

}

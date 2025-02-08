package com.dimensiondelvers.dimensiondelvers.init;

import com.dimensiondelvers.dimensiondelvers.DimensionDelvers;
import com.dimensiondelvers.dimensiondelvers.item.runegem.RuneGemShape;
import com.dimensiondelvers.dimensiondelvers.item.runegem.RuneGemTier;
import com.dimensiondelvers.dimensiondelvers.item.runegem.RunegemData;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
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
                                output.accept(ModItems.EXAMPLE_ITEM);
                                ModItems.BLOCK_ITEMS.forEach(item -> output.accept(item.get()));

                                ItemStack runegemCircle = ModItems.RUNEGEM.toStack();
                                runegemCircle.set(ModDataComponentType.RUNEGEM_DATA.get(), new RunegemData(RuneGemShape.CIRCLE, null, RuneGemTier.RAW));
                                output.accept(runegemCircle);

                                ItemStack runegemSquare = ModItems.RUNEGEM.toStack();
                                runegemSquare.set(ModDataComponentType.RUNEGEM_DATA.get(), new RunegemData(RuneGemShape.SQUARE, null, RuneGemTier.RAW));
                                output.accept(runegemSquare);

                                ItemStack runegemTriangle = ModItems.RUNEGEM.toStack();
                                runegemTriangle.set(ModDataComponentType.RUNEGEM_DATA.get(), new RunegemData(RuneGemShape.TRIANGLE, null, RuneGemTier.RAW));
                                output.accept(runegemTriangle);

                                ItemStack runegemDiamond = ModItems.RUNEGEM.toStack();
                                runegemDiamond.set(ModDataComponentType.RUNEGEM_DATA.get(), new RunegemData(RuneGemShape.DIAMOND, null, RuneGemTier.RAW));
                                output.accept(runegemDiamond);

                                ItemStack runegemHeart = ModItems.RUNEGEM.toStack();
                                runegemHeart.set(ModDataComponentType.RUNEGEM_DATA.get(), new RunegemData(RuneGemShape.HEART, null, RuneGemTier.RAW));
                                output.accept(runegemHeart);

                                ItemStack runegemPentagon = ModItems.RUNEGEM.toStack();
                                runegemPentagon.set(ModDataComponentType.RUNEGEM_DATA.get(), new RunegemData(RuneGemShape.PENTAGON, null, RuneGemTier.RAW));
                                output.accept(runegemPentagon);
                            }).build());

}

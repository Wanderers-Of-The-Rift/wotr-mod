package com.dimensiondelvers.dimensiondelvers.init;

import com.dimensiondelvers.dimensiondelvers.DimensionDelvers;
import com.dimensiondelvers.dimensiondelvers.item.runegem.RunegemData;
import com.dimensiondelvers.dimensiondelvers.item.runegem.RunegemShape;
import com.dimensiondelvers.dimensiondelvers.item.runegem.RunegemTier;
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
                                output.accept(ModItems.EXAMPLE_BLOCK_ITEM.get());
                                output.accept(ModItems.EXAMPLE_ITEM);
                                output.accept(ModItems.DEV_BLOCK_ITEM);
                                output.accept(ModItems.RUNE_ANVIL_BLOCK_ITEM);

                                ItemStack circleGem = ModItems.RUNEGEM.toStack();
                                circleGem.set(ModDataComponentType.RUNEGEM_DATA, new RunegemData(RunegemShape.CIRCLE, DimensionDelvers.tagId(ModModifiers.MODIFIER_KEY, "raw_attack_rune"), RunegemTier.RAW));
                                output.accept(circleGem);

                                ItemStack squareGem = ModItems.RUNEGEM.toStack();
                                squareGem.set(ModDataComponentType.RUNEGEM_DATA, new RunegemData(RunegemShape.SQUARE, DimensionDelvers.tagId(ModModifiers.MODIFIER_KEY, "cut_health_rune"), RunegemTier.CUT));
                                output.accept(squareGem);

                                ItemStack triangleGem = ModItems.RUNEGEM.toStack();
                                triangleGem.set(ModDataComponentType.RUNEGEM_DATA, new RunegemData(RunegemShape.TRIANGLE, DimensionDelvers.tagId(ModModifiers.MODIFIER_KEY, "raw_defense_rune"), RunegemTier.RAW));
                                output.accept(triangleGem);

                                ItemStack diamondGem = ModItems.RUNEGEM.toStack();
                                diamondGem.set(ModDataComponentType.RUNEGEM_DATA, new RunegemData(RunegemShape.DIAMOND, DimensionDelvers.tagId(ModModifiers.MODIFIER_KEY, "shaped_defense_rune"), RunegemTier.SHAPED));
                                output.accept(diamondGem);

                                ItemStack heartGem = ModItems.RUNEGEM.toStack();
                                heartGem.set(ModDataComponentType.RUNEGEM_DATA, new RunegemData(RunegemShape.HEART, DimensionDelvers.tagId(ModModifiers.MODIFIER_KEY, "cut_health_rune"), RunegemTier.CUT));
                                output.accept(heartGem);

                                ItemStack pentagonGem = ModItems.RUNEGEM.toStack();
                                pentagonGem.set(ModDataComponentType.RUNEGEM_DATA, new RunegemData(RunegemShape.PENTAGON, DimensionDelvers.tagId(ModModifiers.MODIFIER_KEY, "raw_attack_rune"), RunegemTier.RAW));
                                output.accept(pentagonGem);
                            }).build());

}

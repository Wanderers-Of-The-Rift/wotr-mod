package com.wanderersoftherift.wotr.init;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.item.runegem.RunegemData;
import com.wanderersoftherift.wotr.item.runegem.RunegemShape;
import com.wanderersoftherift.wotr.item.runegem.RunegemTier;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
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
                                ModItems.BLOCK_ITEMS.forEach(item -> output.accept(item.get()));

                                ItemStack circleGem = ModItems.RUNEGEM.toStack();
                                circleGem.set(ModDataComponentType.RUNEGEM_DATA, new RunegemData(RunegemShape.CIRCLE, WanderersOfTheRift.tagId(ModDatapackRegistries.MODIFIER_KEY, "raw_attack_rune"), RunegemTier.RAW));
                                output.accept(circleGem);

                                ItemStack squareGem = ModItems.RUNEGEM.toStack();
                                squareGem.set(ModDataComponentType.RUNEGEM_DATA, new RunegemData(RunegemShape.SQUARE, WanderersOfTheRift.tagId(ModDatapackRegistries.MODIFIER_KEY, "cut_health_rune"), RunegemTier.CUT));
                                output.accept(squareGem);

                                ItemStack triangleGem = ModItems.RUNEGEM.toStack();
                                triangleGem.set(ModDataComponentType.RUNEGEM_DATA, new RunegemData(RunegemShape.TRIANGLE, WanderersOfTheRift.tagId(ModDatapackRegistries.MODIFIER_KEY, "raw_defense_rune"), RunegemTier.RAW));
                                output.accept(triangleGem);

                                ItemStack diamondGem = ModItems.RUNEGEM.toStack();
                                diamondGem.set(ModDataComponentType.RUNEGEM_DATA, new RunegemData(RunegemShape.DIAMOND, WanderersOfTheRift.tagId(ModDatapackRegistries.MODIFIER_KEY, "shaped_defense_rune"), RunegemTier.SHAPED));
                                output.accept(diamondGem);

                                ItemStack heartGem = ModItems.RUNEGEM.toStack();
                                heartGem.set(ModDataComponentType.RUNEGEM_DATA, new RunegemData(RunegemShape.HEART, WanderersOfTheRift.tagId(ModDatapackRegistries.MODIFIER_KEY, "cut_health_rune"), RunegemTier.CUT));
                                output.accept(heartGem);

                                ItemStack pentagonGem = ModItems.RUNEGEM.toStack();
                                pentagonGem.set(ModDataComponentType.RUNEGEM_DATA, new RunegemData(RunegemShape.PENTAGON, WanderersOfTheRift.tagId(ModDatapackRegistries.MODIFIER_KEY, "raw_attack_rune"), RunegemTier.RAW));
                                output.accept(pentagonGem);
                            }).build());

}

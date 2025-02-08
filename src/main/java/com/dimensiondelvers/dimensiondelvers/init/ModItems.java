package com.dimensiondelvers.dimensiondelvers.init;

import com.dimensiondelvers.dimensiondelvers.DimensionDelvers;
import com.dimensiondelvers.dimensiondelvers.item.runegem.RuneGemShape;
import com.dimensiondelvers.dimensiondelvers.item.runegem.RuneGemTier;
import com.dimensiondelvers.dimensiondelvers.item.runegem.Runegem;
import com.dimensiondelvers.dimensiondelvers.item.runegem.RunegemData;
import net.minecraft.core.registries.Registries;
import com.dimensiondelvers.dimensiondelvers.item.abilities.AbilityBagItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.dimensiondelvers.dimensiondelvers.DimensionDelvers.tagId;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(DimensionDelvers.MODID);


    public static final DeferredItem<BlockItem> EXAMPLE_BLOCK_ITEM = ITEMS.registerSimpleBlockItem(
            "example_block",
            ModBlocks.EXAMPLE_BLOCK
    );

    public static final DeferredItem<BlockItem> DEV_BLOCK_ITEM = ITEMS.registerSimpleBlockItem(
            "dev_block",
            ModBlocks.DEV_BLOCK
    );

    public static final DeferredItem<BlockItem> RUNE_ANVIL_BLOCK_ITEM = ITEMS.registerSimpleBlockItem(
            "rune_anvil",
            ModBlocks.RUNE_ANVIL_BLOCK
    );

    public static final DeferredItem<Item> EXAMPLE_ITEM = ITEMS.registerSimpleItem(
            "example_item",
            new Item.Properties().food(new FoodProperties.Builder()
                    .alwaysEdible()
                    .nutrition(1)
                    .saturationModifier(2f)
                    .build()
            )
    );

    //Runegems
    public static final DeferredItem<Item> RUNEGEM = ITEMS.register("runegem",
            registryName -> new Runegem(new Item.Properties()
                    .component(ModDataComponentType.RUNEGEM_DATA,
                            new RunegemData(RuneGemShape.CIRCLE, tagId(Registries.ENCHANTMENT, "raw_fire_rune"), RuneGemTier.RAW)))
            );

    public static final DeferredItem<Item> ABILITY_BAG = ITEMS.register("ability_bag", registryName ->
            new AbilityBagItem(new Item.Properties().component(DataComponents.CONTAINER, ItemContainerContents.EMPTY)));


}

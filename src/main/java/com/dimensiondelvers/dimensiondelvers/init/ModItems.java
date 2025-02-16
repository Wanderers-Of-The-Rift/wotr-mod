package com.dimensiondelvers.dimensiondelvers.init;

import com.dimensiondelvers.dimensiondelvers.DimensionDelvers;
import com.dimensiondelvers.dimensiondelvers.item.runegem.Runegem;
import com.dimensiondelvers.dimensiondelvers.item.runegem.RunegemData;
import com.dimensiondelvers.dimensiondelvers.item.runegem.RunegemShape;
import com.dimensiondelvers.dimensiondelvers.item.runegem.RunegemTier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
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
                    .setId(ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(DimensionDelvers.MODID, "runegem")))
                    .component(ModDataComponentType.RUNEGEM_DATA,
                            new RunegemData(RunegemShape.CIRCLE, tagId(ModModifiers.MODIFIER_KEY, "raw_attack_rune"), RunegemTier.RAW)))
    );

}

package com.wanderersoftherift.wotr.init;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.item.BuilderGlasses;
import com.wanderersoftherift.wotr.item.runegem.RuneGemShape;
import com.wanderersoftherift.wotr.item.runegem.RuneGemTier;
import com.wanderersoftherift.wotr.item.runegem.Runegem;
import com.wanderersoftherift.wotr.item.runegem.RunegemData;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;

import static com.wanderersoftherift.wotr.WanderersOfTheRift.tagId;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(WanderersOfTheRift.MODID);
    public static final List<DeferredItem<BlockItem>> BLOCK_ITEMS = new ArrayList<>();

    public static final DeferredItem<Item> EXAMPLE_ITEM = ITEMS.registerSimpleItem(
            "example_item",
            new Item.Properties().food(new FoodProperties.Builder()
                    .alwaysEdible()
                    .nutrition(1)
                    .saturationModifier(2f)
                    .build()
            )
    );

    public static final DeferredItem<BuilderGlasses> BUILDER_GLASSES = ITEMS.register(
            "builder_glasses",
            BuilderGlasses::new
    );

    //Runegems
    public static final DeferredItem<Item> RUNEGEM = ITEMS.register("runegem",
            registryName -> new Runegem(new Item.Properties()
                    .setId(ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(WanderersOfTheRift.MODID, "runegem")))
                    .component(ModDataComponentType.RUNEGEM_DATA,
                            new RunegemData(RuneGemShape.CIRCLE, tagId(ModModifiers.MODIFIER_KEY, "raw_fire_rune"), RuneGemTier.RAW)))
    );

    public static <T extends Block> DeferredItem<BlockItem> registerSimpleBlockItem(String id, DeferredBlock<T> block){
        DeferredItem<BlockItem> simpleBlockItem = ITEMS.registerSimpleBlockItem(id, block);
        BLOCK_ITEMS.add(simpleBlockItem);
        return simpleBlockItem;
    }

}

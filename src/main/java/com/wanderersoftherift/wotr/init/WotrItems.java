package com.wanderersoftherift.wotr.init;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.item.AbilityHolder;
import com.wanderersoftherift.wotr.item.BuilderGlasses;
import com.wanderersoftherift.wotr.item.LootBox;
import com.wanderersoftherift.wotr.item.SkillThread;
import com.wanderersoftherift.wotr.item.riftkey.RiftKey;
import com.wanderersoftherift.wotr.item.runegem.Runegem;
import com.wanderersoftherift.wotr.item.runegem.RunegemData;
import com.wanderersoftherift.wotr.item.runegem.RunegemShape;
import com.wanderersoftherift.wotr.item.runegem.RunegemTier;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class WotrItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(WanderersOfTheRift.MODID);
    public static final List<DeferredItem<BlockItem>> BLOCK_ITEMS = new ArrayList<>();
    public static final List<DeferredItem<BlockItem>> DEV_BLOCK_ITEMS = new ArrayList<>();

    public static final DeferredItem<BuilderGlasses> BUILDER_GLASSES = ITEMS.register("builder_glasses",
            BuilderGlasses::new);

    // Runegems
    public static final DeferredItem<Item> RUNEGEM = ITEMS.register("runegem",
            registryName -> new Runegem(new Item.Properties()
                    .setId(ResourceKey.create(Registries.ITEM,
                            ResourceLocation.fromNamespaceAndPath(WanderersOfTheRift.MODID, "runegem")))
                    .component(WotrDataComponentType.RUNEGEM_DATA, new RunegemData(Component.empty(),
                            RunegemShape.CIRCLE, new ArrayList<>(), RunegemTier.RAW))));

    public static final DeferredItem<Item> RIFT_KEY = ITEMS
            .register("rift_key",
                    registryName -> new RiftKey(new Item.Properties()
                            .setId(ResourceKey.create(Registries.ITEM, WanderersOfTheRift.id("rift_key")))
                            .stacksTo(1)));

    public static final DeferredItem<Item> BASE_CURRENCY_BAG = ITEMS.register("base_currency_bag",
            registryName -> new Item(new Item.Properties()
                    .setId(ResourceKey.create(Registries.ITEM, WanderersOfTheRift.id("base_currency_bag")))));

    public static final DeferredItem<Item> CURRENCY_BAG = ITEMS.register("currency_bag",
            registryName -> new Item(new Item.Properties()
                    .setId(ResourceKey.create(Registries.ITEM, WanderersOfTheRift.id("currency_bag")))
                    .component(DataComponents.CONSUMABLE,
                            Consumable.builder()
                                    .consumeSeconds(0.1F)
                                    .animation(ItemUseAnimation.DRINK)
                                    .sound(SoundEvents.GENERIC_DRINK)
                                    .hasConsumeParticles(false)
                                    .build())));

    public static final DeferredItem<Item> RAW_RUNEGEM_GEODE = registerLootBox(
            RunegemTier.RAW.getName() + "_runegem_geode");
    public static final DeferredItem<Item> SHAPED_RUNEGEM_GEODE = registerLootBox(
            RunegemTier.SHAPED.getName() + "_runegem_geode");
    public static final DeferredItem<Item> CUT_RUNEGEM_GEODE = registerLootBox(
            RunegemTier.CUT.getName() + "_runegem_geode");
    public static final DeferredItem<Item> POLISHED_RUNEGEM_GEODE = registerLootBox(
            RunegemTier.POLISHED.getName() + "_runegem_geode");
    public static final DeferredItem<Item> FRAMED_RUNEGEM_GEODE = registerLootBox(
            RunegemTier.FRAMED.getName() + "_runegem_geode");
    public static final DeferredItem<Item> RAW_RUNEGEM_MONSTER = registerLootBox(
            RunegemTier.RAW.getName() + "_runegem_monster");
    public static final DeferredItem<Item> SHAPED_RUNEGEM_MONSTER = registerLootBox(
            RunegemTier.SHAPED.getName() + "_runegem_monster");
    public static final DeferredItem<Item> CUT_RUNEGEM_MONSTER = registerLootBox(
            RunegemTier.CUT.getName() + "_runegem_monster");
    public static final DeferredItem<Item> POLISHED_RUNEGEM_MONSTER = registerLootBox(
            RunegemTier.POLISHED.getName() + "_runegem_monster");
    public static final DeferredItem<Item> FRAMED_RUNEGEM_MONSTER = registerLootBox(
            RunegemTier.FRAMED.getName() + "_runegem_monster");

    // Abilities
    public static final DeferredItem<Item> BASE_ABILITY_HOLDER = ITEMS.register("base_ability_holder",
            registryName -> new Item(new Item.Properties()
                    .setId(ResourceKey.create(Registries.ITEM, WanderersOfTheRift.id("base_ability_holder")))));
    public static final DeferredItem<Item> ABILITY_HOLDER = ITEMS.register("ability_holder",
            registryName -> new AbilityHolder(new Item.Properties().stacksTo(1)
                    .setId(ResourceKey.create(Registries.ITEM, WanderersOfTheRift.id("ability_holder")))));
    public static final DeferredItem<Item> SKILL_THREAD = ITEMS.register("skill_thread",
            registryName -> new SkillThread(new Item.Properties().stacksTo(64)
                    .setId(ResourceKey.create(Registries.ITEM, WanderersOfTheRift.id("skill_thread")))));

    private static @NotNull DeferredItem<Item> registerLootBox(String idString) {
        return ITEMS.register(idString, registryName -> new Item(new Item.Properties()
                .setId(ResourceKey.create(Registries.ITEM, WanderersOfTheRift.id(idString)))
                .component(DataComponents.CONSUMABLE,
                        Consumable.builder()
                                .consumeSeconds(0.1F)
                                .animation(ItemUseAnimation.DRINK)
                                .sound(SoundEvents.GENERIC_DRINK)
                                .hasConsumeParticles(false)
                                .build())
                .component(WotrDataComponentType.LOOT_BOX, new LootBox(
                        ResourceKey.create(Registries.LOOT_TABLE, WanderersOfTheRift.id("loot_box/" + idString))))));
    }

    public static <T extends Block> DeferredItem<BlockItem> registerSimpleBlockItem(String id, DeferredBlock<T> block) {
        DeferredItem<BlockItem> simpleBlockItem = ITEMS.registerSimpleBlockItem(id, block);
        BLOCK_ITEMS.add(simpleBlockItem);
        return simpleBlockItem;
    }

    public static <T extends Block> DeferredItem<BlockItem> registerSimpleDevBlockItem(
            String id,
            DeferredBlock<T> block) {
        DeferredItem<BlockItem> simpleBlockItem = ITEMS.registerSimpleBlockItem(id, block);
        DEV_BLOCK_ITEMS.add(simpleBlockItem);
        return simpleBlockItem;
    }

}

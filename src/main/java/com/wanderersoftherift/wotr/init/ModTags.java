package com.wanderersoftherift.wotr.init;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.abilities.AbstractAbility;
import com.wanderersoftherift.wotr.item.runegem.RunegemData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ModTags {

    public static class Blocks {

        private static TagKey<Block> createTag(String name) {
            return BlockTags.create(WanderersOfTheRift.id(name));
        }
    }

    public static class Items {
        public static final TagKey<Item> UNBREAKABLE_EXCLUSIONS = createTag("unbreakable_exclusions");
        public static final TagKey<Item> SOCKETABLE = createTag("socketable");
        public static final TagKey<Item> SOCKETABLE_HELMET_SLOT = createTag("socketable_helmet_slot");
        public static final TagKey<Item> SOCKETABLE_CHESTPLATE_SLOT = createTag("socketable_chestplate_slot");
        public static final TagKey<Item> SOCKETABLE_LEGGINGS_SLOT = createTag("socketable_leggings_slot");
        public static final TagKey<Item> SOCKETABLE_BOOTS_SLOT = createTag("socketable_boots_slot");
        public static final TagKey<Item> SOCKETABLE_MAIN_HAND_SLOT = createTag("socketable_main_hand_slot");
        public static final TagKey<Item> SOCKETABLE_OFF_HAND_SLOT = createTag("socketable_off_hand_slot");

        public static final TagKey<Item> ROGUE_TYPE_GEAR = createTag("gear_type.rogue_type_gear");
        public static final TagKey<Item> TANK_TYPE_GEAR = createTag("gear_type.tank_type_gear");
        public static final TagKey<Item> BARBARIAN_TYPE_GEAR = createTag("gear_type.barbarian_type_gear");
        public static final TagKey<Item> WIZARD_TYPE_GEAR = createTag("gear_type.wizard_type_gear");

        public static final TagKey<Item> ROGUE_TYPE_WEAPON = createTag("weapon_type.rogue_type_weapon");
        public static final TagKey<Item> TANK_TYPE_WEAPON = createTag("weapon_type.tank_type_weapon");
        public static final TagKey<Item> BARBARIAN_TYPE_WEAPON = createTag("weapon_type.barbarian_type_weapon");
        public static final TagKey<Item> WIZARD_TYPE_WEAPON = createTag("weapon_type.wizard_type_weapon");

        private static TagKey<Item> createTag(String name) {
            return ItemTags.create(WanderersOfTheRift.id(name));
        }
    }

    public static class Runegems {

        public static final TagKey<RunegemData> RAW = createTag("raw");
        public static final TagKey<RunegemData> CUT = createTag("cut");
        public static final TagKey<RunegemData> SHAPED = createTag("shaped");
        public static final TagKey<RunegemData> POLISHED = createTag("polished");
        public static final TagKey<RunegemData> FRAMED = createTag("framed");
        public static final TagKey<RunegemData> UNIQUE = createTag("unique");

        private static TagKey<RunegemData> createTag(String name) {
            return TagKey.create(ModDatapackRegistries.RUNEGEM_DATA_KEY,
                    ResourceLocation.fromNamespaceAndPath("wotr", name));
        }
    }

    public static class Abilities {

        public static final TagKey<AbstractAbility> RIFT_DROPS = createTag("rift_drops");

        private static TagKey<AbstractAbility> createTag(String name) {
            return TagKey.create(RegistryEvents.ABILITY_REGISTRY, ResourceLocation.fromNamespaceAndPath("wotr", name));
        }
    }
}

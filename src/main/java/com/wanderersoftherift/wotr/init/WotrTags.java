package com.wanderersoftherift.wotr.init;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.abilities.Ability;
import com.wanderersoftherift.wotr.item.runegem.RunegemData;
import com.wanderersoftherift.wotr.rift.objective.ObjectiveType;
import com.wanderersoftherift.wotr.world.level.levelgen.theme.RiftTheme;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class WotrTags {

    public static class Blocks {
        public static final TagKey<Block> BANNED_IN_RIFT = createTag("banned_in_rift");

        private static TagKey<Block> createTag(String name) {
            return BlockTags.create(WanderersOfTheRift.id(name));
        }
    }

    public static class Items {
        public static final TagKey<Item> BANNED_IN_RIFT = createTag("banned_in_rift");

        public static final TagKey<Item> UNBREAKABLE_EXCLUSIONS = createTag("unbreakable_exclusions");
        public static final TagKey<Item> SOCKETABLE = createTag("socketable");
        public static final TagKey<Item> SOCKETABLE_HELMET_SLOT = createTag("socketable_helmet_slot");
        public static final TagKey<Item> SOCKETABLE_CHESTPLATE_SLOT = createTag("socketable_chestplate_slot");
        public static final TagKey<Item> SOCKETABLE_LEGGINGS_SLOT = createTag("socketable_leggings_slot");
        public static final TagKey<Item> SOCKETABLE_BOOTS_SLOT = createTag("socketable_boots_slot");
        public static final TagKey<Item> SOCKETABLE_MAIN_HAND_SLOT = createTag("socketable_main_hand_slot");
        public static final TagKey<Item> SOCKETABLE_MAIN_HAND_SLOT_WEAPON = createTag(
                "socketable_main_hand_slot_weapon");
        public static final TagKey<Item> SOCKETABLE_MAIN_HAND_SLOT_TOOL = createTag("socketable_main_hand_slot_tool");
        public static final TagKey<Item> SOCKETABLE_OFF_HAND_SLOT = createTag("socketable_off_hand_slot");

        public static final TagKey<Item> LEATHER_TYPE_GEAR = createTag("gear_type.leather_type_gear");
        public static final TagKey<Item> IRON_TYPE_GEAR = createTag("gear_type.iron_type_gear");
        public static final TagKey<Item> DIAMOND_TYPE_GEAR = createTag("gear_type.diamond_type_gear");
        public static final TagKey<Item> GOLD_TYPE_GEAR = createTag("gear_type.gold_type_gear");

        public static final TagKey<Item> RANGE_TYPE_WEAPON = createTag("weapon_type.range_type_weapon");
        public static final TagKey<Item> IRON_TYPE_WEAPON = createTag("weapon_type.iron_type_weapon");
        public static final TagKey<Item> DIAMOND_TYPE_WEAPON = createTag("weapon_type.diamond_type_weapon");
        public static final TagKey<Item> GOLD_TYPE_WEAPON = createTag("weapon_type.gold_type_weapon");
        public static final TagKey<Item> ABILITY_SLOT_ACCEPTED = createTag("ability_slot_accepted");

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

        public static final TagKey<RunegemData> GEODE_RAW = createTag("geode_raw");
        public static final TagKey<RunegemData> GEODE_CUT = createTag("geode_cut");
        public static final TagKey<RunegemData> GEODE_SHAPED = createTag("geode_shaped");
        public static final TagKey<RunegemData> GEODE_POLISHED = createTag("geode_polished");
        public static final TagKey<RunegemData> GEODE_FRAMED = createTag("geode_framed");
        public static final TagKey<RunegemData> GEODE_UNIQUE = createTag("geode_unique");

        public static final TagKey<RunegemData> MONSTER_RAW = createTag("monster_raw");
        public static final TagKey<RunegemData> MONSTER_CUT = createTag("monster_cut");
        public static final TagKey<RunegemData> MONSTER_SHAPED = createTag("monster_shaped");
        public static final TagKey<RunegemData> MONSTER_POLISHED = createTag("monster_polished");
        public static final TagKey<RunegemData> MONSTER_FRAMED = createTag("monster_framed");
        public static final TagKey<RunegemData> MONSTER_UNIQUE = createTag("monster_unique");

        private static TagKey<RunegemData> createTag(String name) {
            return TagKey.create(WotrRegistries.Keys.RUNEGEM_DATA, ResourceLocation.fromNamespaceAndPath("wotr", name));
        }
    }

    public static class Abilities {

        public static final TagKey<Ability> RIFT_DROPS = createTag("rift_drops");

        private static TagKey<Ability> createTag(String name) {
            return TagKey.create(WotrRegistries.Keys.ABILITIES, ResourceLocation.fromNamespaceAndPath("wotr", name));
        }
    }

    public static class RiftThemes {

        public static final TagKey<RiftTheme> RANDOM_SELECTABLE = createTag("random_selectable");

        private static TagKey<RiftTheme> createTag(String name) {
            return TagKey.create(WotrRegistries.Keys.RIFT_THEMES, ResourceLocation.fromNamespaceAndPath("wotr", name));
        }
    }

    public static class Objectives {

        public static final TagKey<ObjectiveType> RANDOM_SELECTABLE = createTag("random_selectable");

        private static TagKey<ObjectiveType> createTag(String name) {
            return TagKey.create(WotrRegistries.Keys.OBJECTIVES, ResourceLocation.fromNamespaceAndPath("wotr", name));
        }
    }
}

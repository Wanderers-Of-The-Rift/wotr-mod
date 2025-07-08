package com.wanderersoftherift.wotr.datagen;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.init.WotrBlocks;
import com.wanderersoftherift.wotr.init.WotrEntities;
import com.wanderersoftherift.wotr.init.WotrItems;
import com.wanderersoftherift.wotr.init.client.WotrKeyMappings;
import com.wanderersoftherift.wotr.item.essence.EssenceValue;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.LanguageProvider;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

/* Handles Data Generation for I18n of the locale 'en_us' of the Wotr mod */
public class WotrLanguageProvider extends LanguageProvider {

    public WotrLanguageProvider(PackOutput output) {
        super(output, WanderersOfTheRift.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        // Helpers are available for various common object types. Every helper has two variants: an add() variant
        // for the object itself, and an addTypeHere() variant that accepts a supplier for the object.
        // The different names for the supplier variants are required due to generic type erasure.
        // All following examples assume the existence of the values as suppliers of the needed type.
        // See https://docs.neoforged.net/docs/1.21.1/resources/client/i18n/ for translation of other types.

        // Adds a block translation.
        addBlock(WotrBlocks.RUNE_ANVIL_ENTITY_BLOCK, "Rune Anvil");
        addBlock(WotrBlocks.RIFT_CHEST, "Rift Chest");
        addBlock(WotrBlocks.RIFT_SPAWNER, "Rift Spawner");
        addBlock(WotrBlocks.QUEST_HUB, "Quest Hub");
        addBlock(WotrBlocks.KEY_FORGE, "Key Forge");
        addBlock(WotrBlocks.DITTO_BLOCK, "Ditto Block");
        addBlock(WotrBlocks.SPRING_BLOCK, "Spring Block");
        addBlock(WotrBlocks.TRAP_BLOCK, "Trap Block");
        addBlock(WotrBlocks.PLAYER_TRAP_BLOCK, "Player Trap Block");
        addBlock(WotrBlocks.MOB_TRAP_BLOCK, "Mob Trap Block");
        addBlock(WotrBlocks.ABILITY_BENCH, "Ability Bench");
        addBlock(WotrBlocks.RIFT_MOB_SPAWNER, "Rift Mob Spawner");
        addBlock(WotrBlocks.NOGRAVGRAVEL, "No Gravity Gravel");
        addBlock(WotrBlocks.NOGRAVSAND, "No Gravity Sand");
        addBlock(WotrBlocks.NOGRAVREDSAND, "No Gravity Red Sand");
        addBlock(WotrBlocks.NOGRAVWHITECONCRETEPOWDER, "No Gravity White Concrete Powder");
        addBlock(WotrBlocks.NOGRAVORANGECONCRETEPOWDER, "No Gravity Orange Concrete Powder");
        addBlock(WotrBlocks.NOGRAVMAGENTACONCRETEPOWDER, "No Gravity Magenta Concrete Powder");
        addBlock(WotrBlocks.NOGRAVLIGHTBLUECONCRETEPOWDER, "No Gravity Light Blue Concrete Powder");
        addBlock(WotrBlocks.NOGRAVYELLOWCONCRETEPOWDER, "No Gravity Yellow Concrete Powder");
        addBlock(WotrBlocks.NOGRAVLIMECONCRETEPOWDER, "No Gravity Lime Concrete Powder");
        addBlock(WotrBlocks.NOGRAVPINKCONCRETEPOWDER, "No Gravity Pink Concrete Powder");
        addBlock(WotrBlocks.NOGRAVGRAYCONCRETEPOWDER, "No Gravity Gray Concrete Powder");
        addBlock(WotrBlocks.NOGRAVLIGHTGRAYCONCRETEPOWDER, "No Gravity Light Gray Concrete Powder");
        addBlock(WotrBlocks.NOGRAVCYANCONCRETEPOWDER, "No Gravity Cyan Concrete Powder");
        addBlock(WotrBlocks.NOGRAVPURPLECONCRETEPOWDER, "No Gravity Purple Concrete Powder");
        addBlock(WotrBlocks.NOGRAVBLUECONCRETEPOWDER, "No Gravity Blue Concrete Powder");
        addBlock(WotrBlocks.NOGRAVBROWNCONCRETEPOWDER, "No Gravity Brown Concrete Powder");
        addBlock(WotrBlocks.NOGRAVGREENCONCRETEPOWDER, "No Gravity Green Concrete Powder");
        addBlock(WotrBlocks.NOGRAVREDCONCRETEPOWDER, "No Gravity Red Concrete Powder");
        addBlock(WotrBlocks.NOGRAVBLACKCONCRETEPOWDER, "No Gravity Black Concrete Powder");

        // Adds an item translation.
        addItem(WotrItems.BUILDER_GLASSES, "Builder Glasses");
        addItem(WotrItems.RUNEGEM, "Runegem");
        addItem(WotrItems.RIFT_KEY, "Rift Key");
        addItem(WotrItems.RAW_RUNEGEM_GEODE, "Runegem Geode (Raw)");
        addItem(WotrItems.SHAPED_RUNEGEM_GEODE, "Runegem Geode (Shaped)");
        addItem(WotrItems.CUT_RUNEGEM_GEODE, "Runegem Geode (Cut)");
        addItem(WotrItems.POLISHED_RUNEGEM_GEODE, "Runegem Geode (Polished)");
        addItem(WotrItems.FRAMED_RUNEGEM_GEODE, "Runegem Geode (Framed)");
        addItem(WotrItems.RAW_RUNEGEM_MONSTER, "Monster Runegem (Raw)");
        addItem(WotrItems.SHAPED_RUNEGEM_MONSTER, "Monster Runegem (Shaped)");
        addItem(WotrItems.CUT_RUNEGEM_MONSTER, "Monster Runegem (Cut)");
        addItem(WotrItems.POLISHED_RUNEGEM_MONSTER, "Monster Runegem (Polished)");
        addItem(WotrItems.FRAMED_RUNEGEM_MONSTER, "Monster Runegem (Framed)");
        addItem(WotrItems.ABILITY_HOLDER, "Empty Ability");
        addItem(WotrItems.SKILL_THREAD, "Skill Thread");
        addItem(WotrItems.CURRENCY_BAG, "Currency Bag");

        addEntityType(WotrEntities.RIFT_ENTRANCE, "Rift Entrance");
        addEntityType(WotrEntities.RIFT_EXIT, "Rift Egress");
        addEntityType(WotrEntities.SIMPLE_EFFECT_PROJECTILE, "Projectile");

        addEssenceType("void", "Void");
        addEssenceType("flow", "Flow");
        addEssenceType("form", "Form");
        addEssenceType("order", "Order");
        addEssenceType("chaos", "Chaos");

        addEssenceType("earth", "Earth");
        addEssenceType("fire", "Fire");
        addEssenceType("water", "Water");
        addEssenceType("air", "Air");
        addEssenceType("life", "Life");
        addEssenceType("death", "Death");
        addEssenceType("light", "Light");
        addEssenceType("dark", "Dark");

        addEssenceType("animal", "Animal");
        addEssenceType("plant", "Plant");
        addEssenceType("mushroom", "Mushroom");
        addEssenceType("honey", "Honey");
        addEssenceType("food", "Food");
        addEssenceType("slime", "Slime");
        addEssenceType("mechanical", "Mechanical");
        addEssenceType("metal", "Metal");
        addEssenceType("fabric", "Fabric");
        addEssenceType("crystal", "Crystal");
        addEssenceType("energy", "Energy");
        addEssenceType("mind", "Mind");
        addEssenceType("nether", "Nether");
        addEssenceType("end", "End");
        addEssenceType("processor", "Processor");

        addTheme("buzzy_bees", "Buzzy Bees");
        addTheme("cave", "Cave");
        addTheme("color", "Color");
        addTheme("deepfrost", "Deepfrost");
        addTheme("desert", "Desert");
        addTheme("forest", "Forest");
        addTheme("jungle", "Jungle");
        addTheme("meadow", "Meadow");
        addTheme("mesa", "Mesa");
        addTheme("mushroom", "Mushroom");
        addTheme("nether", "Nether");
        addTheme("noir", "Noir");
        addTheme("processor", "Processor");
        addTheme("swamp", "Swamp");

        WotrBlocks.BLOCK_FAMILY_HELPERS.forEach(helper -> {
            // addBlock(helper.getBlock(), getTranslationString(helper.getBlock().get()));
            helper.getVariants().forEach((variant, block) -> addBlock(block, getTranslationString(block.get())));
            helper.getModVariants().forEach((variant, block) -> addBlock(block, getTranslationString(block.get())));
        });

        add("block." + WanderersOfTheRift.MODID + ".processor_block_1", "Processor Block 1 [Wall]");
        add("block." + WanderersOfTheRift.MODID + ".processor_block_2", "Processor Block 2 [Path]");
        add("block." + WanderersOfTheRift.MODID + ".processor_block_3", "Processor Block 3 [Floor]");
        add("block." + WanderersOfTheRift.MODID + ".processor_block_4", "Processor Block 4 [Alt Wall]");
        add("block." + WanderersOfTheRift.MODID + ".processor_block_5", "Processor Block 5 [Alt Floor]");
        add("block." + WanderersOfTheRift.MODID + ".processor_block_6", "Processor Block 6 [Planks]");
        add("block." + WanderersOfTheRift.MODID + ".processor_block_7", "Processor Block 7 [Bricks]");
        add("block." + WanderersOfTheRift.MODID + ".processor_block_8", "Processor Block 8 [Alt Path]");
        add("block." + WanderersOfTheRift.MODID + ".processor_block_9", "Processor Block 9");
        add("block." + WanderersOfTheRift.MODID + ".processor_block_10", "Processor Block 10");
        add("block." + WanderersOfTheRift.MODID + ".processor_block_11", "Processor Block 11");
        add("block." + WanderersOfTheRift.MODID + ".processor_block_12", "Processor Block 12");
        add("block." + WanderersOfTheRift.MODID + ".processor_block_13", "Processor Block 13");
        add("block." + WanderersOfTheRift.MODID + ".processor_block_14", "Processor Block 14");

        // Adds a generic translation
        add("itemGroup." + WanderersOfTheRift.MODID, "Wanderers of the Rift");
        add(WanderersOfTheRift.translationId("itemGroup", "ability"), "Abilities");
        add(WanderersOfTheRift.translationId("itemGroup", "runegem"), "Runegems");
        add(WanderersOfTheRift.translationId("itemGroup", "dev"), "Builders of the Rift");

        add("item." + WanderersOfTheRift.MODID + ".rift_key.themed", "Rift Key of %s");

        add("container." + WanderersOfTheRift.MODID + ".rune_anvil", "Rune Anvil");
        add("container." + WanderersOfTheRift.MODID + ".rune_anvil.apply", "Apply");
        add("container." + WanderersOfTheRift.MODID + ".rift_chest", "Rift Chest");
        add("container." + WanderersOfTheRift.MODID + ".key_forge", "Key Forge");
        add("container." + WanderersOfTheRift.MODID + ".ability_bench", "Ability Bench");
        add(WanderersOfTheRift.translationId("container", "trading"), "%s Trader");
        add(WanderersOfTheRift.translationId("container", "quest.selection"), "Select Quest");
        add(WanderersOfTheRift.translationId("container", "quest.complete"), "Complete");
        add(WanderersOfTheRift.translationId("container", "quest.handin"), "Hand In");
        add(WanderersOfTheRift.translationId("container", "quest.goals"), "Goal");
        add(WanderersOfTheRift.translationId("container", "quest.rewards"), "Reward");
        add(WanderersOfTheRift.translationId("container", "quest.abandon"), "Abandon");
        add(WanderersOfTheRift.translationId("container", "quest.are_you_sure"), "Really abandon?");
        add(WanderersOfTheRift.translationId("container", "quest.goal.give"), "Deliver: ");
        add(WanderersOfTheRift.translationId("container", "quest.goal.kill"), "Defeat %s %s");
        add(WanderersOfTheRift.translationId("container", "quest.accept"), "Accept");
        add(WanderersOfTheRift.translationId("container", "quests"), "Quests");
        add(WanderersOfTheRift.translationId("container", "quest_complete"), "Quest Complete!");

        add("container." + WanderersOfTheRift.MODID + ".ability_bench.upgrade", "Upgrades");
        add("container." + WanderersOfTheRift.MODID + ".ability_bench.unlock", "Unlock next choice");
        add(WanderersOfTheRift.translationId("container", "rift_complete"), "Rift Overview");
        add(WanderersOfTheRift.translationId("container", "rift_complete.reward"), "Rewards");

        add(WanderersOfTheRift.translationId("container", "guilds"), "Guilds");
        add(WanderersOfTheRift.translationId("container", "guild.rank"), "Rank: %s");
        add(WanderersOfTheRift.translationId("container", "guild.reputation"), "Reputation: %s/%s");

        add(WanderersOfTheRift.translationId("container", "wallet"), "Wallet");

        add(WanderersOfTheRift.translationId("stat", "result"), "Result: ");
        add(WanderersOfTheRift.translationId("stat", "result.success"), "Success");
        add(WanderersOfTheRift.translationId("stat", "result.survived"), "Escaped");
        add(WanderersOfTheRift.translationId("stat", "result.failed"), "Failed");
        add(WanderersOfTheRift.translationId("stat", "time"), "Time in rift: ");
        add(WanderersOfTheRift.translationId("stat", "mobs_killed"), "Mobs killed: ");
        add(WanderersOfTheRift.translationId("stat", "chests_opened"), "Chests opened: ");

        add(WanderersOfTheRift.translationId("screen", "configure_hud"), "Configure HUD");

        add("command." + WanderersOfTheRift.MODID + ".dev_world_set",
                "Dev World settings applied:\n - %1$s: Disabled\n - %2$s: Disabled\n - %3$s: Disabled\n - %4$s: Disabled\n - %5$s: Disabled\n - %6$s: Disabled");
        add("command." + WanderersOfTheRift.MODID + ".invalid_item", "Held item is empty!");
        add(WanderersOfTheRift.translationId("commands", "ability.invalid"), "There is no ability '%s'");
        add("command." + WanderersOfTheRift.MODID + ".invalid_player", "Player is null!");
        add("command." + WanderersOfTheRift.MODID + ".get_item_stack_components.success",
                "Item Components available for '%1$s'");
        add("command." + WanderersOfTheRift.MODID + ".rift_key.set_tier", "Rift key tier set to %s");
        add("command." + WanderersOfTheRift.MODID + ".rift_key.set_theme", "Rift key theme set to %s");
        add("command." + WanderersOfTheRift.MODID + ".rift_key.set_objective", "Rift key objective set to %s");
        add("command." + WanderersOfTheRift.MODID + ".rift_key.set_seed", "Rift key seed set to %s");
        add("command." + WanderersOfTheRift.MODID + ".invalid_theme", "Invalid theme '%s'");
        add("command." + WanderersOfTheRift.MODID + ".rift_key.invalid_item", "You must hold a rift key in your hand!");
        add("command." + WanderersOfTheRift.MODID + ".spawn_piece.generating", "Generating %s");
        add(WanderersOfTheRift.translationId("command", "make_ability_item.success"), "Applied ability components");

        add("ability." + WanderersOfTheRift.MODID + ".cannot_unlock",
                "You must unlock the following to get this boost: ");
        add("ability." + WanderersOfTheRift.MODID + ".fireball_ability", "Fireball");
        add("ability." + WanderersOfTheRift.MODID + ".icicles_ability", "Icicles");
        add("ability." + WanderersOfTheRift.MODID + ".mega_boost", "Mega Boost");
        add("ability." + WanderersOfTheRift.MODID + ".dash", "Dash");
        add("ability." + WanderersOfTheRift.MODID + ".summon_skeletons", "Summon Skeletons");
        add("ability." + WanderersOfTheRift.MODID + ".test_ability", "Test Ability");
        add("ability." + WanderersOfTheRift.MODID + ".knockback", "Knockback");
        add("ability." + WanderersOfTheRift.MODID + ".pull", "Pull");
        add("ability." + WanderersOfTheRift.MODID + ".heal", "Heal");
        add("ability." + WanderersOfTheRift.MODID + ".firetouch", "Nonsense Experimental Ability");

        add(WanderersOfTheRift.translationId("effect_marker", "fireshield"), "Fire Shield");

        add("accessibility." + WanderersOfTheRift.MODID + ".screen.title",
                "Wanderers of the Rifts: Accessibility Settings");
        add("accessibility." + WanderersOfTheRift.MODID + ".menubutton", "WotR Accessibility (tmp)");
        add("accessibility." + WanderersOfTheRift.MODID + ".screen.trypophobia", "Trypophobia Friendly");
        add("accessibility." + WanderersOfTheRift.MODID + ".screen.arachnophobia", "Arachnophobia Friendly");
        add("accessibility." + WanderersOfTheRift.MODID + ".screen.flashing_lights", "Flashing Lights");
        add("accessibility." + WanderersOfTheRift.MODID + ".screen.misophonia", "Misophonia Friendly");
        add("accessibility." + WanderersOfTheRift.MODID + ".screen.high_contrast", "High Contrast");
        add("accessibility." + WanderersOfTheRift.MODID + ".screen.hard_of_hearing", "Hard of Hearing");
        add("accessibility." + WanderersOfTheRift.MODID + ".screen.reduced_motion", "Reduced Motion");
        add("accessibility." + WanderersOfTheRift.MODID + ".screen.tooltip.trypophobia",
                "Removes any trypophobia-triggering aspects");
        add("accessibility." + WanderersOfTheRift.MODID + ".screen.tooltip.arachnophobia",
                "Replaces all the spiders with cute turtles!");
        add("accessibility." + WanderersOfTheRift.MODID + ".screen.tooltip.flashing_lights",
                "Reduces flashing-light effects");
        add("accessibility." + WanderersOfTheRift.MODID + ".screen.tooltip.misophonia",
                "Replaces certain sounds that are potentially triggering with different ones (?)");
        add("accessibility." + WanderersOfTheRift.MODID + ".screen.tooltip.high_contrast",
                "Enhances UI and HUD elements with higher contrast for better visibility");
        add("accessibility." + WanderersOfTheRift.MODID + ".screen.tooltip.hard_of_hearing",
                "Enhances audio cues for better accessibility");
        add("accessibility." + WanderersOfTheRift.MODID + ".screen.tooltip.reduced_motion",
                "Disables or slows down UI animations, camera shake, or screen effects");

        add("tooltip." + WanderersOfTheRift.MODID + ".rift_key_tier", "Tier: %s");
        add("tooltip." + WanderersOfTheRift.MODID + ".rift_key_theme", "Theme: %s");
        add("tooltip." + WanderersOfTheRift.MODID + ".rift_key_objective", "Objective: %s");
        add("tooltip." + WanderersOfTheRift.MODID + ".essence_value", "Essence: %s %s");
        add("tooltip." + WanderersOfTheRift.MODID + ".essence_header", "Essence:");
        add("tooltip." + WanderersOfTheRift.MODID + ".socket", "Sockets: ");
        add("tooltip." + WanderersOfTheRift.MODID + ".implicit", "Implicit: ");
        add("tooltip." + WanderersOfTheRift.MODID + ".empty_socket", "(Empty Slot)");
        add("tooltip." + WanderersOfTheRift.MODID + ".show_extra_info", "Hold [%s] for additional information");
        add(WanderersOfTheRift.translationId("tooltip", "mana_bar"), "Mana: %s/%s");
        add(WanderersOfTheRift.translationId("tooltip", "rift_key_seed"), "Seed: %s");
        add(WanderersOfTheRift.translationId("tooltip", "runegem.shape"), "Shape: %s");
        add(WanderersOfTheRift.translationId("tooltip", "runegem.modifiers"), "Modifiers:");
        add(WanderersOfTheRift.translationId("tooltip", "currency_bag"), "Gain %s when consumed");

        add(WanderersOfTheRift.translationId("itemname", "consolation1"), "Whomp whomp");
        add(WanderersOfTheRift.translationId("itemname", "consolation2"), "Tissue");
        add(WanderersOfTheRift.translationId("itemname", "consolation3"), "Success is built on a mountain of failure");
        add(WanderersOfTheRift.translationId("itemname", "consolation4"), "Think of it as a learning opportunity");

        add("subtitles." + WanderersOfTheRift.MODID + ".rift_open", "Rift Opens");

        add("modifier." + WanderersOfTheRift.MODID + ".attribute.add.positive", "+%s %s");
        add("modifier." + WanderersOfTheRift.MODID + ".attribute.add.negative", "%s %s");
        add("modifier." + WanderersOfTheRift.MODID + ".attribute.multiply.positive", "+%s%% %s");
        add("modifier." + WanderersOfTheRift.MODID + ".attribute.multiply.negative", "%s%% %s");

        add(WanderersOfTheRift.MODID + ".rift.create.failed", "Failed to create rift");

        add(WanderersOfTheRift.translationId("ability_upgrade", "aoe.name"), "Area of Effect");
        add(WanderersOfTheRift.translationId("ability_upgrade", "aoe.description"),
                "Increases Area of Effect by 1 block");
        add(WanderersOfTheRift.translationId("ability_upgrade", "cooldown.name"), "Decrease Cooldown");
        add(WanderersOfTheRift.translationId("ability_upgrade", "cooldown.description"), "Decreases Cooldown by 10%");
        add(WanderersOfTheRift.translationId("ability_upgrade", "damage.name"), "Damage Up");
        add(WanderersOfTheRift.translationId("ability_upgrade", "damage.description"), "Increases Damage by 10%");
        add(WanderersOfTheRift.translationId("ability_upgrade", "drain_life.name"), "Drain Life");
        add(WanderersOfTheRift.translationId("ability_upgrade", "drain_life.description"),
                "Drains 1 life per target hit");
        add(WanderersOfTheRift.translationId("ability_upgrade", "mana_cost.name"), "Mana Cost Decrease");
        add(WanderersOfTheRift.translationId("ability_upgrade", "mana_cost.description"), "Decreases mana cost by 10%");
        add(WanderersOfTheRift.translationId("ability_upgrade", "projectile_count.name"), "More Projectiles");
        add(WanderersOfTheRift.translationId("ability_upgrade", "projectile_count.description"),
                "Adds an additional projectile");
        add(WanderersOfTheRift.translationId("ability_upgrade", "projectile_speed.name"), "Projectile Speed");
        add(WanderersOfTheRift.translationId("ability_upgrade", "projectile_speed.description"),
                "Increases Projectile speed by 10%");
        add(WanderersOfTheRift.translationId("ability_upgrade", "projectile_spread.name"),
                "Projectile Spread Reduction");
        add(WanderersOfTheRift.translationId("ability_upgrade", "projectile_spread.description"),
                "Decreases Projectile Spread by 10%");
        add(WanderersOfTheRift.translationId("ability_upgrade", "healing_power.name"), "Healing Up");
        add(WanderersOfTheRift.translationId("ability_upgrade", "healing_power.description"),
                "Heals an additional heart");

        add(WotrKeyMappings.ABILITY_CATEGORY, "Wanderers of the Rift: Abilities");
        add(WotrKeyMappings.MENU_CATEGORY, "Wanderers of the Rift: Menus");
        add(WotrKeyMappings.MISC_CATEGORY, "Wanderers of the Rift: Misc");
        add(WotrKeyMappings.ABILITY_1_KEY.getName(), "Use Ability 1");
        add(WotrKeyMappings.ABILITY_2_KEY.getName(), "Use Ability 2");
        add(WotrKeyMappings.ABILITY_3_KEY.getName(), "Use Ability 3");
        add(WotrKeyMappings.ABILITY_4_KEY.getName(), "Use Ability 4");
        add(WotrKeyMappings.ABILITY_5_KEY.getName(), "Use Ability 5");
        add(WotrKeyMappings.ABILITY_6_KEY.getName(), "Use Ability 6");
        add(WotrKeyMappings.ABILITY_7_KEY.getName(), "Use Ability 7");
        add(WotrKeyMappings.ABILITY_8_KEY.getName(), "Use Ability 8");
        add(WotrKeyMappings.ABILITY_9_KEY.getName(), "Use Ability 9");
        add(WotrKeyMappings.PREV_ABILITY_KEY.getName(), "Select Previous Ability");
        add(WotrKeyMappings.NEXT_ABILITY_KEY.getName(), "Select Next Ability");
        add(WotrKeyMappings.USE_ABILITY_KEY.getName(), "Use Selected Ability");
        add(WotrKeyMappings.ACTIVATE_ABILITY_SCROLL.getName(), "Activate Ability Bar Scroll");
        add(WotrKeyMappings.SHOW_TOOLTIP_INFO.getName(), "Show Additional Tooltip Info");
        add(WotrKeyMappings.JIGSAW_NAME_TOGGLE_KEY.getName(), "Show Jigsaw Block Info");
        add(WotrKeyMappings.CHARACTER_MENU_KEY.getName(), "Open Character Menu");

        add(WanderersOfTheRift.translationId("keybinds", "l_alt"), "LAlt");
        add(WanderersOfTheRift.translationId("keybinds", "r_alt"), "RAlt");
        add(WanderersOfTheRift.translationId("keybinds", "l_ctrl"), "LCtrl");
        add(WanderersOfTheRift.translationId("keybinds", "r_ctrl"), "RCtrl");
        add(WanderersOfTheRift.translationId("keybinds", "mod_alt"), "Alt+");
        add(WanderersOfTheRift.translationId("keybinds", "mod_ctrl"), "Ctrl+");
        add(WanderersOfTheRift.translationId("keybinds", "mod_shift"), "Shi+");

        add(WanderersOfTheRift.translationId("rei", "rolls_label"), "Rolls:");
        add(WanderersOfTheRift.translationId("rei", "percent.min"), "%s: > %s%%");
        add(WanderersOfTheRift.translationId("rei", "percent.max"), "%s: < %s%%");
        add(WanderersOfTheRift.translationId("rei", "absolute.min"), "%s: < %s");
        add(WanderersOfTheRift.translationId("rei", "absolute.max"), "%s: > %s");

        add(WanderersOfTheRift.translationId("objective", "kill.name"), "Kill mobs");
        add(WanderersOfTheRift.translationId("objective", "stealth.name"), "Stealth");
        add(WanderersOfTheRift.translationId("objective", "kill.description"), "Defeat %s monsters");
        add(WanderersOfTheRift.translationId("objective", "stealth.description"), "Defeat monsters stealthily");
        add(WanderersOfTheRift.translationId("gui", "objective_status.complete"), "Objective Complete");

        add(WanderersOfTheRift.translationId("button", "reset"), "Reset");
        add(WanderersOfTheRift.translationId("button", "close"), "Close");
        add(WanderersOfTheRift.translationId("button", "rotate"), "Rotate");
        add(WanderersOfTheRift.translationId("button", "show"), "Show");
        add(WanderersOfTheRift.translationId("button", "hide"), "Hide");
        add(WanderersOfTheRift.translationId("button", "hud_presets"), "Preset");
        add(WanderersOfTheRift.translationId("button", "customize"), "Customize");
        add("hud.minecraft.hotbar", "Hot Bar");
        add("hud.minecraft.experience_bar", "Experience Bar");
        add("hud.minecraft.health_armor", "Health and Armor");
        add("hud.minecraft.food_level", "Food Level");
        add("hud.minecraft.experience_level", "Experience Level");
        add("hud.minecraft.air_level", "Air Level");
        add("hud.minecraft.effects", "Effects");
        add(WanderersOfTheRift.translationId("hud", "ability_bar"), "Ability Bar");
        add(WanderersOfTheRift.translationId("hud", "mana_bar"), "Mana Bar");
        add(WanderersOfTheRift.translationId("hud", "effect_bar"), "Ability Effect Bar");
        add(WanderersOfTheRift.translationId("hud", "objective"), "Objective");

        add(WanderersOfTheRift.translationId("hud_preset", "default"), "Default");
        add(WanderersOfTheRift.translationId("hud_preset", "minimal"), "Minimal");
        add(WanderersOfTheRift.translationId("hud_preset", "custom"), "Custom");

        add(WanderersOfTheRift.translationId("attribute", "ability.aoe"), "Ability Area of Effect");
        add(WanderersOfTheRift.translationId("attribute", "ability.raw_damage"), "Ability Damage");
        add(WanderersOfTheRift.translationId("attribute", "ability.cooldown"), "Ability Cooldown");
        add(WanderersOfTheRift.translationId("attribute", "ability.heal_amount"), "Ability Heal Amount");
        add(WanderersOfTheRift.translationId("attribute", "critical_chance"), "Critical Chance");
        add(WanderersOfTheRift.translationId("attribute", "critical_avoidance"), "Critical Avoidance");
        add(WanderersOfTheRift.translationId("attribute", "critical_bonus"), "Critical Bonus");
        add(WanderersOfTheRift.translationId("attribute", "ability.mana_cost"), "Mana Cost");
        add(WanderersOfTheRift.translationId("attribute", "thorns_chance"), "Thorns Chance");
        add(WanderersOfTheRift.translationId("attribute", "thorns_damage"), "Thorns Damage");
        add(WanderersOfTheRift.translationId("attribute", "life_leech"), "Life Leech");
        add(WanderersOfTheRift.translationId("attribute", "projectile_spread"), "Ability Projectile Spread");
        add(WanderersOfTheRift.translationId("attribute", "projectile_count"), "Ability Projectile Count");
        add(WanderersOfTheRift.translationId("attribute", "projectile_speed"), "Ability Projectile Speed");
        add(WanderersOfTheRift.translationId("attribute", "projectile_pierce"), "Ability Projectile Pierce");
        add(WanderersOfTheRift.translationId("attribute", "max_mana"), "Max Mana");
        add(WanderersOfTheRift.translationId("attribute", "mana_regen_rate"), "Mana Regeneration");
        add(WanderersOfTheRift.translationId("attribute", "mana_degen_rate"), "Mana Degeneration");
        addRunegems();
        addModifiers();

        add(WanderersOfTheRift.translationId("message", "disabled_in_rifts"), "Disabled in rifts");
        add(WanderersOfTheRift.translationId("message", "currency_obtained"), "Added %s %s to your wallet");

        add(WanderersOfTheRift.translationId("currency", "boondongle"), "Boondongle");
        add(WanderersOfTheRift.translationId("currency", "notorized_fish"), "Notorized Fish");

        add(WanderersOfTheRift.translationId("guild", "cat"), "Whiskers of the Rift");
        add(WanderersOfTheRift.translationId("guild", "cat.rank.0"), "Damp Kitten");
        add(WanderersOfTheRift.translationId("guild", "stick"), "Figures of the Stick");
        add(WanderersOfTheRift.translationId("guild", "stick.rank.0"), "Twigling");
        add(WanderersOfTheRift.translationId("guild", "wotr"), "Waterers of the Rifts");
        add(WanderersOfTheRift.translationId("guild", "wotr.rank.0"), "Glass half full");

        add(WanderersOfTheRift.translationId("quest", "skillthread.title"), "Deliver Skill Thread");
        add(WanderersOfTheRift.translationId("quest", "skillthread.description"),
                "I've heard there is a strange thread that can be found in the rifts that holds the key to unlocking the full potential of abilities. Could you bring me a sample?");
        add(WanderersOfTheRift.translationId("quest", "gold_and_iron.title"), "Sample the Wares");
        add(WanderersOfTheRift.translationId("quest", "gold_and_iron.description"),
                "Psst. You look like a discerning customer? I've got some powerful shiny baubles if you can bring me some precious metals!");
        add(WanderersOfTheRift.translationId("quest", "kill_skeletons.title"), "Defeat Skeletons");
        add(WanderersOfTheRift.translationId("quest", "kill_skeletons.description"), "Bones scare me! Save me!");

        add("mobgroup.minecraft.skeletons", "Skeletons");
    }

    private void addRunegems() {
        WotrRuneGemDataProvider.DATA.entrySet().stream().forEach(entry -> {
            add(WanderersOfTheRift.translationId("runegem", entry.getKey().location().getPath()),
                    snakeCaseToCapitalizedCase(entry.getKey().location().getPath()) + " Runegem");
        });
    }

    private void addModifiers() {
        WotrModifierProvider.DATA.entrySet().stream().forEach(entry -> {
            add(WanderersOfTheRift.translationId("modifier", entry.getKey().location().getPath()),
                    snakeCaseToCapitalizedCase(entry.getKey().location().getPath()));
        });
    }

    private void addEssenceType(String id, String value) {
        add(EssenceValue.ESSENCE_TYPE_PREFIX + "." + WanderersOfTheRift.MODID + "." + id, value);
    }

    private void addTheme(String id, String value) {
        add("rift_theme." + WanderersOfTheRift.MODID + "." + id, value);
    }

    private static @NotNull String getTranslationString(Block block) {
        String idString = BuiltInRegistries.BLOCK.getKey(block).getPath();
        return snakeCaseToCapitalizedCase(idString);
    }

    private static @NotNull String snakeCaseToCapitalizedCase(String idString) {
        StringBuilder sb = new StringBuilder();
        for (String word : idString.toLowerCase(Locale.ROOT).split("_")) {
            sb.append(word.substring(0, 1).toUpperCase(Locale.ROOT));
            sb.append(word.substring(1));
            sb.append(" ");
        }
        return sb.toString().trim();
    }
}

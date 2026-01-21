package com.wanderersoftherift.wotr.datagen;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.init.WotrBlocks;
import com.wanderersoftherift.wotr.init.WotrEntities;
import com.wanderersoftherift.wotr.init.WotrItems;
import com.wanderersoftherift.wotr.init.client.WotrKeyMappings;
import com.wanderersoftherift.wotr.init.worldgen.WotrRiftLayoutLayers;
import com.wanderersoftherift.wotr.item.essence.EssenceValue;
import com.wanderersoftherift.wotr.util.listedit.Append;
import com.wanderersoftherift.wotr.util.listedit.Clear;
import com.wanderersoftherift.wotr.util.listedit.Drop;
import com.wanderersoftherift.wotr.util.listedit.DropLast;
import com.wanderersoftherift.wotr.util.listedit.Prepend;
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
        addBlock(WotrBlocks.NPC, "NPC Block");
        addBlock(WotrBlocks.KEY_FORGE, "Key Forge");
        addBlock(WotrBlocks.DITTO_BLOCK, "Ditto Block");
        addBlock(WotrBlocks.SPRING_BLOCK, "Spring Block");
        addBlock(WotrBlocks.TRAP_BLOCK, "Trap Block");
        addBlock(WotrBlocks.PLAYER_TRAP_BLOCK, "Player Trap Block");
        addBlock(WotrBlocks.MOB_TRAP_BLOCK, "Mob Trap Block");
        addBlock(WotrBlocks.ABILITY_BENCH, "Ability Bench");
        addBlock(WotrBlocks.RIFT_MOB_SPAWNER, "Rift Mob Spawner");
        addBlock(WotrBlocks.ANOMALY, "Anomaly");
        addBlock(WotrBlocks.OBJECTIVE, "Objective");

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

        addItem(WotrItems.NOIR_HELMET, "Fedora");
        addItem(WotrItems.COLOR_HELMET, "Clown");

        // Essence items
        WotrItems.ESSENCE_ITEMS.forEach((essenceType, essenceItem) -> {
            addItem(essenceItem, essenceType.name + " Essence");
        });

        addEntityType(WotrEntities.RIFT_ENTRANCE, "Rift Entrance");
        addEntityType(WotrEntities.RIFT_EXIT, "Rift Egress");
        addEntityType(WotrEntities.SIMPLE_EFFECT_PROJECTILE, "Projectile");
        addEntityType(WotrEntities.RIFT_ZOMBIE, "Rift Zombie");
        addEntityType(WotrEntities.RIFT_SKELETON, "Rift Skeleton");
        addEntityType(WotrEntities.DRONE_BEE, "Drone");

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
        add(WanderersOfTheRift.translationId("itemGroup", "npc"), "NPCs");
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
        add(WanderersOfTheRift.translationId("container", "quest.hand_in_to"), "Hand in to: %s");
        add(WanderersOfTheRift.translationId("container", "quest.goal.give"), "Deliver %s/%s ");
        add(WanderersOfTheRift.translationId("container", "quest.goal.kill"), "Defeat %s (%s/%s)");
        add(WanderersOfTheRift.translationId("container", "quest.goal.complete_rifts"), "%s %srifts (%s/%s)");
        add(WanderersOfTheRift.translationId("container", "quest.goal.anomaly"), "Close anomalies (%s/%s)");
        add(WanderersOfTheRift.translationId("container", "quest.goal.anomaly.typed"), "Close %s anomalies (%s/%s)");
        add(WanderersOfTheRift.translationId("container", "quest.goal.visit_room"), "Explore rift rooms (%s/%s)");
        add(WanderersOfTheRift.translationId("container", "quest.accept"), "Accept");
        add(WanderersOfTheRift.translationId("container", "quests"), "Quests");
        add(WanderersOfTheRift.translationId("container", "quest_complete"), "Quest Complete!");
        add(WanderersOfTheRift.translationId("container", "guild_rank_up"), "Guild Rank Up!");
        add(WanderersOfTheRift.translationId("container", "guilds.claim_reward"), "Claim rank up reward");
        add(WanderersOfTheRift.translationId("container", "quest.goal.objective_block"),
                "Activate objective blocks (%s/%s)");

        add("container." + WanderersOfTheRift.MODID + ".ability_bench.upgrade", "Upgrades");
        add("container." + WanderersOfTheRift.MODID + ".ability_bench.unlock", "Unlock next choice");
        add(WanderersOfTheRift.translationId("container", "rift_complete"), "Rift Overview");
        add(WanderersOfTheRift.translationId("container", "rift_complete.reward"), "Rewards");

        add(WanderersOfTheRift.translationId("container", "guilds"), "Guilds");
        add(WanderersOfTheRift.translationId("container", "guild.rank"), "Rank: %s");
        add(WanderersOfTheRift.translationId("container", "guild.reputation"), "Reputation: %s/%s");
        add(WanderersOfTheRift.translationId("container", "guild.reputation.max"), "Reputation: MAX");

        add(WanderersOfTheRift.translationId("container", "wallet"), "Wallet");

        add(WanderersOfTheRift.translationId("stat", "result"), "Result: ");
        add(WanderersOfTheRift.translationId("stat", "result.success"), "Success");
        add(WanderersOfTheRift.translationId("stat", "result.survived"), "Escaped");
        add(WanderersOfTheRift.translationId("stat", "result.failed"), "Failed");
        add(WanderersOfTheRift.translationId("stat", "time"), "Time in rift: ");
        add(WanderersOfTheRift.translationId("stat", "mobs_killed"), "Mobs killed: ");
        add(WanderersOfTheRift.translationId("stat", "chests_opened"), "Chests opened: ");

        add(WanderersOfTheRift.translationId("screen", "configure_hud"), "Configure HUD");

        add(WanderersOfTheRift.translationId("command", "total"), "Total: %s");
        add("command." + WanderersOfTheRift.MODID + ".dev_world_set",
                "Dev World settings applied:\n - %1$s: Disabled\n - %2$s: Disabled\n - %3$s: Disabled\n - %4$s: Disabled\n - %5$s: Disabled\n - %6$s: Disabled");
        add("command." + WanderersOfTheRift.MODID + ".invalid_item", "Held item is empty!");
        add(WanderersOfTheRift.translationId("commands", "ability.invalid"), "There is no ability '%s'");
        add("command." + WanderersOfTheRift.MODID + ".invalid_player", "Player is null!");
        add("command." + WanderersOfTheRift.MODID + ".get_item_stack_components.success",
                "Item Components available for '%1$s'");
        add("command." + WanderersOfTheRift.MODID + ".rift_key.set_tier", "Rift key tier set to %s");
        add("command." + WanderersOfTheRift.MODID + ".rift_key.set_preset", "Rift key preset set to %s");
        add("command." + WanderersOfTheRift.MODID + ".rift_key.set_theme", "Rift key theme set to %s");
        add("command." + WanderersOfTheRift.MODID + ".rift_key.set_objective", "Rift key objective set to %s");
        add("command." + WanderersOfTheRift.MODID + ".rift_key.set_seed", "Rift key seed set to %s");
        add("command." + WanderersOfTheRift.MODID + ".rift_key.layout_layers.add", "Added Layout Edit: %s");
        add("command." + WanderersOfTheRift.MODID + ".rift_key.layout_layers.undo", "Undid Layout Edit: %s");
        add("command." + WanderersOfTheRift.MODID + ".rift_key.layout_layers.clear", "Undid all edits");
        add("command." + WanderersOfTheRift.MODID + ".rift_key.generator.bake", "Custom preset created");
        add("command." + WanderersOfTheRift.MODID + ".rift_key.generator.export", "Rift generator preset saved to %s");
        add("command." + WanderersOfTheRift.MODID + ".rift_key.generator.export.output_contains_dot",
                "output path must not contain dots");
        add("command." + WanderersOfTheRift.MODID + ".rift_key.generator.export.not_custom",
                "Preset must be custom to be exportable, did you forget to run `/wotr riftKey generator bake`?`");
        add("command." + WanderersOfTheRift.MODID + ".rift_key.generator.export.encode_failed",
                "Could not encode preset");
        add("command." + WanderersOfTheRift.MODID + ".invalid_theme", "Invalid theme '%s'");
        add("command." + WanderersOfTheRift.MODID + ".invalid_objective", "Invalid objective '%s'");
        add("command." + WanderersOfTheRift.MODID + ".invalid_generator_preset", "Invalid generator preset '%s'");
        add("command." + WanderersOfTheRift.MODID + ".invalid_template_pool", "Invalid template pool '%s'");
        add("command." + WanderersOfTheRift.MODID + ".invalid_rift_parameter", "Invalid rift parameter %s");
        add("command." + WanderersOfTheRift.MODID + ".invalid_ability_resource", "Invalid ability resource '%s'");
        add("command." + WanderersOfTheRift.MODID + ".rift_key.invalid_item", "You must hold a rift key in your hand!");
        add("command." + WanderersOfTheRift.MODID + ".spawn_piece.generating", "Generating %s");
        add("command." + WanderersOfTheRift.MODID + ".rift_parameter.get", "Current value of parameter is %s");
        add("command." + WanderersOfTheRift.MODID + ".rift_parameter.set", "Updated value of parameter %s -> %s");
        add("command." + WanderersOfTheRift.MODID + ".rift_parameter.missing",
                "This parameter does not exist in this world");
        add(WanderersOfTheRift.translationId("command", "make_ability_item.success"), "Applied ability components");
        add(WanderersOfTheRift.translationId("command", "stats.invalid"), "Invalid primary statistic");
        add(WanderersOfTheRift.translationId("command", "show_attribute"), "%s: %s");
        add(WanderersOfTheRift.translationId("command", "set_attribute"), "%s set to %s");
        add(WanderersOfTheRift.translationId("command", "place.processor.invalid"), "Invalid processor %s");
        add(WanderersOfTheRift.translationId("command", "rift.roominfo.void"), "Room: none");
        add(WanderersOfTheRift.translationId("command", "rift.roominfo.room"), "Room: %s");
        add(WanderersOfTheRift.translationId("command", "rift.roominfo.origin"), "Origin: %s %s %s");
        add(WanderersOfTheRift.translationId("command", "rift.roominfo.size"), "Size: %s %s %s");
        add(WanderersOfTheRift.translationId("command", "rift.roominfo.transform"), "Transform: %s %s %s");
        add(WanderersOfTheRift.translationId("command", "rift.roominfo.invalid"), "Invalid level");
        add(WanderersOfTheRift.translationId("command", "gear.socket.invalid"),
                "No item held or does not support sockets");
        add(WanderersOfTheRift.translationId("command", "gear.implicit.invalid"),
                "No item held or does not support implicits");
        add(WanderersOfTheRift.translationId("command", "generic.success"), "Done.");
        add(WanderersOfTheRift.translationId("command", "quest.log.print"), "Quest completion counts:");
        add(WanderersOfTheRift.translationId("command", "quest.active"), "Active Quests:");

        add("death.attack.wotr.fire.item", "%1$s was scorched by %2$s");
        add("death.attack.wotr.fire.player", "%1$s was burned alive by %2$s");
        add("death.attack.wotr.fire", "%1$s died in flames");
        add("death.attack.wotr.fire_burn.item", "%1$s was set ablaze by %2$s");
        add("death.attack.wotr.fire_burn.player", "%1$s was burned alive by %2$s");
        add("death.attack.wotr.fire_burn", "%1$s burned to a crisp");
        add("death.attack.wotr.ice.item", "%1$s was cooled below zero by %2$s");
        add("death.attack.wotr.ice.player", "%1$s was frozen solid by %2$s");
        add("death.attack.wotr.ice", "%1$s froze to death");
        add("death.attack.wotr.poison.item", "%1$s was overwhelmed by the poison of %2$s");
        add("death.attack.wotr.poison.player", "%1$s succumbed to poison from %2$s");
        add("death.attack.wotr.poison", "%1$s did not cure their infection");
        add("death.attack.wotr.lightning.item", "%1$s was struck by lightning from %2$s");
        add("death.attack.wotr.lightning.player", "%1$s did not recover from the shock %2$s caused");
        add("death.attack.wotr.lightning", "%1$s was shocked to death");
        add("death.attack.wotr.earth.item", "%1$s was crushed by %2$s");
        add("death.attack.wotr.earth.player", "%1$s got pinned down by %2$s");
        add("death.attack.wotr.earth", "%1$s was buried alive");
        add("death.attack.wotr.thorns.item", "%1$s was killed by the thorns of %2$s");
        add("death.attack.wotr.thorns.player", "%1$s was killed by attacking %2$s");
        add("death.attack.wotr.thorns", "%1$s was killed by thorns");

        add("ability." + WanderersOfTheRift.MODID + ".cannot_unlock",
                "You must unlock the following to get this boost: ");
        add("ability." + WanderersOfTheRift.MODID + ".fireball", "Fireball");
        add("ability." + WanderersOfTheRift.MODID + ".firebolts", "Firebolts");
        add("ability." + WanderersOfTheRift.MODID + ".strength", "Strength");
        add("ability." + WanderersOfTheRift.MODID + ".weak_strength", "Strength (Exhausted)");
        add("ability." + WanderersOfTheRift.MODID + ".test_chain_ability", "Strength Chain");
        add("ability." + WanderersOfTheRift.MODID + ".icicles", "Icicles");
        add("ability." + WanderersOfTheRift.MODID + ".mega_boost", "Mega Boost");
        add("ability." + WanderersOfTheRift.MODID + ".dash", "Dash");
        add("ability." + WanderersOfTheRift.MODID + ".summon_skeletons", "Summon Skeletons");
        add("ability." + WanderersOfTheRift.MODID + ".test_ability", "Test Ability");
        add("ability." + WanderersOfTheRift.MODID + ".knockback", "Knockback");
        add("ability." + WanderersOfTheRift.MODID + ".pull", "Pull");
        add("ability." + WanderersOfTheRift.MODID + ".heal", "Heal");
        add("ability." + WanderersOfTheRift.MODID + ".firetouch", "Nonsense Experimental Ability");
        add("ability." + WanderersOfTheRift.MODID + ".veinminer", "Veinmine");

        add("ability." + WanderersOfTheRift.MODID + ".fire_breath", "Fire Breath");
        add("ability." + WanderersOfTheRift.MODID + ".poison_breath", "Poison Breath");
        add("ability." + WanderersOfTheRift.MODID + ".earth_breath", "Earth Breath");
        add("ability." + WanderersOfTheRift.MODID + ".lightning_breath", "Lightning Breath");
        add("ability." + WanderersOfTheRift.MODID + ".ice_breath", "Ice Breath");
        add("ability." + WanderersOfTheRift.MODID + ".fire_dart", "Fire Dart");
        add("ability." + WanderersOfTheRift.MODID + ".poison_dart", "Poison Dart");
        add("ability." + WanderersOfTheRift.MODID + ".earth_dart", "Earth Dart");
        add("ability." + WanderersOfTheRift.MODID + ".lightning_dart", "Lightning Dart");
        add("ability." + WanderersOfTheRift.MODID + ".ice_dart", "Ice Dart");
        add("ability." + WanderersOfTheRift.MODID + ".exploding_kittens", "Exploding Kittens");
        add("ability." + WanderersOfTheRift.MODID + ".feather_fall", "Feather Fall");
        add("ability." + WanderersOfTheRift.MODID + ".filthy_aura", "Filthy Aura");
        add("ability." + WanderersOfTheRift.MODID + ".group_hug", "Group Hug");
        add("ability." + WanderersOfTheRift.MODID + ".hook_shot", "Hook Shot");
        add("ability." + WanderersOfTheRift.MODID + ".levitate", "Levitate");
        add("ability." + WanderersOfTheRift.MODID + ".life_steal", "Life Steal");
        add("ability." + WanderersOfTheRift.MODID + ".mana_cat", "Mana Cat");
        add("ability." + WanderersOfTheRift.MODID + ".slime_wall", "Slime Wall");
        add("ability." + WanderersOfTheRift.MODID + ".squawk_strike", "Squawk Strike");
        add("ability." + WanderersOfTheRift.MODID + ".stab-stab-slash", "Stab-Stab-Slash");
        add("ability." + WanderersOfTheRift.MODID + ".teleport", "Teleport");
        add("ability." + WanderersOfTheRift.MODID + ".painful_sneak", "Painful Sneak");

        add("trigger." + WanderersOfTheRift.MODID + ".tick", "Tick");
        add("trigger." + WanderersOfTheRift.MODID + ".take_damage", "Take Damage");
        add("trigger." + WanderersOfTheRift.MODID + ".deal_damage", "Deal Damage");
        add("trigger." + WanderersOfTheRift.MODID + ".break_block", "Break Block");
        add("ability_conditions." + WanderersOfTheRift.MODID + ".fast_dash", "Fast Dash");

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

        add("tooltip." + WanderersOfTheRift.MODID + ".rift_key_generator_preset", "Generator Preset: %s");
        add("tooltip." + WanderersOfTheRift.MODID + ".rift_key_tier", "Tier: %s");
        add("tooltip." + WanderersOfTheRift.MODID + ".rift_key_theme", "Theme: %s");
        add("tooltip." + WanderersOfTheRift.MODID + ".rift_key_objective", "Objective: %s");
        add("tooltip." + WanderersOfTheRift.MODID + ".rift_key_parameter_entry", "%s: %s");
        add("tooltip." + WanderersOfTheRift.MODID + ".essence_value", "Essence: %s %s");
        add("tooltip." + WanderersOfTheRift.MODID + ".essence_header", "Essence:");
        add("tooltip." + WanderersOfTheRift.MODID + ".socket", "Sockets: ");
        add("tooltip." + WanderersOfTheRift.MODID + ".implicit", "Implicit: ");
        add("tooltip." + WanderersOfTheRift.MODID + ".empty_socket", "(Empty Slot)");
        add("tooltip." + WanderersOfTheRift.MODID + ".show_extra_info", "Hold [%s] for more info");
        add(WanderersOfTheRift.translationId("tooltip", "mana_bar"), "Mana: %s/%s");
        add(WanderersOfTheRift.translationId("tooltip", "rift_key_seed"), "Seed: %s");
        add(WanderersOfTheRift.translationId("tooltip", "runegem.shape"), "Shape: %s");
        add(WanderersOfTheRift.translationId("tooltip", "runegem.modifiers"), "Modifiers:");
        add(WanderersOfTheRift.translationId("tooltip", "tier"), "T%s");
        add(WanderersOfTheRift.translationId("tooltip", "currency_bag"), "Gain %s when consumed");
        add(WanderersOfTheRift.translationId("tooltip", "reward.reputation"), "%s Reputation");

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
        add(WanderersOfTheRift.translationId("ability_upgrade", "cooldown.description"), "Decreases Cooldown by 3%");
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
        add(WotrKeyMappings.GUILD_MENU_KEY.getName(), "Open Guild Menu");
        add(WotrKeyMappings.QUEST_MENU_KEY.getName(), "Open Quest Menu");
        add(WotrKeyMappings.WALLET_MENU_KEY.getName(), "Open Wallet Menu");

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
        add(WanderersOfTheRift.translationId("objective", "nothing.name"), "Nothing");
        add(WanderersOfTheRift.translationId("objective", "anomaly.name"), "Anomalies");
        add(WanderersOfTheRift.translationId("objective", "activate_block.name"), "Objective Blocks");
        add(WanderersOfTheRift.translationId("objective", "explore.name"), "Explore");
        add(WanderersOfTheRift.translationId("objective", "kill.description"), "Defeat %s monsters");
        add(WanderersOfTheRift.translationId("objective", "stealth.description"), "Defeat monsters stealthily");
        add(WanderersOfTheRift.translationId("objective", "nothing.description"), "Do nothing");
        add(WanderersOfTheRift.translationId("objective", "nothing.message"), "Survive and Escape");
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
        add(WanderersOfTheRift.translationId("attribute", "strength"), "Strength");
        add(WanderersOfTheRift.translationId("attribute", "dexterity"), "Dexterity");
        add(WanderersOfTheRift.translationId("attribute", "constitution"), "Constitution");
        add(WanderersOfTheRift.translationId("attribute", "intelligence"), "Intelligence");
        add(WanderersOfTheRift.translationId("attribute", "wisdom"), "Wisdom");
        add(WanderersOfTheRift.translationId("attribute", "charisma"), "Charisma");

        addRunegems();
        addModifiers();
        add(WanderersOfTheRift.translationId("modifier", "silk_touch_enchant"), "Silk Touch");
        add(WanderersOfTheRift.translationId("modifier", "fast_dash_condition"), "Fast Dash");

        add(WanderersOfTheRift.translationId("message", "disabled_in_rifts"), "Disabled in rifts");
        add(WanderersOfTheRift.translationId("message", "currency_obtained"), "Added %s %s to your wallet");
        add(WanderersOfTheRift.translationId("message", "quest_already_active"),
                "You must complete your existing quest before taking on another");

        add(WanderersOfTheRift.translationId("currency", "coin"), "Coin");

        add(WanderersOfTheRift.translationId("guild", "cats_cradle"), "Cats Cradle");
        add(WanderersOfTheRift.translationId("guild", "cats_cradle.rank.0"), "Damp Kitten");
        add(WanderersOfTheRift.translationId("guild", "cats_cradle.rank.1"), "Pawprentis");
        add(WanderersOfTheRift.translationId("guild", "cats_cradle.rank.2"), "Purrfessional");
        add(WanderersOfTheRift.translationId("guild", "cats_cradle.rank.3"), "Journeynyan");
        add(WanderersOfTheRift.translationId("guild", "cats_cradle.rank.4"), "Furrmidable");
        add(WanderersOfTheRift.translationId("guild", "cats_cradle.rank.5"), "Meowster");
        add(WanderersOfTheRift.translationId("guild", "cats_cradle.rank.6"), "Gwand Meowster");

        add(WanderersOfTheRift.translationId("guild", "parrots_perch"), "Parrots Perch");
        add(WanderersOfTheRift.translationId("guild", "parrots_perch.rank.0"), "Blue Egg");
        add(WanderersOfTheRift.translationId("guild", "parrots_perch.rank.1"), "Beakginner");
        add(WanderersOfTheRift.translationId("guild", "parrots_perch.rank.2"), "Pollyficient");
        add(WanderersOfTheRift.translationId("guild", "parrots_perch.rank.3"), "Wingcredible");
        add(WanderersOfTheRift.translationId("guild", "parrots_perch.rank.4"), "Featheran");
        add(WanderersOfTheRift.translationId("guild", "parrots_perch.rank.5"), "Talonted");
        add(WanderersOfTheRift.translationId("guild", "parrots_perch.rank.6"), "Feathery Talonted");

        add(WanderersOfTheRift.translationId("npc", "default"), "Bailey");
        add(WanderersOfTheRift.translationId("npc", "cats_cradle_merchant"), "Cats Cradle Merchant");
        add(WanderersOfTheRift.translationId("npc", "cats_cradle_quest_giver"), "Cats Cradle Quest Giver");
        add(WanderersOfTheRift.translationId("npc", "parrots_perch_merchant"), "Parrots Perch Merchant");
        add(WanderersOfTheRift.translationId("npc", "parrots_perch_quest_giver"), "Parrots Perch Quest Giver");

        add(WanderersOfTheRift.translationId("goal", "rift.attempt"), "Attempt");
        add(WanderersOfTheRift.translationId("goal", "rift.survive"), "Survive");
        add(WanderersOfTheRift.translationId("goal", "rift.complete"), "Complete");
        add(WanderersOfTheRift.translationId("goal", "rift.tier"), "tier %s");
        add(WanderersOfTheRift.translationId("goal", "any"), "any");
        add(WanderersOfTheRift.translationId("goal", "mobs"), "mobs");

        add(WanderersOfTheRift.translationId("quest", "fetch_quest.title"), "Fetch Quest");
        add(WanderersOfTheRift.translationId("quest", "fetch_quest.description"),
                "Bring me what I need and I'll make it worth your while.");
        add(WanderersOfTheRift.translationId("quest", "skillthread.title"), "Deliver Skill Thread");
        add(WanderersOfTheRift.translationId("quest", "skillthread.description"),
                "I've heard there is a strange thread that can be found in the rifts that holds the key to unlocking the full potential of abilities. Could you bring me a sample?");
        add(WanderersOfTheRift.translationId("quest", "gold_and_iron.title"), "Sample the Wares");
        add(WanderersOfTheRift.translationId("quest", "gold_and_iron.description"),
                "Psst. You look like a discerning customer? I've got some powerful shiny baubles if you can bring me some precious metals!");
        add(WanderersOfTheRift.translationId("quest", "kill_skeletons.title"), "Defeat Skeletons");
        add(WanderersOfTheRift.translationId("quest", "kill_skeletons.description"), "Bones scare me! Save me!");
        add(WanderersOfTheRift.translationId("quest", "complete_rift.title"), "Complete Rifts");
        add(WanderersOfTheRift.translationId("quest", "complete_rift.description"), "Prove your mettle.");
        add(WanderersOfTheRift.translationId("quest", "bring_big_fish.title"), "Prove your value");
        add(WanderersOfTheRift.translationId("quest", "bring_big_fish.description"),
                "I'm starving. Could you purlease bring me some fish?");
        add(WanderersOfTheRift.translationId("quest", "bring_fish.title"), "Fish of the day");
        add(WanderersOfTheRift.translationId("quest", "bring_fish.description"), "Purlease could I have another fish?");
        add(WanderersOfTheRift.translationId("quest", "deliver_fish.title"), "A special delivery");
        add(WanderersOfTheRift.translationId("quest", "deliver_fish.description"),
                "The Cats Cradle merchant has been working very hard lately, I worry they're skipping lunch. Could you bring them a fish for me?");
        add(WanderersOfTheRift.translationId("quest", "complete_noir.title"), "A stitch through time");
        add(WanderersOfTheRift.translationId("quest", "complete_noir.description"),
                "Please help find the fedora I lost in that black-and-white rift. It is of purrticular sentimental value.");
        add(WanderersOfTheRift.translationId("quest", "complete_color.title"), "Inspire the Hatter");
        add(WanderersOfTheRift.translationId("quest", "complete_color.description"),
                "The hatter is feeling uninspired. Show them the beauty of colors found in the rifts.");

        add(WanderersOfTheRift.translationId("quest", "lost_brother_1.title"), "Lost Brother 1");
        add(WanderersOfTheRift.translationId("quest", "lost_brother_1.description"),
                "I can't find my brother anywhere! He went into a rift a while ago and never came back. Could you help me find him?");
        add(WanderersOfTheRift.translationId("quest", "lost_brother_2.title"), "Lost Brother 2");
        add(WanderersOfTheRift.translationId("quest", "lost_brother_2.description"),
                "Maybe he went into a different rift? He likes to play in the sand. Could you check a sandy rift for me?");
        add(WanderersOfTheRift.translationId("quest", "lost_brother_3.title"), "Lost Brother 3");
        add(WanderersOfTheRift.translationId("quest", "lost_brother_3.description"),
                "You have not found him there either? Perhaps try a warmer rift, he always loved the heat.");
        add(WanderersOfTheRift.translationId("quest", "lost_brother_4.title"), "Lost Brother 4");
        add(WanderersOfTheRift.translationId("quest", "lost_brother_4.description"),
                "Not there either? Oh dear. He always loved flowers, maybe he wandered into a flowery rift and ran into some bees. Could you check there?");
        add(WanderersOfTheRift.translationId("quest", "lost_brother_5.title"), "Lost Brother 5");
        add(WanderersOfTheRift.translationId("quest", "lost_brother_5.description"),
                "Ohh silly me, I don't even have a brother! Well, after all that rift exploring, I think we've earned a good rest, don't you?");

        add(WanderersOfTheRift.translationId("quest", "choose_cat.title"), "Meow or never");
        add(WanderersOfTheRift.translationId("quest", "choose_cat.description"),
                "Show that you are one of us, and not a filthy flyer. Bring me a feather to show your loyalty, and I shall reward you generously.");
        add(WanderersOfTheRift.translationId("quest", "choose_bird.title"), "Parrots Pact");
        add(WanderersOfTheRift.translationId("quest", "choose_bird.description"),
                "Show ye’ve got the wings for the skies, not the paws for the alleys! Fetch me a string from those whiskered land-lubbers, and I’ll reward ye with treasures!");

        add(WanderersOfTheRift.translationId("quest", "fetch_quest_parrot.title"), "Fetch Quest");
        add(WanderersOfTheRift.translationId("quest", "fetch_quest_parrot.description"),
                "Arr! Bring me what I need and I'll shower ye with riches.");
        add(WanderersOfTheRift.translationId("quest", "gold_and_iron_parrot.title"), "Sample the Wares");
        add(WanderersOfTheRift.translationId("quest", "gold_and_iron_parrot.description"),
                "Psst! Got an aye for treasure? Fetch me some glitterin' metals from the rifts, and I'll trade ye some shiny baubles!");
        add(WanderersOfTheRift.translationId("quest", "kill_skeletons_parrot.title"), "Defeat Skeletons");
        add(WanderersOfTheRift.translationId("quest", "kill_skeletons_parrot.description"),
                "Eek! Ah, it's you. Help me fend off these bony fiends!");
        add(WanderersOfTheRift.translationId("quest", "complete_rift_parrot.title"), "Complete Rifts");
        add(WanderersOfTheRift.translationId("quest", "complete_rift_parrot.description"),
                "Chart a course through the rift and claim the spoils! Show ye’ve got the courage to sail where few dare!");

        add(WanderersOfTheRift.translationId("quest", "parrot_ability_1.title"), "Hidden Treasure 1");
        add(WanderersOfTheRift.translationId("quest", "parrot_ability_1.description"),
                "Ahoy matey! See this red cross on me map? Aye, this here marks the spot of treasure! Go sail to the rift and come back with what ye find!");
        add(WanderersOfTheRift.translationId("quest", "parrot_ability_2.title"), "Hidden Treasure 2");
        add(WanderersOfTheRift.translationId("quest", "parrot_ability_2.description"),
                "Back for more, eh? Ye be a persistent one! This place might be hot, but the treasure be worth it. Set sail again and bring me back another bounty!");
        add(WanderersOfTheRift.translationId("quest", "parrot_ability_3.title"), "Hidden Treasure 3");
        add(WanderersOfTheRift.translationId("quest", "parrot_ability_3.description"),
                "Ye be on a roll, matey! This next spot be teeming with riches, but we aint the first to arrive. Snatch the loot before them bees do!");

        add(WanderersOfTheRift.translationId("quest", "parrot_main_1.title"), "Join the crew");
        add(WanderersOfTheRift.translationId("quest", "parrot_main_1.description"),
                "Ahoy there! Fancy joining the Parrots Perch, do ye? Step into a rift and let the skies judge yer courage");
        add(WanderersOfTheRift.translationId("quest", "parrot_main_2.title"), "Forge your fortune");
        add(WanderersOfTheRift.translationId("quest", "parrot_main_2.description"),
                "Time to toughen yer talons, matey! Fetch me an anvil, and I’ll craft ye a bench fit for a wanderer to sharpen their steel!");
        add(WanderersOfTheRift.translationId("quest", "parrot_main_3.title"), "Wings of war");
        add(WanderersOfTheRift.translationId("quest", "parrot_main_3.description"),
                "Aye, now yer ready for battle. Prove yer mettle by defeating 20 foes in the rifts. Show 'em the might of the Parrots Perch!");
        add(WanderersOfTheRift.translationId("quest", "parrot_main_4.title"), "Keys to the skies");
        add(WanderersOfTheRift.translationId("quest", "parrot_main_4.description"),
                "Well done, matey! Now, to truly soar, ye need a better rift key. Bring me a gilded key, and I’ll upgrade yer own to unlock grander adventures!");
        add(WanderersOfTheRift.translationId("quest", "parrot_main_5.title"), "Aspiring Admiral");
        add(WanderersOfTheRift.translationId("quest", "parrot_main_5.description"),
                "Now ye be a true Parrots Perch member! Show yer dedication by completing 3 rifts. The skies await yer legend!");

        add(WanderersOfTheRift.translationId("quest", "peace_treaty.title"), "Peace tweety");
        add(WanderersOfTheRift.translationId("quest", "peace_treaty.description"),
                "Arr! Seems we got off on the wrong claw.. paw.. whatever ye call it!. Them four-legged furballs and us feathered friends should be allies, not enemies. Show em we mean well");

        add("mobgroup.minecraft.skeletons", "Skeletons");
        add("mobgroup.wotr.rift_monsters", "Rift Monsters");
        add("modifier.wotr.projectile_count", "Projectile Count");
        add("modifier.wotr.projectile_pierce", "Projectile Pierce");
        add("modifier_effect.wotr.ability", "Cast %s when %s");

        add(Append.TYPE.translationKey(), "Add %s");
        add(Prepend.TYPE.translationKey(), "Add %s at start");
        add(Clear.TYPE.translationKey(), "Remove all");
        add(Drop.TYPE.translationKey(), "Remove %s from start");
        add(DropLast.TYPE.translationKey(), "Remove %s");
        add(WotrRiftLayoutLayers.PREDEFINED_LAYER.getKey().location().toLanguageKey("layout_layer"), "%s Room");
        add(WotrRiftLayoutLayers.RING_LAYER.getKey().location().toLanguageKey("layout_layer"), "Ring of %s Rooms");
        add(WotrRiftLayoutLayers.BOXED_LAYER.getKey().location().toLanguageKey("layout_layer"), "Room group");
        add(WotrRiftLayoutLayers.CHAOS_LAYER.getKey().location().toLanguageKey("layout_layer"), "%s Rooms");
        add("template_pool.wotr.rift.room_portal", "Portal");
        add("template_pool.wotr.rift.room_stable", "Stable");
        add("template_pool.wotr.rift.room_unstable", "Unstable");
        add("template_pool.wotr.rift.room_chaos", "Chaos");

        add(WanderersOfTheRift.translationId("toast", "quest.complete"), "Quest Complete");
        add(WanderersOfTheRift.translationId("toast", "guild.rank"), "Guild Rank Up");

        add(WanderersOfTheRift.translationId("anomaly", "needle"), "Needle");
        add(WanderersOfTheRift.translationId("anomaly", "battle"), "Battle");
        add(WanderersOfTheRift.translationId("anomaly", "bundle"), "Bundle");
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

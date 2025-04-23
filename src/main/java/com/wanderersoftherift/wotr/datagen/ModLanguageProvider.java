package com.wanderersoftherift.wotr.datagen;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.init.ModBlocks;
import com.wanderersoftherift.wotr.init.ModEntities;
import com.wanderersoftherift.wotr.init.ModItems;
import com.wanderersoftherift.wotr.init.client.ModKeybinds;
import com.wanderersoftherift.wotr.item.essence.EssenceValue;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.LanguageProvider;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

/* Handles Data Generation for I18n of the locale 'en_us' of the Wotr mod */
public class ModLanguageProvider extends LanguageProvider {

    public ModLanguageProvider(PackOutput output) {
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
        addBlock(ModBlocks.DEV_BLOCK, "Dev Block");
        addBlock(ModBlocks.RUNE_ANVIL_ENTITY_BLOCK, "Rune Anvil");
        addBlock(ModBlocks.RIFT_CHEST, "Rift Chest");
        addBlock(ModBlocks.RIFT_SPAWNER, "Rift Spawner");
        addBlock(ModBlocks.KEY_FORGE, "Key Forge");
        addBlock(ModBlocks.DITTO_BLOCK, "Ditto Block");
        addBlock(ModBlocks.SPRING_BLOCK, "Spring Block");
        addBlock(ModBlocks.TRAP_BLOCK, "Trap Block");
        addBlock(ModBlocks.PLAYER_TRAP_BLOCK, "Player Trap Block");
        addBlock(ModBlocks.MOB_TRAP_BLOCK, "Mob Trap Block");
        addBlock(ModBlocks.ABILITY_BENCH, "Ability Bench");

        // Adds an item translation.
        addItem(ModItems.EXAMPLE_ITEM, "Example Item");
        addItem(ModItems.BUILDER_GLASSES, "Builder Glasses");
        addItem(ModItems.RUNEGEM, "Runegem");
        addItem(ModItems.RIFT_KEY, "Rift Key");
        addItem(ModItems.RUNEGEM_GEODE, "Runegem Geode");
        addItem(ModItems.ABILITY_HOLDER, "Empty Ability");
        addItem(ModItems.SKILL_THREAD, "Skill Thread");

        addEntityType(ModEntities.RIFT_ENTRANCE, "Rift Entrance");
        addEntityType(ModEntities.SIMPLE_EFFECT_PROJECTILE, "Projectile");

        addEssenceType("earth", "Earth");
        addEssenceType("life", "Life");
        addEssenceType("water", "Water");
        addEssenceType("meat", "Meat");

        ModBlocks.BLOCK_FAMILY_HELPERS.forEach(helper -> {
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
        add("block." + WanderersOfTheRift.MODID + ".processor_block_8", "Processor Block 8");
        add("block." + WanderersOfTheRift.MODID + ".processor_block_9", "Processor Block 9");
        add("block." + WanderersOfTheRift.MODID + ".processor_block_10", "Processor Block 10");
        add("block." + WanderersOfTheRift.MODID + ".processor_block_11", "Processor Block 11");
        add("block." + WanderersOfTheRift.MODID + ".processor_block_12", "Processor Block 12");
        add("block." + WanderersOfTheRift.MODID + ".processor_block_13", "Processor Block 13");
        add("block." + WanderersOfTheRift.MODID + ".processor_block_14", "Processor Block 14");

        // Adds a generic translation
        add("itemGroup." + WanderersOfTheRift.MODID, "Wanderers of the Rift");

        add("item." + WanderersOfTheRift.MODID + ".rift_key.themed", "Rift Key of %s");

        add("container." + WanderersOfTheRift.MODID + ".rune_anvil", "Rune Anvil");
        add("container." + WanderersOfTheRift.MODID + ".rune_anvil.apply", "Apply");
        add("container." + WanderersOfTheRift.MODID + ".rift_chest", "Rift Chest");
        add("container." + WanderersOfTheRift.MODID + ".key_forge", "Key Forge");
        add("container." + WanderersOfTheRift.MODID + ".ability_bench", "Ability Bench");

        add("container." + WanderersOfTheRift.MODID + ".ability_bench.upgrade", "Upgrades");
        add("container." + WanderersOfTheRift.MODID + ".ability_bench.unlock", "Unlock next choice");

        add("command." + WanderersOfTheRift.MODID + ".dev_world_set",
                "Dev World settings applied:\n - %1$s: Disabled\n - %2$s: Disabled\n - %3$s: Disabled\n - %4$s: Disabled\n - %5$s: Disabled\n - %6$s: Disabled");
        add("command." + WanderersOfTheRift.MODID + ".invalid_item", "Held item is empty!");
        add("command." + WanderersOfTheRift.MODID + ".invalid_player", "Player is null!");
        add("command." + WanderersOfTheRift.MODID + ".get_item_stack_components.success",
                "Item Components available for '%1$s'");
        add("command." + WanderersOfTheRift.MODID + ".rift_key.theme.success", "Rift key theme set to '%1$s'");
        add("command." + WanderersOfTheRift.MODID + ".rift_key.tier.success", "Rift key tier set to '%1$s'");
        add("command." + WanderersOfTheRift.MODID + ".rift_key.seed.success", "Rift key seed set to '%1$s'");
        add("command." + WanderersOfTheRift.MODID + ".rift_key.invalid_item", "You must hold a rift key in your hand!");

        add("ability." + WanderersOfTheRift.MODID + ".cannot_unlock",
                "You must unlock the following to get this boost: ");
        add("ability." + WanderersOfTheRift.MODID + ".fireball_ability", "Fireball");
        add("ability." + WanderersOfTheRift.MODID + ".mega_boost", "Mega Boost");
        add("ability." + WanderersOfTheRift.MODID + ".dash", "Dash");
        add("ability." + WanderersOfTheRift.MODID + ".summon_skeletons", "Summon Skeletons");
        add("ability." + WanderersOfTheRift.MODID + ".test_ability", "Test Ability");
        add("ability." + WanderersOfTheRift.MODID + ".knockback", "Knockback");
        add("ability." + WanderersOfTheRift.MODID + ".pull", "Pull");
        add("ability." + WanderersOfTheRift.MODID + ".heal", "Heal");
        add("ability." + WanderersOfTheRift.MODID + ".firetouch", "Nonsense Experimental Ability");

        add(WanderersOfTheRift.translationId("effect_marker", "fireshield"), "Fire Shield");

        add("accessibility." + WanderersOfTheRift.MODID + ".screen.title", "Dimension Delvers: Accessibility Settings");
        add("accessibility." + WanderersOfTheRift.MODID + ".menubutton", "DimDelvers Accessibility (tmp)");
        add("accessibility." + WanderersOfTheRift.MODID + ".screen.trypophobia", "Trypophobia");
        add("accessibility." + WanderersOfTheRift.MODID + ".screen.arachnophobia", "Arachnophobia");
        add("accessibility." + WanderersOfTheRift.MODID + ".screen.flashing_lights", "Flashing Lights");
        add("accessibility." + WanderersOfTheRift.MODID + ".screen.misophonia", "Misophonia");
        add("accessibility." + WanderersOfTheRift.MODID + ".screen.high_contrast", "High Contrast");
        add("accessibility." + WanderersOfTheRift.MODID + ".screen.hard_of_hearing", "Hard of Hearing");
        add("accessibility." + WanderersOfTheRift.MODID + ".screen.reduced_motion", "Reduced Motion");
        add("accessibility." + WanderersOfTheRift.MODID + ".screen.tooltip.trypophobia",
                "Removes any trypophobia-triggering aspects");
        add("accessibility." + WanderersOfTheRift.MODID + ".screen.tooltip.arachnophobia",
                "Replaces all the spiders with cute cats!");
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

        add("command." + WanderersOfTheRift.MODID + ".spawn_piece.generating", "Generating %s");
        add(WanderersOfTheRift.translationId("command", "make_ability_item.success"), "Applied ability components");

        add("tooltip." + WanderersOfTheRift.MODID + ".rift_key_tier", "Rift Tier: %s");
        add("tooltip." + WanderersOfTheRift.MODID + ".essence_value", "Essence: %s %s");
        add("tooltip." + WanderersOfTheRift.MODID + ".essence_header", "Essence:");
        add("tooltip." + WanderersOfTheRift.MODID + ".socket", "Sockets: ");
        add("tooltip." + WanderersOfTheRift.MODID + ".implicit-", "Implicit: ");
        add(WanderersOfTheRift.translationId("tooltip", "mana_bar"), "Mana: %s/%s");

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

        add(ModKeybinds.ABILITY_CATEGORY, "Abilities");
        add(ModKeybinds.ABILITY_1_KEY.getName(), "Use Ability 1");
        add(ModKeybinds.ABILITY_2_KEY.getName(), "Use Ability 2");
        add(ModKeybinds.ABILITY_3_KEY.getName(), "Use Ability 3");
        add(ModKeybinds.ABILITY_4_KEY.getName(), "Use Ability 4");
        add(ModKeybinds.ABILITY_5_KEY.getName(), "Use Ability 5");
        add(ModKeybinds.ABILITY_6_KEY.getName(), "Use Ability 6");
        add(ModKeybinds.ABILITY_7_KEY.getName(), "Use Ability 7");
        add(ModKeybinds.ABILITY_8_KEY.getName(), "Use Ability 8");
        add(ModKeybinds.ABILITY_9_KEY.getName(), "Use Ability 9");
        add(ModKeybinds.PREV_ABILITY_KEY.getName(), "Select Previous Ability");
        add(ModKeybinds.NEXT_ABILITY_KEY.getName(), "Select Next Ability");
        add(ModKeybinds.USE_ABILITY_KEY.getName(), "Use Selected Ability");

        add(WanderersOfTheRift.translationId("keybinds", "l_alt"), "LAlt");
        add(WanderersOfTheRift.translationId("keybinds", "r_alt"), "RAlt");
        add(WanderersOfTheRift.translationId("keybinds", "l_ctrl"), "LCtrl");
        add(WanderersOfTheRift.translationId("keybinds", "r_ctrl"), "RCtrl");
        add(WanderersOfTheRift.translationId("keybinds", "mod_alt"), "Alt+");
        add(WanderersOfTheRift.translationId("keybinds", "mod_ctrl"), "Ctrl+");
        add(WanderersOfTheRift.translationId("keybinds", "mod_shift"), "Shi+");
    }

    private void addEssenceType(String id, String value) {
        add(EssenceValue.ESSENCE_TYPE_PREFIX + "." + WanderersOfTheRift.MODID + "." + id, value);
    }

    private static @NotNull String getTranslationString(Block block) {
        String idString = BuiltInRegistries.BLOCK.getKey(block).getPath();
        StringBuilder sb = new StringBuilder();
        for (String word : idString.toLowerCase(Locale.ROOT).split("_")) {
            sb.append(word.substring(0, 1).toUpperCase(Locale.ROOT));
            sb.append(word.substring(1));
            sb.append(" ");
        }
        return sb.toString().trim();
    }
}

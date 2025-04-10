package com.wanderersoftherift.wotr.datagen;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.init.ModBlocks;
import com.wanderersoftherift.wotr.init.ModEntityTypes;
import com.wanderersoftherift.wotr.init.ModItems;
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

        // Adds an item translation.
        addItem(ModItems.EXAMPLE_ITEM, "Example Item");
        addItem(ModItems.BUILDER_GLASSES, "Builder Glasses");
        addItem(ModItems.RUNEGEM, "Runegem");
        addItem(ModItems.RIFT_KEY, "Rift Key");
        addItem(ModItems.RUNEGEM_GEODE, "Runegem Geode");

        addEntityType(ModEntityTypes.RIFT_ENTRANCE, "Rift Entrance");

        addEssenceType("earth", "Earth");
        addEssenceType("life", "Life");
        addEssenceType("water", "Water");
        addEssenceType("meat", "Meat");

        ModBlocks.BLOCK_FAMILY_HELPERS.forEach(helper -> {
            //addBlock(helper.getBlock(), getTranslationString(helper.getBlock().get()));
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

        add("command." + WanderersOfTheRift.MODID + ".dev_world_set", "Dev World settings applied:\n - %1$s: Disabled\n - %2$s: Disabled\n - %3$s: Disabled\n - %4$s: Disabled\n - %5$s: Disabled\n - %6$s: Disabled");
        add("command." + WanderersOfTheRift.MODID + ".invalid_item", "Held item is empty!");
        add("command." + WanderersOfTheRift.MODID + ".get_item_stack_components.invalid_player", "Player is null!");
        add("command." + WanderersOfTheRift.MODID + ".get_item_stack_components.success", "Item Components available for '%1$s'");

        add("accessibility." + WanderersOfTheRift.MODID + ".screen.title", "Dimension Delvers: Accessibility Settings");
        add("accessibility." + WanderersOfTheRift.MODID + ".menubutton", "DimDelvers Accessibility (tmp)");
        add("accessibility." + WanderersOfTheRift.MODID + ".screen.trypophobia", "Trypophobia");
        add("accessibility." + WanderersOfTheRift.MODID + ".screen.arachnophobia", "Arachnophobia");
        add("accessibility." + WanderersOfTheRift.MODID + ".screen.flashing_lights", "Flashing Lights");
        add("accessibility." + WanderersOfTheRift.MODID + ".screen.misophonia", "Misophonia");
        add("accessibility." + WanderersOfTheRift.MODID + ".screen.high_contrast", "High Contrast");
        add("accessibility." + WanderersOfTheRift.MODID + ".screen.hard_of_hearing", "Hard of Hearing");
        add("accessibility." + WanderersOfTheRift.MODID + ".screen.reduced_motion", "Reduced Motion");
        add("accessibility." + WanderersOfTheRift.MODID + ".screen.tooltip.trypophobia", "Removes any trypophobia-triggering aspects");
        add("accessibility." + WanderersOfTheRift.MODID + ".screen.tooltip.arachnophobia", "Replaces all the spiders with cute cats!");
        add("accessibility." + WanderersOfTheRift.MODID + ".screen.tooltip.flashing_lights", "Reduces flashing-light effects");
        add("accessibility." + WanderersOfTheRift.MODID + ".screen.tooltip.misophonia", "Replaces certain sounds that are potentially triggering with different ones (?)");
        add("accessibility." + WanderersOfTheRift.MODID + ".screen.tooltip.high_contrast", "Enhances UI and HUD elements with higher contrast for better visibility");
        add("accessibility." + WanderersOfTheRift.MODID + ".screen.tooltip.hard_of_hearing", "Enhances audio cues for better accessibility");
        add("accessibility." + WanderersOfTheRift.MODID + ".screen.tooltip.reduced_motion", "Disables or slows down UI animations, camera shake, or screen effects");

        add("command." + WanderersOfTheRift.MODID + ".spawn_piece.generating", "Generating %s");

        add("tooltip." + WanderersOfTheRift.MODID + ".rift_key_tier", "Rift Tier: %s");
        add("tooltip." + WanderersOfTheRift.MODID + ".essence_value", "Essence: %s %s");
        add("tooltip." + WanderersOfTheRift.MODID + ".essence_header", "Essence:");
        add("tooltip." + WanderersOfTheRift.MODID + ".socket", "Sockets: ");

        add("subtitles." + WanderersOfTheRift.MODID + ".rift_open", "Rift Opens");

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

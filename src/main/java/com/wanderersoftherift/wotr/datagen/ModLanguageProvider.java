package com.wanderersoftherift.wotr.datagen;

import com.mojang.datafixers.optics.Wander;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.init.ModBlocks;
import com.wanderersoftherift.wotr.init.ModItems;
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
        addBlock(ModBlocks.RUNE_ANVIL_BLOCK, "Rune Anvil");
        addBlock(ModBlocks.RIFT_CHEST, "Rift Chest");

        // Adds an item translation.
        addItem(ModItems.EXAMPLE_ITEM, "Example Item");
        addItem(ModItems.RUNEGEM, "Runegem");

        ModBlocks.BLOCK_FAMILY_HELPERS.forEach(helper -> {
            addBlock(helper.getBlock(), getTranslationString(helper.getBlock().get()));
            helper.getVariants().forEach((variant, block) -> addBlock(block, getTranslationString(block.get())));
        });


        // Adds a generic translation
        add("itemGroup." + WanderersOfTheRift.MODID, "Dimension Delvers");

        add("container." + WanderersOfTheRift.MODID + ".rune_anvil", "Rune Anvil");
        add("container." + WanderersOfTheRift.MODID + ".rift_chest", "Rift Chest");

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

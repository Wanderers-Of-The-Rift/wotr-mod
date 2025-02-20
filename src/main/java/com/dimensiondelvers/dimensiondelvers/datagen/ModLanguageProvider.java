package com.dimensiondelvers.dimensiondelvers.datagen;

import com.dimensiondelvers.dimensiondelvers.DimensionDelvers;
import com.dimensiondelvers.dimensiondelvers.init.ModBlocks;
import com.dimensiondelvers.dimensiondelvers.init.ModItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

/* Handles Data Generation for I18n of the locale 'en_us' of the DimensionDelvers mod */
public class ModLanguageProvider extends LanguageProvider {
    public ModLanguageProvider(PackOutput output) {
        super(output, DimensionDelvers.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        // Helpers are available for various common object types. Every helper has two variants: an add() variant
        // for the object itself, and an addTypeHere() variant that accepts a supplier for the object.
        // The different names for the supplier variants are required due to generic type erasure.
        // All following examples assume the existence of the values as suppliers of the needed type.
        // See https://docs.neoforged.net/docs/1.21.1/resources/client/i18n/ for translation of other types.

        // Adds a block translation.
        addBlock(ModBlocks.EXAMPLE_BLOCK, "Example Block");
        addBlock(ModBlocks.DEV_BLOCK, "Dev Block");
        addBlock(ModBlocks.RUNE_ANVIL_ENTITY_BLOCK, "Rune Anvil");
        addBlock(ModBlocks.RIFT_CHEST, "Rift Chest");

        // Adds an item translation.
        addItem(ModItems.EXAMPLE_ITEM, "Example Item");
        addItem(ModItems.RUNEGEM, "Runegem");


        // Adds a generic translation
        add("itemGroup.dimensiondelvers", "Dimension Delvers");

        add("container.dimensiondelvers.rune_anvil", "Rune Anvil");
        add("container.dimensiondelvers.rune_anvil.apply", "Apply");
        add("tooltip.dimensiondelvers.socket", "Sockets: ");
        add("container.dimensiondelvers.rift_chest", "Rift Chest");

        add("accessibility.dimensiondelvers.screen.title", "Dimension Delvers: Accessibility Settings");
        add("accessibility.dimensiondelvers.menubutton", "DimDelvers Accessibility (tmp)");
        add("accessibility.dimensiondelvers.screen.trypophobia", "Trypophobia");
        add("accessibility.dimensiondelvers.screen.arachnophobia", "Arachnophobia");
        add("accessibility.dimensiondelvers.screen.flashing_lights", "Flashing Lights");
        add("accessibility.dimensiondelvers.screen.misophonia", "Misophonia");
        add("accessibility.dimensiondelvers.screen.high_contrast", "High Contrast");
        add("accessibility.dimensiondelvers.screen.hard_of_hearing", "Hard of Hearing");
        add("accessibility.dimensiondelvers.screen.reduced_motion", "Reduced Motion");
        add("accessibility.dimensiondelvers.screen.tooltip.trypophobia", "Removes any trypophobia-triggering aspects");
        add("accessibility.dimensiondelvers.screen.tooltip.arachnophobia", "Replaces all the spiders with cute cats!");
        add("accessibility.dimensiondelvers.screen.tooltip.flashing_lights", "Reduces flashing-light effects");
        add("accessibility.dimensiondelvers.screen.tooltip.misophonia", "Replaces certain sounds that are potentially triggering with different ones (?)");
        add("accessibility.dimensiondelvers.screen.tooltip.high_contrast", "Enhances UI and HUD elements with higher contrast for better visibility");
        add("accessibility.dimensiondelvers.screen.tooltip.hard_of_hearing", "Enhances audio cues for better accessibility");
        add("accessibility.dimensiondelvers.screen.tooltip.reduced_motion", "Disables or slows down UI animations, camera shake, or screen effects");
    }
}

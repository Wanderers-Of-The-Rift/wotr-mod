package com.wanderersoftherift.wotr.datagen.book.wotr.intro;

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookCraftingRecipePageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.mojang.datafixers.util.Pair;
import com.wanderersoftherift.wotr.init.WotrBlocks;

public class RiftKeyForgeEntry extends EntryProvider {
    public static final String ID = "rift_key_forge";

    public RiftKeyForgeEntry(CategoryProvider parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro",
                () -> BookTextPageModel.create()
                        .withTitle(this.context().pageTitle())
                        .withText(this.context().pageText())
        );
        this.pageTitle("Rift Key Forge");
        this.pageText("""
                Your first goal as you set out on your journey should be to craft a Rift Key Forge.
                This table can be used to create Rift Keys using common materials you can find all around you.
                """);

        this.page("crafting",
                () -> BookCraftingRecipePageModel.create()
                        .withRecipeId1("wotr:key_forge")
                        .withText(this.context().pageText())
                        .withTitle2("Recipe")
        );
        this.pageText("Your first step on your journey!");
        this.add("Recipe", "Rift Key Forge");
    }

    @Override
    protected String entryName() {
        return "Rift Key Forge";
    }

    @Override
    protected String entryDescription() {
        return "Rift Key Forge recipe and information.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(WotrBlocks.KEY_FORGE);
    }

    @Override
    protected String entryId() {
        return ID;
    }
}

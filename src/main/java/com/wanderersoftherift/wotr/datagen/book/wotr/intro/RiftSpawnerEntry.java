package com.wanderersoftherift.wotr.datagen.book.wotr.intro;

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookCraftingRecipePageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.mojang.datafixers.util.Pair;
import com.wanderersoftherift.wotr.init.WotrBlocks;

public class RiftSpawnerEntry extends EntryProvider {
    public static final String ID = "rift_spawner";

    public RiftSpawnerEntry(CategoryProvider parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro",
                () -> BookTextPageModel.create()
                        .withTitle(this.context().pageTitle())
                        .withText(this.context().pageText())
        );
        this.pageTitle("Rift Spawner");
        this.pageText("""
                You're well on your way to your first rift! Next you will need to craft a Rift Spawner.
                """);

        this.page("crafting",
                () -> BookCraftingRecipePageModel.create()
                        .withRecipeId1("wotr:rift_spawner")
                        .withText(this.context().pageText())
                        .withTitle2("Recipe 2")
        );
        this.pageText("Keep it up!");
        this.add("Recipe 2", "Rift Spawner");
    }

    @Override
    protected String entryName() {
        return "Rift Spawner";
    }

    @Override
    protected String entryDescription() {
        return "Rift Spawner recipe and information.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(WotrBlocks.RIFT_SPAWNER);
    }

    @Override
    protected String entryId() {
        return ID;
    }
}

package com.wanderersoftherift.wotr.datagen.book.wotr.intro;

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookEmptyPageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.item.Items;

public class WelcomeEntry extends EntryProvider {
    public static final String ID = "welcome";

    public WelcomeEntry(CategoryProvider parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText())
        );
        this.pageTitle("Welcome to WotR!");
        this.pageText("""
                Welcome to Wanderers of the Rift! Stay a while, we've got **notorized fish**!\s
                """);

        this.context().page("empty");
        var empty = BookEmptyPageModel.create();
    }

    @Override
    protected String entryName() {
        return "Welcome to WotR";
    }

    @Override
    protected String entryDescription() {
        return "Enjoy your stay!";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.OAK_HANGING_SIGN);
    }

    @Override
    protected String entryId() {
        return ID;
    }
}

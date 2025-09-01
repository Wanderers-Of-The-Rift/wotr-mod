package com.wanderersoftherift.wotr.datagen.book.wotr.rift;

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookSpotlightPageModel;
import com.mojang.datafixers.util.Pair;
import com.wanderersoftherift.wotr.init.WotrItems;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

public class LootEntry extends EntryProvider {
    public static final String ID = "loot";

    public LootEntry(CategoryProvider parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro",
                () -> BookSpotlightPageModel.create()
                        .withTitle(this.context().pageTitle())
                        .withText(this.context().pageText())
                        .withItem(Items.CHEST)
        );
        this.pageTitle("Loot!");
        this.pageText("""
                Throughout the rift you will find a number of chests that contain loot within them.\\
                \\
                The next few pages will highlight ones to pay attention to.\s
                """
        );

        this.page("loot1",
                () -> BookSpotlightPageModel.create()
                        .withTitle(this.context().pageTitle())
                        .withText(this.context().pageText())
                        .withItem(Ingredient.of(WotrItems.RAW_RUNEGEM_GEODE))
        );
        this.pageTitle("Runegem Geodes");
        this.pageText(
                "Runegem geodes can be cracked open by using them to reveal one of several types of runegem inside. Include link");

        this.page("loot2",
                () -> BookSpotlightPageModel.create()
                        .withText(this.context().pageText())
                        .withItem(WotrItems.SKILL_THREAD)
        );
        this.pageText("{0} can be used to improve your abilities. Include link", this.itemLink(WotrItems.SKILL_THREAD)
        );

        this.page("loot3",
                () -> BookSpotlightPageModel.create()
                        .withTitle(this.context().pageTitle())
                        .withText(this.context().pageText())
                        .withItem(Items.DIAMOND_SWORD)
        );
        this.pageTitle("Gear");
        this.pageText(
                "Gear can be found in chests, with each gear piece having both additional implicit attributes and a number of sockets that can be filled to improve the gear. Include link");
    }

    @Override
    protected String entryName() {
        return "Loot!";
    }

    @Override
    protected String entryDescription() {
        return "Get it while you can!";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.CHEST);
    }

    @Override
    protected String entryId() {
        return ID;
    }
}

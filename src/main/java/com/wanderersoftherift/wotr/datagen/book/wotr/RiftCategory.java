package com.wanderersoftherift.wotr.datagen.book.wotr;

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.SingleBookSubProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookCategoryModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.book.BookCategoryBackgroundParallaxLayer;
import com.wanderersoftherift.wotr.datagen.book.wotr.rift.LootEntry;
import com.wanderersoftherift.wotr.datagen.book.wotr.rift.RiftMobEntry;
import com.wanderersoftherift.wotr.init.WotrItems;

public class RiftCategory extends CategoryProvider {
    public static final String ID = "rift";

    public RiftCategory(SingleBookSubProvider parent) {
        super(parent);
    }

    @Override
    protected String[] generateEntryMap() {
        // The entry map allows to define where entries are in relation to each other.
        // It is recommended to use a single character per entry
        // (if you think you are running out of characters .. any unicode character works.)
        //spotless:off
        return new String[] {
                "_________________",
                "_____l___________",
                "_________________",
                "_____m___________",
                "_________________"};
        //spotless:on
    }

    @Override
    protected void generateEntries() {
        var lootEntry = this.add(new LootEntry(this).generate('l'));

        var riftMobEntry = this.add(new RiftMobEntry(this).generate('m'))
                .withParent(this.parent(lootEntry).withLineReversed(true));
    }

    @Override
    protected BookCategoryModel additionalSetup(BookCategoryModel category) {
        return category.withBackgroundParallaxLayers(
                new BookCategoryBackgroundParallaxLayer(this.modLoc("textures/gui/parallax/flow/base.png"), 0.7f, -1),
                new BookCategoryBackgroundParallaxLayer(this.modLoc("textures/gui/parallax/flow/1.png"), 1f, -1),
                new BookCategoryBackgroundParallaxLayer(this.modLoc("textures/gui/parallax/flow/2.png"), 1.4f, -1)
        );
    }

    @Override
    protected String categoryName() {
        return "Into the Rift!";
    }

    @Override
    protected BookIconModel categoryIcon() {
        return BookIconModel.create(WotrItems.RIFT_KEY);
    }

    @Override
    public String categoryId() {
        return ID;
    }
}

package com.wanderersoftherift.wotr.datagen.book.wotr;

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.SingleBookSubProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookCategoryModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.book.BookCategoryBackgroundParallaxLayer;
import com.wanderersoftherift.wotr.datagen.book.wotr.intro.RiftKeyForgeEntry;
import com.wanderersoftherift.wotr.datagen.book.wotr.intro.RiftSpawnerEntry;
import com.wanderersoftherift.wotr.datagen.book.wotr.intro.WelcomeEntry;
import net.minecraft.world.item.Items;

public class IntroCategory extends CategoryProvider {
    public static final String ID = "intro";

    public IntroCategory(SingleBookSubProvider parent) {
        super(parent);
    }

    @Override
    protected String[] generateEntryMap() {
        // The entry map allows to define where entries are in relation to each other.
        // It is recommended to use a single character per entry
        // (if you think you are running out of characters .. any unicode character works.)
        //spotless:off
        return new String[] {
                "_____________________",
                "_____w_r___s_________",
                "_____________________",
                "_____________________",
                "_____________________",
                "_____________________",
                "_____________________",
                "_____________________" };
        //spotless:on
    }

    @Override
    protected void generateEntries() {
        var welcomeEntry = this.add(new WelcomeEntry(this).generate('w'));

        var riftKeyForgeEntry = this.add(new RiftKeyForgeEntry(this).generate('r'))
                .withParent(this.parent(welcomeEntry).withLineReversed(true));

        var riftSpawnerEntry = this.add(new RiftSpawnerEntry(this).generate('s'))
                .withParent(this.parent(riftKeyForgeEntry).withLineReversed(true));
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
        return "Intro To WotR";
    }

    @Override
    protected BookIconModel categoryIcon() {
        return BookIconModel.create(Items.WARPED_SIGN);
    }

    @Override
    public String categoryId() {
        return ID;
    }
}

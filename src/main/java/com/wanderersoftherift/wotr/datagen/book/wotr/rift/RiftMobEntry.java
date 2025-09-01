package com.wanderersoftherift.wotr.datagen.book.wotr.rift;

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookEntityPageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.item.Items;

public class RiftMobEntry extends EntryProvider {
    public static final String ID = "entity";

    public RiftMobEntry(CategoryProvider parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("entity", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText())
        );
        this.pageTitle("Mobs!");
        this.pageText("""
                You will come across various enemy mobs throughout the rift. Be prepared to defend yourself!
                """);

        this.page("entity1", () -> BookEntityPageModel.create()
                .withEntityName(this.context().pageTitle())
                .withEntityId("minecraft:zombie")
                .withScale(0.5f)
        );
        this.pageTitle("Mob Variants");

        this.page("entity2", () -> BookEntityPageModel.create()
                .withText(this.context().pageText())
                .withEntityId("minecraft:spider")
                .withScale(1f)
        );
        this.pageText("A sample entity page with automatic title.");
    }

    @Override
    protected String entryName() {
        return "Rift Mobs";
    }

    @Override
    protected String entryDescription() {
        return "Danger awaits you in the rift!";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.ZOMBIE_HEAD);
    }

    @Override
    protected String entryId() {
        return ID;
    }
}


/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.wanderersoftherift.wotr.datagen.book;

import com.klikli_dev.modonomicon.api.datagen.ModonomiconLanguageProvider;
import com.klikli_dev.modonomicon.api.datagen.SingleBookSubProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookModel;
import com.wanderersoftherift.wotr.datagen.book.wotr.IntroCategory;
import com.wanderersoftherift.wotr.datagen.book.wotr.RiftCategory;
import net.minecraft.resources.ResourceLocation;

public class WotrBook extends SingleBookSubProvider {

    public static final String ID = "wotr";

    public WotrBook(String modid, ModonomiconLanguageProvider lang) {
        super(ID, modid, lang);
    }

    @Override
    protected BookModel additionalSetup(BookModel book) {
        // if we want to handle a second language in here we can add book-related (not category or entry or page)
        // translations here
        // this.add(this.lang("ru_ru"), this.context().bookName(), "Демонстрационная книга");
        // this.add(lang("ru_ru"), this.context().bookTooltip(), "Книга для демонстрации и тестирования функций
        // \"Модономикона\".");

        return book.withModel(ResourceLocation.parse("modonomicon:modonomicon_purple"))
                .withBookTextOffsetX(5)
                .withBookTextOffsetY(0) // no top offset
                .withBookTextOffsetWidth(-5)
                .withAllowOpenBooksWithInvalidLinks(true);
    }

    @Override
    protected void registerDefaultMacros() {
        // currently no macros
    }

    @Override
    protected void generateCategories() {
        // for the two big categories we use the category provider
        var introCategory = this.add(new IntroCategory(this).generate());
        var riftCategory = this.add(new RiftCategory(this).generate());
    }

    @Override
    protected String bookName() {
        return "WotR Book";
    }

    @Override
    protected String bookTooltip() {
        return "A guide book to help you get started with Wanderers of the Rift.";
    }
}

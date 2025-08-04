package com.wanderersoftherift.wotr.gui.menu.character;

import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.MenuConstructor;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.Nullable;

/**
 * CharacterMenuItem provides the details for displaying and opening a submenu of the character menu
 *
 * @param name         The displayable name of the menu item
 * @param menuType     The type of the menu item
 * @param menuSupplier The constructor for the item's menu
 * @param relativeTo   An optional menu to place the item relative to as per the order hint
 * @param orderHint    A hint for how to place the menu item
 */
public record CharacterMenuItem(Component name, MenuType<?> menuType, MenuConstructor menuSupplier,
        @Nullable MenuType<?> relativeTo, OrderHint orderHint) {

    public CharacterMenuItem(Component name, MenuType<?> menuType, MenuConstructor menuSupplier) {
        this(name, menuType, menuSupplier, null, OrderHint.AFTER);
    }
}
